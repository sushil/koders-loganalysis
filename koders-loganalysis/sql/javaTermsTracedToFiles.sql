select DISTINCT  Terms, FileHash, S.ID as search_log_id, R.SearchLogID as search_log_id_in_reuse, UserID from 
SearchLog as S, UserActivityLog as A, ReuseLog as R
where
    -- ((A.Type=1001 AND R.ID = A.ActivityID) OR (A.Type=1000 AND S.ID = A.ActivityID))
    
	-- looking at searches
	A.Type=1000 AND S.ID = A.ActivityID
	-- search linked to downloads
AND R.SearchLogID = S.ID
	-- language specific search
AND S.Language = 'java'
order by Terms