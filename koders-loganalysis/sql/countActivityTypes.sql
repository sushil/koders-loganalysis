select count(Type) as ct, Type from UserActivityLog
group by Type
order by ct desc