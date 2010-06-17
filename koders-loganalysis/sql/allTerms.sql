select DISTINCT  Terms, UserID from 
SearchLog as S, UserActivityLog as A
where
    A.Type=1000 AND S.ID = A.ActivityID

	-- limit to one language
AND S.Language = 'java'
order by Terms