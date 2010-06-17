SELECT     SearchLog.Terms, SearchLog.Language, SearchLog.Day, SearchLog.Month, SearchLog.Year, UserActivityLog.Timestamp
FROM         UserActivityLog INNER JOIN
                      SearchLog ON UserActivityLog.ActivityID = SearchLog.ID
WHERE     (UserActivityLog.UserID = 15075893)
AND (SearchLog.Language = 'java')
ORDER BY UserActivityLog.Timestamp