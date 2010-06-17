SELECT  UserActivityLog.UserID as uid, 
        SearchLog.Language as lang,
		SearchLog.License as lic,
		SearchLog.Terms as Terms
  
   FROM  UserActivityLog INNER JOIN SearchLog 
     ON  UserActivityLog.ActivityID = SearchLog.ID
  WHERE  UserActivityLog.Type=1000 
		 and (UserActivityLog.UserID in (select uid from JUidActiveDays))
  order by 
     uid asc, UserActivityLog.Timestamp asc