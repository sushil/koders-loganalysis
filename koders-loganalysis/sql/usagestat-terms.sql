select UserActivityLog.ID, Timestamp, UserID, Terms
from SearchLog, UserActivityLog
where ActivityID = SearchLog.ID and Type = 1000
order by UserID, Timestamp asc