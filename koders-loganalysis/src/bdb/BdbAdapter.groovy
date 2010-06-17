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

import com.sleepycat.je.DatabaseException;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredKeySet;

/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */
public class BdbAdapter{

	final static BdbAdapter instance = new BdbAdapter()
	
	private BdbDatabase bdbDatabase = null
	private View view = null
	
	 
	private BdbAdapter(){
		
	}
	
	 public static BdbAdapter getInstance(String bdbLoc) {
		 
		 boolean error = false
		 
		 try{
			 // open Db
			 if (instance.bdbDatabase == null) {
				 instance.bdbDatabase = new BdbDatabase(bdbLoc)
				 // open view
			 	 instance.view = new View(instance.bdbDatabase)
			 }
		 
		 
		 }catch(DatabaseException dbe){
			 error = true
			 System.error.println("Database Exception")
			 dbe.printStackTrace()
		 }catch(FileNotFoundException fnfe){
			 error = true
			 System.error.println("File not found")
			 fnfe.printStackTrace()
		 }catch(Exception e) {
			 error = true
			 System.error.println("Excepton..")
			 e.printStackTrace()
		 }
		 
		 return error?null:instance
	 }
	 
	 
	 void addQueryInSampleActivities(Integer i, Query q){
		 assert view!=null
		 
		 StoredMap<Integer,Query> map = view.getSampleActivitiesMap()
		 if(!map.containsKey(i))
			 map.put(i,q)
	 }
	 
	 Query getSampleActivity(Integer i){
		 assert view!=null
		 
		 StoredMap<Integer,Query> map = view.getSampleActivitiesMap()
		 return map.get(i)
	 }
	 
	 public void clearSampleActivitiesMap(){
		 assert view!=null
		 
		 StoredMap<Integer,Query> map = view.getSampleActivitiesMap()
		 StoredKeySet<Integer> keys = map.keySet()
		 for(Integer i: keys)
			 map.remove(i)
			 
	 }
	 
	 public Set<Query> getAllSampleActivities(){
		 assert view!=null
		 
		 return (Set<Query>) view.getSampleActivitiesEntrySet()
	 }
	 
	 public void dispose(){
		if(bdbDatabase!=null){
			try{
				bdbDatabase.close()
			}catch(Exception e){
				System.err.println("Error closing BDB environment")
				e.printStackTrace()
			}
		}
	 }
	
}
