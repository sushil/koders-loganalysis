-- count distinct Files that were reused (downloaded)
-- select count(distinct FileHash) from ReuseLog 
-- #result:   1,126,384 distinct files that were downloaded


-- count reuse activity
-- select count(ID) from UserActivityLog where Type = 1001
-- #result:  5,072,045 instances of downloads


-- count search activity
-- select count(ID) from UserActivityLog where Type = 1000
-- #result:  5,215,207 instances of searches


-- count all distinct search activities for java
/*
select count(A.ID) from 
UserActivityLog as A, SearchLog as S
where  
     (A.Type=1000 AND S.ID = A.ActivityID)
  AND S.Language = 'java'
*/
-- #result:   944,024


-- count search activities that were recorded to lead (can be traced) to downloads
-- Some search activities are not correctly associted with downloads. This is the 
-- limitation of the data set (or the logging process)
-- while a particular search activity might not actually have led to a download
/*
select count(A.ID) from 
ReuseLog as R, UserActivityLog as A, SearchLog as S
where  
     (A.Type=1000 AND S.ID = A.ActivityID)
 AND (R.SearchLogID = S.ID)
*/
-- #result: 1,055,443


-- count all search that were recorded to lead (can be traced) to downloads
-- restricted to Java
/*
select count(A.ID) from 
ReuseLog as R, UserActivityLog as A, SearchLog as S
where  
     (A.Type=1000 AND S.ID = A.ActivityID)
 AND (R.SearchLogID = S.ID)
 AND S.Language = 'java'
*/
-- #result: 224,791


-- count downloads that can be traced back to search
-- Some downloads might not be associated with a query
-- for example, a user might end up at a file by navigating
-- files in the project.
-- While in some cases the system might have missed the 
-- correct link too.
/*
select count(A.ID) from 
ReuseLog as R, UserActivityLog as A, SearchLog as S
where  
     (A.Type=1001 AND R.ID = A.ActivityID)
 AND (R.SearchLogID = S.ID)
*/
-- #result: 680,129


-- count downloads that can be traced back to search
-- restricted to Java
/*
select count(A.ID) from 
ReuseLog as R, UserActivityLog as A, SearchLog as S
where  
     (A.Type=1001 AND R.ID = A.ActivityID)
 AND (R.SearchLogID = S.ID)
 AND S.Language = 'java'
*/
-- #result: 139,288

-- count total activities
-- select count(ID) from UserActivityLog;
-- #result:	10,287,252

-- count total users in the log
-- select count(DISTINCT UserID) from UserActivityLog
-- #result:	3,187,969

-- count total distinct users from all search activity
-- select count(DISTINCT UserID) from UserActivityLog where Type = 1000
-- #result: 1,884,326

-- count total distinct users from all download activity
-- select count(DISTINCT UserID) from UserActivityLog where Type = 1001
-- #result: 1,975,303


-- count distinct users in all download activities that could be traced
-- back to search activity
/* 
select count(DISTINCT A.UserID) 
from 
	UserActivityLog as A, ReuseLog as R, SearchLog as S
where 
	A.Type = 1001
AND A.ActivityID = R.ID
AND S.ID = R.SearchLogID
*/
-- #result: 265,858


-- count users who search only
-- these probably are robots/system
/*
-- see rows count in the result
select DISTINCT UserID from UserActivityLog 
where 
	Type = 1000
AND UserID NOT IN (select DISTINCT UserID from UserActivityLog where Type = 1001)
*/
-- #result: 1,212,666


-- count users who download only
-- these probably are robots/system
-- or just a random visitor who never came back
/*
select DISTINCT UserID from UserActivityLog 
where 
	Type = 1001
AND UserID NOT IN (select DISTINCT UserID from UserActivityLog where Type = 1000)
*/
-- #result: 1,303,643


-- count users who search and download as well
/*
select DISTINCT UserID from UserActivityLog
where 
	 UserID IN (select DISTINCT UserID from UserActivityLog where Type = 1001)
AND  UserID IN (select DISTINCT UserID from UserActivityLog where Type = 1000)
*/
-- #result: 671,660


-- count total distinct users from all search activity for java only
/*
select count(DISTINCT U.UserID) 
from 
	SearchLog as S, UserActivityLog as U
where 
	U.Type = 1000
AND S.ID = U.ActivityID
AND S.Language = 'java'
*/
-- #result: 302,018


-- count total distinct users from all search activity for java only
-- and restrict that to users that do both search and download
/*
select count(DISTINCT U.UserID) 
from 
	SearchLog as S, UserActivityLog as U
where 
	U.Type = 1000
AND S.ID = U.ActivityID
AND S.Language = 'java'
AND U.UserID IN
	(
		select DISTINCT UserID from UserActivityLog
		where 
			UserID IN (select DISTINCT UserID from UserActivityLog where Type = 1001)
		AND	UserID IN (select DISTINCT UserID from UserActivityLog where Type = 1000)
	)
*/
-- #result: 194,272


-- count distinct users in all download activities that could be traced
-- back to search activity for java only
/* 
select count(DISTINCT A.UserID) 
from 
	UserActivityLog as A, ReuseLog as R, SearchLog as S
where 
	A.Type = 1001
AND A.ActivityID = R.ID
AND S.ID = R.SearchLogID
AND S.Language = 'java'
*/
-- #result: 53,364