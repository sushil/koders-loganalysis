select uid, activeDays from UsersActiveDays
where uid in (select UserID from uidtc) 