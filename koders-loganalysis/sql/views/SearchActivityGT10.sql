SELECT     ct AS UserActivityCount, UserID
FROM         (SELECT     COUNT(*) AS ct, UserID
                       FROM          dbo.UserActivityLog
                       WHERE      (Type = 1000)
                       GROUP BY UserID) AS innersql
WHERE     (ct > 10)