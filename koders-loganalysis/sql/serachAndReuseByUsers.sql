( SELECT     SearchLog.Terms, SearchLog.ID as sID, 
             SearchLog.Language as lang,
             SearchLog.Day as d, 
             SearchLog.Month as m, SearchLog.Year as y, 
             UserActivityLog.Timestamp as ts, UserActivityLog.UserID as uid
  FROM         UserActivityLog INNER JOIN SearchLog 
     ON UserActivityLog.ActivityID = SearchLog.ID
     WHERE UserActivityLog.Type=1000
  -- 
     AND    UserActivityLog.UserID = 12409189
				-- in (select UserID from usersByActivity)
  -- AND (SearchLog.Language = 'java')
)
UNION
(SELECT     ReuseLog.FileHash, ReuseLog.SearchLogID as sID, 
            CAST(ProjectID As VARCHAR) as lang,
            ReuseLog.Day as d, ReuseLog.Month as m, ReuseLog.Year as y, 
            UserActivityLog.Timestamp as ts, UserActivityLog.UserID as uid
 FROM       UserActivityLog INNER JOIN ReuseLog 
    ON UserActivityLog.ActivityID = ReuseLog.ID
    WHERE UserActivityLog.Type=1001 
 -- 
    AND UserActivityLog.UserID = 12409189
					-- in (select UserID from usersByActivity) 
							   -- 12409189) /* big but many strange downloads */
                               -- 15582186) /* much more downloads */
                               -- 22292912) /* system */
                               -- 12777534)
                               -- 14004341) /*may be*/
                               -- 21709250)
                               -- 19713664)
                               -- 13232622)/* weird */
                               -- 20112311) /*interesting*/
							   -- = 18767385) /*System ?*/
                               -- = 20608868) /*System ?*/ 
                               -- 12409189)
)
ORDER BY ts, sID