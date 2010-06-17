select uid, count(Date) as numdays from
(select 
distinct
UserID as uid, CONVERT(char(10), Timestamp, 101) AS [Date] 
from UserActivityLog
where 
UserID in
(select distinct UserID from UserActivityLog where Type=1000)
) as insql
group by uid