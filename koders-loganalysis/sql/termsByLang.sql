select count(Terms) as ct, Terms from SearchLog
where Language='java'
group by Terms
order by ct desc