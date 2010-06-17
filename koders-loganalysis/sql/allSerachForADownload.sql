 select SR.Terms as s_term, SR.sID, SR.lang, SR.ts as ts, SR.uid as s_userid, D.uid as d_userid, 
        datediff(ss, SR.ts, D.ts) as ss_diff, 
        SR.activity_id as sr_activity_id_fine, D.activity_id as d_activity_id_fine
 from 
 -- searchAndReuseSelectedUsers as SR, searchAndReuseSelectedUsers as D
    searchAndReuse as SR, searchAndReuse as D
 where D.Terms = '002DB6BD79CABA7B6AA0F2669061424E3B9776D3' 
       AND SR.uid = D.uid
	   -- AND ss_diff > -60 AND ss_diff < 180
       AND datediff(ss, SR.ts, D.ts) > -60 AND datediff(ss, SR.ts, D.ts) < 180
 order by SR.uid, SR.ts asc