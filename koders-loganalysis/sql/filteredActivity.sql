select terms, count(uid) as t_f from searchAndReuse
group by terms
order by t_f desc