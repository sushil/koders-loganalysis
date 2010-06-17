select count (Distinct UserID) from
-- select DISTINCT Terms, UserID from 
SearchLog, UserActivityLog
where
Type=1000 AND SearchLog.ID=UserActivityLog.ActivityID AND SearchLog.Language = 'java'