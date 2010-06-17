select Terms, Searches, Hits, Day, Month, Year from SearchLog 
where language='java' AND Searches>10 
order by Searches desc