select TOP(1000) * from UserActivityGT10
where UserActivityCount < 16500
order by UserActivityCount desc