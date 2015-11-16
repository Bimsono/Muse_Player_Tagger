import java.awt.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.sun.glass.ui.Application;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
//import com.jgoodies.forms.layout.FormLayout;
//import com.jgoodies.forms.layout.ColumnSpec;
//import com.jgoodies.forms.layout.RowSpec;
//import net.miginfocom.swing.MigLayout;

//import java.util.function.*;
public class MainView {

	public static ArrayList<Track> activeTrackList;
	public static ArrayList<Tag> activeTags = new ArrayList<Tag>();
	public static ArrayList<Search> savedSearches = new ArrayList<Search>();
	public static ArrayList<Tag> parents;
	public static ArrayList<Tag> parent;
	
	public static ArrayList<Track> selectedTracks = new ArrayList<Track>();
	public static Tag selectedTag;
	
	public static MetadataComparator trackComparator = new MetadataComparator("Date Added");
	
	private static JFrame frmSnap;
	
	private static Color frameBG = new Color(32, 32, 32);
	private static Color sideBG = new Color(64, 64, 64);
	private static Color middleBG = new Color(38,38,38);
	
	private static int width = 1000;
	private static int height = 700;
	
	private static JTextField tagInfo;
	private static JTextField searchField;
	private static JTextField addTagField;
	
	private static JTable trackTable;
	private static JTable tagTable;
	private static JTableHeader header;
	
	private static JTable savedSearchTable;
	
	private static JPanel leftPanel;
	private static JPanel middlePanel;
	private static JPanel rightPanel;
	private static JPanel addTagPanel;
	private static JPanel tagInfoPanel;
	private static JPanel searchPanel;
	private static JPanel playerPanel;
	private static JPanel songPanel;
	
	private static DefaultTableModel trackModel;
	private static DefaultTableModel tagModel;
	
	private static JButton btnImport;
	private static JButton btnAddTag;
	private static JButton btnDeleteTag;
	private static JButton btnMusicPlayer;
	private static JButton btnSave;

	private static JLabel lblMenu;
	private static JLabel lblSongList;
	private static JLabel lblDetails;
	private static JLabel lblTagName;
	
	private static GroupLayout groupLayout;
	private static GroupLayout gl_leftPanel;
	
	private static JScrollPane scrollPane;
	private static JButton searchButton;
	
	//TODO: Paremeterize
	private static JList parentList;
	private static JButton btnX;
	private static JPanel tagButtonPanel;
	
	private JFXPanel fxPanel;
	
