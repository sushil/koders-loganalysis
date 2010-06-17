select UserID as uid, Type, Timestamp as ts from
UserActivityLog
where UserID in
(select distinct UserID from UserActivityLog where Type=1000)
ORDER BY uid, ts