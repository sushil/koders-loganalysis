-- download count among search users
select count(ID) from UserActivityLog 
where 
--Type = 1001
--and
UserID in
(
select distinct UserID from UserActivityLog where Type = 1000
)