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

import junit.framework.TestCase

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */
public class AddActivityBdbTest extends TestCase{
	 
	 BdbAdapter bdbAdapter;
	 String dbLoc = "./test/data/bdb.activity";
	 String dbStat = "./test/data/recordsize.properties";
	 String activityData = "/Users/shoeseal/Scratch/LogAnalysis/koders-data/tc_all/1226541912234/fsessions_9999_test.tsv";
	 
	 /* (non-Javadoc)
		 * @see bdb.BdbViews#getSampleActivityEntrySet()
		 */
		void testAddSampleActivityInMap(){
			bdbAdapter = BdbAdapter.getInstance(dbLoc);
			assertNotNull(bdbAdapter);
			
			bdbAdapter.clearSampleActivitiesMap();
			println "..cleared"
			
			Set _acts = bdbAdapter.getAllSampleActivities()
			assertEquals(0, _acts.size())
			
			readFromFile(activityData, bdbAdapter)

			assertEquals(99, _acts.size())
			
			bdbAdapter.dispose()
		}
	 
		public void readFromFile(String filename, BdbAdapter bdbAdapter) {
		        
		        LineNumberReader lineNumberReader = null;
		        
		        try {
		            int lines = 0;
		            //Construct the LineNumberReader object
		            lineNumberReader = new LineNumberReader(new FileReader(filename));
		            
		            String line = null;
		            
		            // skip header
		            lineNumberReader.readLine()
		            
		            while ((line = lineNumberReader.readLine()) != null) {
		                
		            	String[] cols = line.trim().toString().split("\t");
						if(cols.length == 8) {
							Query q = new Query(cols[1], cols[3], cols[6], cols[2]=="S")
							bdbAdapter.addQueryInSampleActivities(new Integer(lines++), q)
						}
		            }
		            
		            new File(dbStat).withWriter { out ->
					 // header
					 out.write("activities.records.size="+lines);
					}
		            
		        } catch (FileNotFoundException ex) {
		            ex.printStackTrace();
		        } catch (IOException ex) {
		            ex.printStackTrace();
		        } finally {
		            //Close the BufferedWriter
		            try {
		                if (lineNumberReader != null) {
		                    lineNumberReader.close();
		                }
		            } catch (IOException ex) {
		                ex.printStackTrace();
		            }
		        }
		 }
}
