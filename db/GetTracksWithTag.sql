SELECT ttt.TrackId,
	ttt.FileLocation
FROM Tag t
JOIN TrackTag tt ON tt.TagId = t.TagId
JOIN Track ttt ON ttt.TrackId = tt.TrackId
WHERE t.TagId = ?;