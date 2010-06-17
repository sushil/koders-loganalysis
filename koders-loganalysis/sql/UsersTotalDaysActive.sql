select UserID as uid, count(DISTINCT date) as activeDays
from
(select UserID, 
				( CAST(YEAR(Timestamp) AS VARCHAR) + ',' +
			     CAST(MONTH(Timestamp) AS VARCHAR) + ',' +
			     CAST(DAY(Timestamp) AS VARCHAR)) as date 
from UserActivityLog) as inners
group by UserID