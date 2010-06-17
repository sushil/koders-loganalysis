select FileHash, Count(UserID) as un
from
(
select DISTINCT FileHash, 
                Terms, 
                -- S.ID as search_log_id, 
                -- R.SearchLogID as search_log_id_in_reuse, 
                UserID 
from 
 ReuseLog as R
 ,UserActivityLog as A
 ,SearchLog as S
where
    A.Type=1001 AND R.ID = A.ActivityID 
-- AND R.SearchLogID = S.ID
AND S.Language = 'java'

-- select DISTINCT FileHash, UserID from 
-- ReuseLog as R, UserActivityLog as A, SearchLog as S
-- where
-- A.Type=1001 AND R.ID = A.ActivityID 
--           AND R.SearchLogID = S.ID
--          AND S.Language = 'java'
-- AND UserID in
--(select UserID from selectedUsersByActivity)
) as inner_sql
group by (FileHash)
order by un desc