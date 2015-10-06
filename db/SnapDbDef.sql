	CREATE TABLE IF NOT EXISTS Track(
		TrackId INTEGER PRIMARY KEY,
		Name TEXT,
		FileLocation TEXT UNIQUE NOT NULL);
		
	CREATE TABLE IF NOT EXISTS Tag(
		TagId INTEGER PRIMARY KEY,
		Name TEXT UNIQUE NOT NULL,
		Description TEXT);
		
	CREATE TABLE IF NOT EXISTS TrackTag(
		TrackId INTEGER,
		TagId INTEGER,
		FOREIGN KEY(TrackId) REFERENCES Track(TrackId),
		FOREIGN KEY(TagId) REFERENCES Tag(TagId));
		
	CREATE TABLE IF NOT EXISTS ParentTagLink(
		ParentTagId INTEGER,
		ChildTagId INTEGER,
		FOREIGN KEY(ParentTagId) REFERENCES Tag(TagId),
		FOREIGN KEY(ChildTagId) REFERENCES Tag(TagId));
