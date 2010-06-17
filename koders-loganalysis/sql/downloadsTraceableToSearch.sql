select DISTINCT  FileHash, Terms, R.ID as reuse_log_id, R.SearchLogID as search_log_id_in_reuse, UserID from 
ReuseLog as R, UserActivityLog as A, SearchLog as S
where
    -- ((A.Type=1001 AND R.ID = A.ActivityID) OR (A.Type=1000 AND S.ID = A.ActivityID))
    
	-- looking at downloads
	A.Type=1001 AND R.ID = A.ActivityID
	-- traceable to a search
AND R.SearchLogID = S.ID
	-- language specific search
AND S.Language = 'java'
order by FileHash