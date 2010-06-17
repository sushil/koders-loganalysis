SELECT     dbo.UserActivityLog.ActivityID, dbo.SearchLog.Terms, dbo.SearchLog.ID AS sID, dbo.SearchLog.Language AS lang, dbo.SearchLog.Day AS d, 
                      dbo.SearchLog.Month AS m, dbo.SearchLog.Year AS y, dbo.UserActivityLog.Timestamp AS ts, dbo.UserActivityLog.UserID AS uid
FROM         dbo.UserActivityLog INNER JOIN
                      dbo.SearchLog ON dbo.UserActivityLog.ActivityID = dbo.SearchLog.ID
WHERE     (dbo.UserActivityLog.Type = 1000)
UNION
SELECT     TOP (100) PERCENT UserActivityLog_1.ActivityID, dbo.ReuseLog.FileHash, dbo.ReuseLog.SearchLogID AS sID, 
                      CAST(dbo.ReuseLog.ProjectID AS VARCHAR) AS lang, dbo.ReuseLog.Day AS d, dbo.ReuseLog.Month AS m, dbo.ReuseLog.Year AS y, 
                      UserActivityLog_1.Timestamp AS ts, UserActivityLog_1.UserID AS uid
FROM         dbo.UserActivityLog AS UserActivityLog_1 INNER JOIN
                      dbo.ReuseLog ON UserActivityLog_1.ActivityID = dbo.ReuseLog.ID
WHERE     (UserActivityLog_1.Type = 1001)
ORDER BY ts, sID