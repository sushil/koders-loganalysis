SELECT     UserID, hi_t, lo_t, act_count, duration, act_months, act_days, total_days
FROM         (SELECT     UserID, hi_t, lo_t, act_count, act_duration AS duration, SUBSTRING(act_duration, 7, 2) AS act_months, SUBSTRING(act_duration, 10, 2) 
                                              AS act_days, CAST(SUBSTRING(act_duration, 7, 2) AS int) * 30 + CAST(SUBSTRING(act_duration, 10, 2) AS int) AS total_days
                       FROM          (SELECT     UserID, MAX(Timestamp) AS hi_t, MIN(Timestamp) AS lo_t, COUNT(ActivityID) AS act_count, dbo.fn_datediff2(MIN(Timestamp), 
                                                                      MAX(Timestamp)) AS act_duration
                                               FROM          dbo.UserActivityLog
                                               GROUP BY UserID) AS inner_sql) AS inner_sql2
WHERE     (total_days > 30) AND (act_count > 150) AND (act_count < 6000)