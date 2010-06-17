-- user, lo_st, hi_st, lo_rt, hi_rt, s_duration, r_duration, activity_duration

select * from
(
select UserID, hi_t, lo_t, act_count, act_duration as duration,
		substring(act_duration, 7, 2) as act_months,
		substring(act_duration, 10, 2) as act_days,
		(cast(substring(act_duration, 7, 2) as int)*30
			+ cast(substring(act_duration, 10, 2) as int)) as total_days
from
(
 select UserID, max(Timestamp) as hi_t, min(Timestamp) as lo_t,
       count(ActivityID) as act_count, 
       dbo.fn_datediff2(min(Timestamp), max (Timestamp)) as act_duration      
 from UserActivityLog
 group by UserID
) as inner_sql
) as inner_sql2
where total_days > 3 and act_count > 99
order by act_count desc, total_days desc

