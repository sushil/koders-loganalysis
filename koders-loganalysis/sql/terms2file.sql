select Terms from SearchLog
where ID in
(select SearchLogID from ReuseLog 
 where FileHash='DD507AA710FB8CFA6B60DD4D21C20DD94DB821E2') 