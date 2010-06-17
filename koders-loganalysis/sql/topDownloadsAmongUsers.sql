-- popularity heuristics used here:
-- it does not matter how many times a user has downloaded
-- what matters is how many users have downloaded it
select FileHash, count(UserID) as user_count
from
(
 select DISTINCT  FileHash, UserID from 
 ReuseLog as R, UserActivityLog as A
 where  A.Type=1001 AND R.ID = A.ActivityID
) as qry_file_per_user
group by FileHash
order by user_count desc