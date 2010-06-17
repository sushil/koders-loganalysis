/*
 * koders-loganalysis: Tools/Scripts to analyze Koders usage log. 
 * Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package bdb

import com.sleepycat.collections.StoredEntrySet
import com.sleepycat.collections.StoredMap
import junit.framework.TestCase


/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 	
 *
 */
public class GetActivityBdbTest extends TestCase{
	
	 BdbAdapter bdbAdapter;
	 String dbLoc = "./test/data/bdb.activity";
	 String dbStat = "./test/data/recordsize.properties";
	 
	/* (non-Javadoc)
	 * @see bdb.BdbViews#getSampleActivityMap()
	 */
	void testGetSampleActivityMap(){
		
		bdbAdapter = BdbAdapter.getInstance(dbLoc);
		assertNotNull(bdbAdapter);
		
		Properties dbP = new Properties()
		dbP.load(new FileInputStream(dbStat))
		def rsize = dbP.getProperty("activities.records.size")
		
		if (rsize!=null)
			rsize = new Integer(rsize)
		
		println rsize
		
		Set _acts =  bdbAdapter.getAllSampleActivities()
		
		assertEquals(rsize, _acts.size())
		
		if(rsize>0){
			Query q = bdbAdapter.getSampleActivity(new Integer(1))
			assertNotNull(q)
			println q
			
			q = bdbAdapter.getSampleActivity(new Integer(2))
			assertNotNull(q)
			println q
		}
		
		bdbAdapter.dispose()
	}
	
	
}
