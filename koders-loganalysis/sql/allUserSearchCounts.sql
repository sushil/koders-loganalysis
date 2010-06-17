select distinct UserID, COALESCE(scount, 0) as scount from UserActivityLog
left outer join
(select UserID as uid, count(ID) as scount from UserActivityLog
  where Type = 1000 
  group by UserID) as sql2
on UserID = sql2.uid