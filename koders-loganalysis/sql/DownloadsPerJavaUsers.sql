select UserID, COALESCE(dcount, 0) as dcount from uidtc
left outer join
(select UserID as uid, count(ID) as dcount from UserActivityLog
where Type = 1001
group by UserID)
as sql2
on uidtc.UserID = sql2.uid