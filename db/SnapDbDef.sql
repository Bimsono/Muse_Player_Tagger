	CREATE TABLE IF NOT EXISTS Track(
		TrackId INTEGER PRIMARY KEY,
		Name TEXT,
		FileLocation TEXT UNIQUE NOT NULL,
		CreatedDate DATETIME DEFAULT (DATETIME('now')));
		
	CREATE TABLE IF NOT EXISTS Tag(
		TagId INTEGER PRIMARY KEY,
		Name TEXT UNIQUE NOT NULL,
		Description TEXT,
		CreatedDate DATETIME DEFAULT (DATETIME('now')) );
		
	CREATE TABLE IF NOT EXISTS TrackTag(
		TrackId INTEGER,
		TagId INTEGER,
		CreatedDate DATETIME DEFAULT (DATETIME('now')),
		FOREIGN KEY(TrackId) REFERENCES Track(TrackId),
		FOREIGN KEY(TagId) REFERENCES Tag(TagId),
		PRIMARY KEY(TrackId, TagId) );
		
	CREATE TABLE IF NOT EXISTS ParentTagLink(
		ParentTagId INTEGER,
		ChildTagId INTEGER,
		CreatedDate DATETIME DEFAULT (DATETIME('now')),
		FOREIGN KEY(ParentTagId) REFERENCES Tag(TagId),
		FOREIGN KEY(ChildTagId) REFERENCES Tag(TagId),
		PRIMARY KEY(ParentTagId, ChildTagId) );

	CREATE TABLE IF NOT EXISTS Search(
		SearchId INTEGER PRIMARY KEY,
		SearchText TEXT NOT NULL,
		SubSearchId INTEGER,
		CreatedDate DATETIME DEFAULT (DATETIME('now')),
		FOREIGN KEY(SubSearchId) REFERENCES Search(SearchId) ON DELETE CASCADE );
		
	CREATE TABLE IF NOT EXISTS SearchedTag(
		SearchId INTEGER,
		TagId INTEGER,
		IsIncluded BOOLEAN NOT NULL,
		CreatedDate DATETIME DEFAULT (DATETIME('now')),
		FOREIGN KEY(SearchId) REFERENCES Search(SearchId) ON DELETE CASCADE,
		FOREIGN KEY(TagId) REFERENCES Tag(TagId),
		PRIMARY KEY(SearchId, TagId) );