select TOP(1000) * from SearchActivityGT10
where UserActivityCount < 16500
order by UserActivityCount desc