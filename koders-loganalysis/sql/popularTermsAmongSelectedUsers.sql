select Terms, Count(UserID) as un
from
(
select DISTINCT Terms, UserID from 
SearchLog, UserActivityLog
where
Type=1000 AND SearchLog.ID=UserActivityLog.ActivityID AND SearchLog.Language = 'java'
-- AND UserID in
--(select UserID from selectedUsersByActivity)
) as inner_sql
group by (Terms)
order by un desc