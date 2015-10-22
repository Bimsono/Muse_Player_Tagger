import java.util.ArrayList;

public class Tag {

	//TODO possible structures to hold children and parents
	private int id;
	private String name;
	private String description;	
	private boolean searched; 
	
	//Tag pulled from the database
	public Tag(String name, int id) throws IllegalArgumentException{ 
		//TODO Sanitize tag names
		this.name = name;
		this.id = id;
		if(!nameIsValid(name)){
			throw new IllegalArgumentException("Tag name invalid: \"" + name + "\"");
		}
	}
	
	public Tag(String name) throws IllegalArgumentException{
		this(name, DbManager.getTagId(name));
	}
	
	/**creates a TrackList containing every Track listed under This in the database, 
	 * then for every child This has, merges with the getTracks of the child.
	 * @return 
	 */
	public ArrayList<Track> getTracks(){
		ArrayList<Integer> visitedTags = new ArrayList<Integer>();
		return this.getTracks(visitedTags);
//		ArrayList<ArrayList<Track>> allTrackLists = new ArrayList<ArrayList<Track>>();
//		ArrayList<Tag> visitedTags = new ArrayList<Tag>();
//		
//		allTrackLists.add(DbManager.getTracks(this));
//		visitedTags.add(this);
//		
//		for(Tag child : this.getChildren()){
//			allTrackLists.add(child.getTracks());			
//		}
//		return TrackListController.merge(allTrackLists);
	}
	
	public ArrayList<Track> getTracks(ArrayList<Integer> visitedTags){
		ArrayList<ArrayList<Track>> allTrackLists = new ArrayList<ArrayList<Track>>();
		
		allTrackLists.add(DbManager.getTracks(this));
		visitedTags.add(this.getTagId());
		
		for(Tag child : this.getChildren()){
			if(!visitedTags.contains(child.getTagId())){
				allTrackLists.add(child.getTracks(visitedTags));
				visitedTags.add(child.getTagId());
			}						
		}
		
		return TrackListController.merge(allTrackLists);
	}
	
	//TODO addChild method
	public boolean addChild(String child){
		if( !nameIsValid(child) )
			return false;
		int childID = DbManager.getTagId(child);
		if( childID == -1 ){
			childID = (DbManager.insertTag(child)).getTagId();
		}
		boolean status = DbManager.insertParentTagLink(id, childID);	
		return status;
	}
	
	/**
	 * Add a parent tag to this tag. If tag not created. 
	 * 
	 * @param parent
	 * @return status
	 * 
	 */
	public boolean addParent(String parent){
		if( !nameIsValid(parent) )
			return false;
		int parentID = DbManager.getTagId(parent);
		if( parentID == -1 ){
			parentID = DbManager.insertTag(parent).getTagId();
		}
		boolean status = DbManager.insertParentTagLink(parentID, id);	
		return status;
	}
	
	//TODO delete Parent and child tags
	
	/** see Track.addTag
	 * avoid calling this method
	 * @param trackBeingAdded  
	 */
	public void addTrack(Track trackBeingAdded){
		trackBeingAdded.addTag(this.name);
	}
	
	/** see Track.removeTag
	 * avoid calling this method
	 * @param trackBeingRemoved
	 */
	public void removeTrack(Track trackBeingRemoved){
		trackBeingRemoved.removeTag(this);
	}
	
	public void removeParent(Tag parent){
		DbManager.removeParentTagLink(parent.getTagId(), this.id);
	}
	
	public void removeChild(Tag child){
		DbManager.removeParentTagLink(this.id, child.getTagId());
	}
	
	
	/**Searches the database to return the Tag whose name matches the passed String
	 * If there are multiple matches, return the first and throw exception
	 * @param name
	 * @return
	 */
	public static Tag getTagByName(String name){
		return null;
	}
	
	
	/**Searches the database to return the Tag whose uniqueIdentifier matches the passed String
	 * @param ID
	 * @return
	 */
	public static Tag getTagByID(String ID){
		return null;
	}	
	
	/**Creates tiered relationship between two Tags by adding the child to the list of the parent's children and vice versa in the database
	 * @param child is searched for whenever parent is
	 * @param parent is not searched for when child is
	 */
	public static void createRelationship(Tag child, Tag parent){
		
	}
	
	/**
	 * @return any Tags listed as children of This in the database.
	 */
	public ArrayList<Tag> getChildren(){
		return DbManager.getChildren(id);		
	}
	
	/**
	 * @return any Tags listed as parents of This in the database.
	 */
	public ArrayList<Tag> getParents(){
		return DbManager.getParents(id);
	}
	
	public int getTagId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean setName(String newName){
		//TODO implement validation
		if(nameIsValid(newName)){
			name = newName;
			DbManager.setTagName(newName, id);
			return true;
		}
		else
			return false;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String newDescription){
		description = newDescription;
	}
	
	private boolean nameIsValid(String name){
		//space, comma, dash, "not", parentheses, empty/whitespace
		String trimName = name.trim();
		if(name.equalsIgnoreCase("not") || name.equalsIgnoreCase("") )
			return false;
		
		String regex_begin ="[\\(,\\-\\s\\)].+"; 
		String regex_middle = ".+[\\s\\,\\(\\)].+";
		String regex_end = ".+[\\,\\(\\)]$";
		
		if(trimName.matches(regex_begin))
			return false;
		else if(trimName.matches(regex_middle))
				return false;
		else if(trimName.matches(regex_end))
			return false;
		
		return true;
	}
	
	/*
	public static void main(String[] args){
		//DbManager.setupConnection();
		
		//rock, fast, pants, pop-punk, 
		System.out.println("nameisValid: " + "rock" + "=" + nameIsValid("rock"));
		
		System.out.println("nameisValid: " + "fast" + "=" + nameIsValid("fast"));
		
		System.out.println("nameisValid: " + "pop-punk" + "=" + nameIsValid("pop-punk"));
		
		System.out.println("nameisValid: " + "rock" + "=" + nameIsValid("rock"));
		
		System.out.println("nameisValid: " + "<empty-String>" + "=" + nameIsValid(""));
		
		System.out.println("nameisValid: " + "not" + "=" + nameIsValid("not"));
		
		System.out.println("nameisValid: " + "trip" + "=" + nameIsValid("trip"));
		
		System.out.println("nameisValid: " + "NoT" + "=" + nameIsValid("NoT"));
		
		System.out.println("nameisValid: " + "-EMO" + "=" + nameIsValid("-EMO"));
		
		System.out.println("nameisValid: " + ",me" + "=" + nameIsValid(",me"));
		
		System.out.println("nameisValid: " + "my,name" + "=" + nameIsValid("my,name"));
		
		System.out.println("nameisValid: " + "---Hi-me" + "=" + nameIsValid("---Hi-me"));
		
		System.out.println("nameisValid: " + "()" + "=" + nameIsValid("()"));
		
		System.out.println("nameisValid: " + "pop--" + "=" + nameIsValid("pop--"));	
		
		System.out.println("nameisValid: " + "pot-luck" + "=" + nameIsValid("pot-luck"));
		
		System.out.println("nameisValid: " + "(lookhere" + "=" + nameIsValid("(lookhere"));
		
		System.out.println("nameisValid: " + "lookhere)" + "=" + nameIsValid("lookhere)"));
		
		
	}
	*/
	
	

}
