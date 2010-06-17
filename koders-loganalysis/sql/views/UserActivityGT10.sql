SELECT     ct AS UserActivityCount, UserID
FROM         (SELECT     COUNT(*) AS ct, UserID
                       FROM          dbo.UserActivityLog
                       GROUP BY UserID) AS innersql
WHERE     (ct > 10)