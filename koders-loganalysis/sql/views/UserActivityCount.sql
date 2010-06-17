SELECT     COUNT(*) AS ActivityCount, UserID
FROM         dbo.UserActivityLog
GROUP BY UserID