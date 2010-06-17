select ct, UserId from
(select count(*) as ct, UserID from UserActivityLog
 group by UserID) as innersql
where ct>10
order by ct desc
