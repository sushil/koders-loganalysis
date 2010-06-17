select count(ID) as ct, Language from SearchLog 
group by Language
order by ct desc