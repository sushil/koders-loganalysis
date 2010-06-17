select DISTINCT Terms, UserID from 
SearchLog, UserActivityLog
where
Type=1000 AND SearchLog.ID=UserActivityLog.ActivityID
AND UserID in
(select UserID from selectedUsersByActivity)
order by UserID, Terms