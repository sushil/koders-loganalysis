select UserActivityLog.ID, Timestamp, UserID, Terms
from SearchLog, UserActivityLog
where ActivityID = SearchLog.ID 
and Type = 1000
and UserID in (select uid from JUidActiveDays)
order by UserID, Timestamp asc