	public static void main(String[] args) {
		SnapMain();
		JFXPanel fxPanel= new JFXPanel();
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
					window.frmSnap.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	
            	PlayBackApplication snapPlayBack = new PlayBackApplication();
            	Scene scene =  snapPlayBack.snapPlayBackSetup(trackModel, trackTable, selectedTracks, activeTrackList);
        		fxPanel.setScene(scene);                
                middlePanel.add( fxPanel, BorderLayout.SOUTH);
            }
       });



	}
	
	
	
	/**
	 * Create the application.
	 */
	public static void SnapMain() {
		initialize();
		
		activeTrackList = DbManager.getLibrary();
		savedSearches = DbManager.getSavedSearches();
		System.out.println("savedSearches.size() in SnapMain: " + savedSearches.size());
		
		trackModel = (DefaultTableModel) trackTable.getModel();
		updateTrackTable(false);
		updateSavedSearchTable();
		
//		for(int i = 0; i < activeTrackList.size(); i++){
//			Track currTrack = activeTrackList.get(i);
//			trackModel.addRow(new Object[]{currTrack.getTitle(), currTrack.getArtist(), currTrack.getAlbum(), currTrack.getGenre()});
//		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
        /** Make dummy mp3s */
		//Util_DemoMP3.copyMP3(100 );
        
		DbManager.setupConnection();
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		frmSnap = new JFrame();
		tagTable = new JTable();
		
		savedSearchTable = new JTable();
		
		lblMenu = new JLabel("Menu");
		lblSongList = new JLabel("Song List");
		lblDetails = new JLabel("Details");
		
		leftPanel = new JPanel();
		middlePanel = new JPanel();
		rightPanel = new JPanel();
		addTagPanel = new JPanel();
		tagInfoPanel = new JPanel();
		searchPanel = new JPanel();
		songPanel = new JPanel();
		//playerPanel = new JPanel();
		
		lblTagName = new JLabel("Tag Information");
		
		btnAddTag = new JButton("+");
		btnDeleteTag = new JButton("Delete Tag");
		btnImport = new JButton("Import");
		btnSave = new JButton("Save");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String text = searchField.getText();
				if(text != null && !text.isEmpty()) {
			    	Search search = new Search(text);
			    	
			    	activeTrackList = search.executeSearch();
					
					System.out.println("MainView:Initialize: (coming from executeSearch())activeTrackList size: "+activeTrackList.size());
					/***************DEBUG:false param means not importToSnap use. Means don't overwrite activeTrackList************/
					updateTrackTable(false);
					search.favoriteSearch();
					savedSearches = DbManager.getSavedSearches();
					for(int i = 0; i < savedSearches.size(); i++){
						System.out.println(savedSearches.get(i).getSearchText());
					}
					System.out.println("reached e");
				}
			}
		});
		
		//TODO remove this
		btnMusicPlayer = new JButton("Music Player");
		
		Action addTagaction = new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e) {
				for(Track track : selectedTracks){
					track.addTag(addTagField.getText());
				}
				updateTagTable();
				addTagField.setText("");
			}
		};
		
		addTagField = new JTextField();
		addTagField.addActionListener(addTagaction);
		
		
		
		tagInfo = new JTextField();
		searchField = new JTextField();
		
		
		gl_leftPanel = new GroupLayout(leftPanel);
		groupLayout = new GroupLayout(frmSnap.getContentPane());
		
		frmSnap.setTitle("Snap");
		frmSnap.getContentPane().setBackground(frameBG);
		
		lblMenu.setHorizontalAlignment(SwingConstants.LEFT);
		lblMenu.setBackground(Color.GRAY);
		lblMenu.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblMenu.setForeground(Color.WHITE);
		lblMenu.setOpaque(true);
		
		lblSongList.setHorizontalAlignment(SwingConstants.CENTER);
		lblSongList.setBackground(Color.GRAY);
		lblSongList.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblSongList.setForeground(Color.WHITE);
		lblSongList.setOpaque(true);
		
		lblDetails.setOpaque(true);
		lblDetails.setHorizontalAlignment(SwingConstants.LEFT);
		lblDetails.setForeground(Color.WHITE);
		lblDetails.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblDetails.setBackground(Color.GRAY);
		
		leftPanel.setBackground(sideBG);
		
		middlePanel.setBorder(new LineBorder(Color.DARK_GRAY));
		middlePanel.setForeground(Color.WHITE);
		middlePanel.setBackground(frameBG);

		
		rightPanel.setBackground(sideBG);
		rightPanel.setPreferredSize(new Dimension(180, height));
		
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(middlePanel, GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblMenu, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSongList, GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblDetails, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMenu)
						.addComponent(lblSongList)
						.addComponent(lblDetails))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
								.addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, 556, Short.MAX_VALUE))
							.addGap(12))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(middlePanel, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
							.addContainerGap())))
		);
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		lblTagName.setForeground(Color.LIGHT_GRAY);
		lblTagName.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblTagName.setHorizontalAlignment(SwingConstants.CENTER);
		rightPanel.add(lblTagName, BorderLayout.NORTH);
		
		addTagPanel.setForeground(Color.WHITE);
		addTagPanel.setBackground(Color.DARK_GRAY);
		rightPanel.add(addTagPanel, BorderLayout.SOUTH);
		
		btnAddTag.setEnabled(false);
		btnAddTag.setForeground(Color.BLACK);
		btnAddTag.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnAddTag.addActionListener(addTagaction);
		addTagPanel.setLayout(new BorderLayout(0, 0));
		
		addTagField.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		addTagField.setHorizontalAlignment(SwingConstants.CENTER);
		addTagField.setEnabled(false);
		addTagField.setColumns(10);
		addTagPanel.add(addTagField);
		btnAddTag.setPreferredSize(new Dimension(60, 30));
		btnAddTag.setBackground(Color.DARK_GRAY);
		addTagPanel.add(btnAddTag, BorderLayout.EAST);

		tagInfoPanel.setForeground(Color.WHITE);
		tagInfoPanel.setBackground(Color.DARK_GRAY);
		rightPanel.add(tagInfoPanel, BorderLayout.CENTER);
		tagInfoPanel.setLayout(new BorderLayout(0, 0));
		
		tagInfo.setHorizontalAlignment(SwingConstants.CENTER);
		tagInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		tagInfoPanel.add(tagInfo, BorderLayout.NORTH);
		tagInfo.setForeground(Color.WHITE);
		tagInfo.setBackground(Color.DARK_GRAY);
		tagInfo.setColumns(13);
		tagInfo.setEditable(false);
		
		tagTable.setShowGrid(false);
		tagTable.setForeground(Color.LIGHT_GRAY);
		tagTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Tag"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tagTable.getColumnModel().getColumn(0).setResizable(false);
		tagTable.getColumnModel().getColumn(0).setMaxWidth(100);
		tagTable.setBackground(Color.DARK_GRAY);
		tagTable.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		tagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tagTable.setShowVerticalLines(false);
		tagTable.setPreferredSize(new Dimension(180, height));
		tagInfoPanel.add(tagTable, BorderLayout.CENTER);
		
		tagTable.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent click) {
		        if (click.getClickCount() == 2) {
		            System.out.println(tagTable.getSelectedRow());
		            
		            
		            
		            selectedTag = activeTags.get(tagTable.getSelectedRow());
					parents = selectedTag.getParents();
					parent = selectedTag.getChildren();
					
					JPanel editTagPanel = new JPanel();
					JTextField newTagNameField = new JTextField();
					newTagNameField.setText(selectedTag.getName());
					DefaultListModel parentModel = new DefaultListModel();
					final String[] parentString = new String[parents.size()];
					
					JTextField newParentField = new JTextField();
					String[] childrenString = new String[parent.size()];
					
					DefaultListModel childModel = new DefaultListModel();
					JTextField newChildField = new JTextField();
					
					for(int i = 0; i < parents.size(); i++){
						parentString[i] = parents.get(i).getName();
					}
					
					for(int i = 0; i < parent.size(); i++){
						childrenString[i] = parent.get(i).getName();
					}
				
					parentList = new JList(parentString);
					parentList = new JList(childrenString);
					
					parentList.addMouseListener(new MouseAdapter() {
					    public void mouseClicked(MouseEvent evt) {
					    	System.out.println("Tag " + parents.get(parentList.getSelectedIndex()).getName());
					        if (evt.getClickCount() == 2) {
					            // Double-click detected
					        	selectedTag.removeParent(parents.get(parentList.getSelectedIndex()));
					        	
					        }
					    }
					});
					
					parentList.addMouseListener(new MouseAdapter() {
					    public void mouseClicked(MouseEvent evt) {
					    	System.out.println("Tag " + parent.get(parentList.getSelectedIndex()).getName());
					        if (evt.getClickCount() == 2) {
					            // Double-click detected
					        	selectedTag.removeChild(parent.get(parentList.getSelectedIndex()));
					        }
					    }
					});
					
					Object[] message = {
					    "Rename tag:", newTagNameField,
					    "Double click to remove parent",parentList,
					    "Add parent tag:", newParentField,
					    "Double click to remove child",parentList,
					    "Add child tag:", newChildField
					    
					};
					

					int option = JOptionPane.showConfirmDialog(null, message, "Edit Tag", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (option == JOptionPane.OK_OPTION) {
					    if (selectedTag.setName(newTagNameField.getText())) {
					        System.out.println("successful");
					    }
					    if (selectedTag.addParent(newParentField.getText())){
					    	
					        
					    }
					    if (selectedTag.addChild(newChildField.getText())){
					    	
					    }
					} else {
					    System.out.println("canceled");
					}
					
					updateTagTable();
		        }
		    }
		});
		
		btnDeleteTag.setEnabled(false);
		btnDeleteTag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
	        	
				selectedTag = activeTags.get(tagTable.getSelectedRow());
				
				for(Track track : selectedTracks){
					track.removeTag(selectedTag);
				}
				
				updateTagTable();
			}
		});
		
		tagInfoPanel.add(btnDeleteTag, BorderLayout.SOUTH);
		middlePanel.setLayout(new BorderLayout(0, 0));
		
		tagTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	btnDeleteTag.setEnabled(true);
	        }
	    });
		
		searchPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		searchPanel.setBackground(middleBG);
		middlePanel.add(searchPanel, BorderLayout.NORTH);
		searchPanel.setLayout(new BorderLayout(0, 0));
		
		searchField.setColumns(30);
		searchPanel.add(searchField);
		
		Action searchAction = new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Search search = new Search(searchField.getText());
		    	
				activeTrackList = search.executeSearch();
				
				Collections.sort(activeTrackList, trackComparator);
				System.out.println("MainView:Initialize: (coming from executeSearch())activeTrackList size: "+activeTrackList.size());
				/***************DEBUG:false param means not importToSnap use. Means don't overwrite activeTrackList************/
				updateTrackTable(false);//call here overwrites what is correctly in activeTrackList with the entire library again
			}
		};
		
		searchField.addActionListener(searchAction);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(searchAction);
		searchPanel.add(searchButton, BorderLayout.EAST);
		
		btnX = new JButton("X");
		btnX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchField.setText("");
				
				activeTrackList = DbManager.getLibrary();
				
				updateTrackTable(true);
			}
		});
		searchPanel.add(btnX, BorderLayout.WEST);
		
		tagButtonPanel = new JPanel();
		searchPanel.add(tagButtonPanel, BorderLayout.SOUTH);
		tagButtonPanel.setOpaque(true);
		tagButtonPanel.setBackground(Color.DARK_GRAY);
		tagButtonPanel.setPreferredSize(new Dimension(556, 40));
		tagButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		//playerPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		//playerPanel.setBackground(middleBG);
		//middlePanel.add(playerPanel, BorderLayout.SOUTH);
		
		//playerPanel.add(btnMusicPlayer);
		
		middlePanel.add(songPanel, BorderLayout.CENTER);
		
		songPanel.setOpaque(true);
		songPanel.setBackground(middleBG);
		songPanel.setLayout(new BoxLayout(songPanel, BoxLayout.X_AXIS));
		
		scrollPane = new JScrollPane();
		scrollPane.setOpaque(true);
		songPanel.add(scrollPane);
		
		trackTable = new JTable();
		scrollPane.setViewportView(trackTable);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(middleBG);
		scrollPane.getViewport().setBackground(middleBG);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		trackTable.setAlignmentY(Component.TOP_ALIGNMENT);
		trackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		trackTable.setShowVerticalLines(false);
		trackTable.setShowHorizontalLines(false);
		trackTable.setShowGrid(false);
		trackTable.setForeground(Color.WHITE);
		trackTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Name", "Artist", "Album", "Date Added"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		trackTable.getColumnModel().getColumn(0).setPreferredWidth(180);
		trackTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		trackTable.getColumnModel().getColumn(2).setPreferredWidth(150);
		trackTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		trackTable.setOpaque(true);
		trackTable.setBackground(middleBG);
		
		trackTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {

	        	if ( !event.getValueIsAdjusting()) {	        		
	        		selectedTracks.clear();
	        		System.out.println("trackTable.getSelectedRows size: "+ trackTable.getSelectedRows().length);
	        		

	        		for(int row : trackTable.getSelectedRows()){
	        			selectedTracks.add(activeTrackList.get(row));
	        			System.out.println("MainView: Songs in Selected Rows via activeTrackList: " + activeTrackList.get(row).getTitle());
	        		}
	        		updateTagTable();
	        	}
	        			
	        	btnAddTag.setEnabled(true);
	        	addTagField.setEnabled(true);
	            //System.out.println(songTable.getValueAt(songTable.getSelectedRow(), 0).toString());
	        }
	    });
		
		trackTable.getTableHeader().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				String field = trackTable.getModel().getColumnName(trackTable.columnAtPoint(e.getPoint()));
				
				System.out.println("Clicked column: " + field);
				
				trackComparator.setField(field);
				
				updateTrackTable(false);				
			}
		});
				
		
		trackTable.setRowSelectionAllowed(true);
		trackTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		header = trackTable.getTableHeader();
	    header.setBackground(middleBG);
	    header.setForeground(Color.white);
		
		
		btnImport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				TrackListController.importToSnap();
				updateTrackTable(true);
			}
		});
		btnImport.setForeground(Color.GRAY);
		btnImport.setBackground(Color.DARK_GRAY);
		btnImport.setHorizontalAlignment(SwingConstants.LEFT);
		btnImport.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		
		btnSave.setForeground(Color.GRAY);
		btnSave.setHorizontalAlignment(SwingConstants.LEFT);
		btnSave.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		
		savedSearchTable.setShowGrid(false);
		savedSearchTable.setForeground(Color.LIGHT_GRAY);
		savedSearchTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Tag"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		
		savedSearchTable.setOpaque(true);
		savedSearchTable.setBackground(middleBG);
		
		savedSearchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {

	        	if ( !event.getValueIsAdjusting()) {	
	        		System.out.println(savedSearchTable.getSelectedRow());
	        		Search search = savedSearches.get(savedSearchTable.getSelectedRow());
	        		searchField.setText(search.getSearchText());
	        		
	        		activeTrackList = search.executeSearch();
	        		
	        		updateTrackTable(false);
//	        		for(int row : trackTable.getSelectedRows()){
//	        			selectedTracks.add(activeTrackList.get(row));
//	        			System.out.println("MainView: Songs in Selected Rows via activeTrackList: " + activeTrackList.get(row).getTitle());
//	        		}
//	        		updateTagTable();
	        	}
	        }
	    });
		
		
		gl_leftPanel.setHorizontalGroup(
			gl_leftPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_leftPanel.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_leftPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSave)
						.addComponent(btnImport)
						.addComponent(savedSearchTable))
					.addGap(100))
		);
		gl_leftPanel.setVerticalGroup(
			gl_leftPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_leftPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(btnImport)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSave)
					.addComponent(savedSearchTable)
					.addGap(487))
		);
		leftPanel.setLayout(gl_leftPanel);
		frmSnap.getContentPane().setLayout(groupLayout);
		frmSnap.setBounds((screen.width/2)-(width/2), (screen.height/2)-(height/2), width, height);
		frmSnap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	
	
	
	
	/***********************DEBUG:Added parameter for importToSnap button and Search button to both work using this call*********/
	private static void updateTrackTable(boolean importToSnap){
		/*********************DEBUG:PROBLEM DISPLAYING SEARCH RESULT*****************/
		 //At this point activeTrackList was with searched result but you call getLibrary to overwrite that correct search.
		 //Therefore, the search result isn't displayed to user
		/******************DEBUG*************/
		if(importToSnap)
			
			activeTrackList = DbManager.getLibrary();
		
		/****************************DEBUG*****************/
//		System.out.println("MainView:updateTrackTable: activeTrackList_Size: "+ activeTrackList.size());
//		System.out.println("MainView:updateTrackTable: activeTrackList_contents: "+ activeTrackList.toString()	);
		
		clearTable(trackTable);
		
		Collections.sort(activeTrackList, trackComparator);
		for(int i = 0; i < activeTrackList.size(); i++){
			Track currTrack = activeTrackList.get(i);
			Date inDate = new Date(currTrack.getImportDate().getTime() - currTrack.getImportDate().getTimezoneOffset()*60000);
			String dateString = ""+ inDate.getMonth() + "/" + inDate.getDate() + "/" + (inDate.getYear()%100) + " " + inDate.getHours() + ":" + inDate.getMinutes();
			trackModel.addRow(new Object[]{currTrack.getTitle(), currTrack.getArtist(), currTrack.getAlbum(), dateString});
			
			System.out.println("MainView: trackModel #rows: " + trackModel.getRowCount());//ME
			
			
		}
	}
	
	private static void updateSavedSearchTable(){
		DefaultTableModel searchModel = (DefaultTableModel) savedSearchTable.getModel();
		System.out.println("savedSearches.size() = " + savedSearches.size());
		for(int i = 0; i < savedSearches.size(); i++){
			System.out.println("butt: " + savedSearches.get(i).getSearchText());
			searchModel.addRow(new Object[]{savedSearches.get(i).getSearchText()});
		}
	}
	
	private static void updateTagTable() {
		// TODO Auto-generated method stub
		tagModel = (DefaultTableModel) tagTable.getModel();
    	
    	//clears row to be ready to display new set of tags
    	int rows = tagModel.getRowCount(); 
    	for(int i = rows - 1; i >=0; i--){
    		tagModel.removeRow(i); 
    	}
    	
    	tagButtonPanel.removeAll();
    	tagButtonPanel.updateUI();
    	
    	activeTags = TrackListController.getCommonTags(selectedTracks);
    	
    	//populates rows with tags of selected track
    	for(int i = 0; i < activeTags.size(); i++){
    		tagModel.addRow(new Object[]{activeTags.get(i).getName()});
    		JButton newTagButton = addTagButton(activeTags.get(i));
    		tagButtonPanel.add(newTagButton);
    	}
    	
    	//tagInfo.setText(trackTable.getValueAt(trackTable.getSelectedRow(), 0).toString());
	}
	
	
	
	/**
	 * Clears all rows of table
	 * @param T 
	 */
	private static void clearTable(JTable T){
		
		DefaultTableModel currModel = (DefaultTableModel) T.getModel();
		
		tagButtonPanel.removeAll();
		
		int rows = currModel.getRowCount(); 
    	for(int i = rows - 1; i >=0; i--){
    		currModel.removeRow(i); 
    	}	
	}
	
	public ArrayList<Track> getActiveTrackList(){
		return activeTrackList;
	}
	
	public JTable getTrackTable(){
		return trackTable;
	}
	
	private static JButton addTagButton(Tag curTag){
		ImageIcon xIcon = new ImageIcon("src/xIcon.png","x");
		JButton newTagButton = new JButton(xIcon);
		newTagButton.setText(curTag.getName());
		
		//Create the popup menu.
        final JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(new AbstractAction("Delete") {
            public void actionPerformed(ActionEvent e) {

				
				for(Track track : selectedTracks){
					track.removeTag(curTag);
				}
				
				updateTagTable();
            }
        }));

		
		newTagButton.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent click) {
		    	if (SwingUtilities.isRightMouseButton(click)){
		    		popup.show(click.getComponent(), click.getX(), click.getY());
		    	}else if (SwingUtilities.isLeftMouseButton(click)){
					parent = curTag.getParents();
					
					JPanel editTagPanel = new JPanel();
					JTextField newTagNameField = new JTextField();
					newTagNameField.setText(curTag.getName());
					
					String[] parentString = new String[parent.size()];
					
					DefaultListModel childModel = new DefaultListModel();
					JTextField newParentField = new JTextField();
					
					
					for(int i = 0; i < parent.size(); i++){
						parentString[i] = parent.get(i).getName();
					}
					parentList = new JList(parentString);
					
					parentList.addMouseListener(new MouseAdapter() {
					    public void mouseClicked(MouseEvent evt) {
					    	System.out.println("Tag " + parent.get(parentList.getSelectedIndex()).getName());
					        if (evt.getClickCount() == 2) {
					            // Double-click detected
					        	curTag.removeParent(parent.get(parentList.getSelectedIndex()));
					        }
					    }
					});
					
					Object[] message = {
					    "Rename tag:", newTagNameField,
					    "Double click to remove parent",parentList,
					    "Add parent tag:", newParentField
					    
					};
					

					int option = JOptionPane.showConfirmDialog(null, message, "Edit Tag", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (option == JOptionPane.OK_OPTION) {
					    if (curTag.setName(newTagNameField.getText())) {
					        System.out.println("successful");
					    }
					    if (curTag.addParent(newParentField.getText())){
					    	
					    }
					} else {
					    System.out.println("canceled");
					}
					
					updateTagTable();
		    	}
		    }
		});
		
		return newTagButton;
	}
	
	
	/** Adds all .mp3 files in specified folder into the Library and updates activeTrackList
	 * @param folderLocation location of folder containing files to import 
	 */
	private void importFiles(String folderLocation){
		
	}
	
	/**Converts the search parameters passed by the user into a Search object and then sets the activeTracklist to the results of executing the search
	 * @param searchParams
	 */
	private void search(String searchParams){
		
	}	
	
	/**resets the activeTrackList to the whole Library
	 * 
	 */
	private void clearSearch(){
		
	}
}

