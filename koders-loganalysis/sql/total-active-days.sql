select uid, count(Date) as numdays from
(select 
distinct
UserID as uid, CONVERT(char(10), Timestamp, 101) AS [Date] 
from UserActivityLog
) as insql
group by uid