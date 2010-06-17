select UserID as uid, count(ID) as dcount from UserActivityLog
where Type = 1001
group by UserID