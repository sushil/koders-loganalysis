-- downlod count by searchers
select UserID, dcount from
(
select distinct UserID, COALESCE(dcount, 0) as dcount from UserActivityLog
left outer join
(select UserID as uid, count(ID) as dcount from UserActivityLog
where Type = 1001
group by UserID)
as sql2
on UserID = sql2.uid
) as sqlout
where UserID in
(select distinct UserID from UserActivityLog where Type = 1000)
