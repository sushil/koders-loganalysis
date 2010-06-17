SELECT     ReuseLog.FileHash, ReuseLog.Day, ReuseLog.Month, ReuseLog.Year, UserActivityLog.Timestamp
FROM         UserActivityLog INNER JOIN
				ReuseLog ON UserActivityLog.ActivityID = ReuseLog.ID
WHERE     (UserActivityLog.UserID = 15075893)
ORDER BY UserActivityLog.Timestamp