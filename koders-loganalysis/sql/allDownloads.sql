select FileHash, UserID, R.SearchLogID as search_id, A.ID as activity_id from 
ReuseLog as R, UserActivityLog as A
where
    
	A.Type=1001 AND R.ID = A.ActivityID
	-- language specific search not possible
	-- AND S.Language = 'java'
order by UserID, FileHash