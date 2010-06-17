-- search count by searchers
select UserID, count(ID) as scount
from UserActivityLog
where Type=1000
group by UserID
