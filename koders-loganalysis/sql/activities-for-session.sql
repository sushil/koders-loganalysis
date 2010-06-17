( SELECT     UserActivityLog.UserID as uid, 
             SearchLog.ID as sID, 
             SearchLog.Language as lang,
             SearchLog.Day as d, 
             SearchLog.Month as m, SearchLog.Year as y,
			 UserActivityLog.Type as type, 
             UserActivityLog.Timestamp as ts, 
			 SearchLog.Terms
  
   FROM  UserActivityLog INNER JOIN SearchLog 
     ON  UserActivityLog.ActivityID = SearchLog.ID
  WHERE  UserActivityLog.Type=1000
  -- 
  AND    UserActivityLog.UserID in
					-- (select UserId from uidtc where NumTopics>=4 AND NumTopics<=12)
 				    -- (select UserId from uidtc where NumTopics>=1)
					   
					/*	(select UserId from uiddays where NumDays=1 
							AND UserId in 
                                  (select UserId from uidtc))
					*/

					-- (select uid from javaUsersSearchActCount where SCount>80) 

					-- (select UserID from JavaUsersDwnldActCount where dcount >= 90)	
					   
                    --   (select uid from JUidActiveDays where activeDays >= 15)
				(select distinct UserID from UserActivityLog where Type=1000)
						
)
UNION
(SELECT     UserActivityLog.UserID as uid, 
            ReuseLog.SearchLogID as sID, 
            CAST(ProjectID As VARCHAR) as lang,
            ReuseLog.Day as d, ReuseLog.Month as m, ReuseLog.Year as y, 
            UserActivityLog.Type as type, 
			UserActivityLog.Timestamp as ts, 
            ReuseLog.FileHash
 
  FROM UserActivityLog INNER JOIN ReuseLog 
    ON UserActivityLog.ActivityID = ReuseLog.ID
 WHERE UserActivityLog.Type=1001 
 -- 
 AND UserActivityLog.UserID in
					-- (select UserId from uidtc where NumTopics>=4 AND NumTopics<=12)
					-- (select UserId from uidtc where NumTopics>=1)
					   
					/*   (select UserId from uiddays where NumDays=1
							AND UserId in 
                                  (select UserId from uidtc))
					*/
					
					 --  (select uid from javaUsersSearchActCount where SCount>80)	

					/* Downloads: =0, <=1 & <30, <=30 & <90, >=90*/
					-- (select UserID from JavaUsersDwnldActCount where dcount >= 90)
					
					/* 1, >=2 & <7, >=7 & <15, >=15 */   
				--	(select uid from JUidActiveDays where activeDays >= 15)	
			(select distinct UserID from UserActivityLog where Type=1000) -- limits to users who have at least 1 search
)
ORDER BY uid, ts, sID