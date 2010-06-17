-- count activities
-- 269
select count(ul.ID) as act_count from 
SearchLog as sl inner join UserActivityLog as ul
on ul.ActivityID = sl.ID 
	AND ul.Type=1000
    AND sl.terms like '%' + char(13) + '%'

-- count users
-- 143
select count (distinct ul.UserID) as uid from 
SearchLog as sl inner join UserActivityLog as ul
on ul.ActivityID = sl.ID 
	AND ul.Type=1000
    AND sl.terms like '%' + char(13) + '%'

-- count distinct queries
-- 209
select count(distinct terms) from  SearchLog 
where terms like '%' + char(13) + '%'

-- show queries
select terms + char(13) + '----' from  SearchLog 
where terms like '%' + char(13) + '%'

--
select terms from SearchLog where terms=''
