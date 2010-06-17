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


SessionStatESE stat = new SessionStatESE()

def compRepeatStat = {session, sessionId ->
	def pageViewsInSes = []
	
	String prevAType = ""
	String prevQuery = ""
	int sb4d = 0
	int pageViews = 0
	
	boolean sessionHasD = false
	boolean sessionHasS = false
	
	// a session can be:  ..S.Q1 S.Q1 S.Q2 D D S.Q3 S.Q1 ...
	
	session.each(){ line ->
		String[] parts = line.split("\t")
		String atype = parts[6]
		String query = parts[8]                     
		
		// first activity
		// if (prevAType == ""){
		//	
		// }
		
		if(atype == "1000") {
			++sb4d
			sessionHasS = true
			
			
			
			// S -> S
			if(prevAType == "1000"){
				// S.Q1 -> S.Q1 
				if(prevQuery == query ){
					++pageViews
				} else {
					// dump this pageview 
					// S.Q1 -> S.Q2
					pageViewsInSes.add (pageViews+ 0)
					pageViews = 1
				}
			} else {
				// D|<null> -> S
				pageViews = 1
			}
			prevQuery = query
		}
		// download activity
		else if(atype == "1001"){
			sessionHasD = true
			
			// S -> D
			if(prevAType == "1000"){
				
				assert pageViews > 0
				
				// download does not resets pageview
				//pageViewsInSes.add (pageViews + 0)
				
				// update stat.PageViewsInSesB4D
				stat.updateCount (stat.numPageViewsInSesB4D, pageViews + 0)
				
				// update stat.numSB4D
				stat.updateCount (stat.numSB4D, sb4d)
				
				// download does not reset page view
				//pageViews = 0
			
			} else {
				// D|null -> D
				
				// either session started with this D, or
				// there was another D before this
				stat.updateCount (stat.numSB4D, 0)
			}
			sb4d = 0
			
			// a download does not resets the query
			// prevQuery = ""
		}
		prevAType = atype
	} // end processing each activity
	
	if (pageViews > 0) pageViewsInSes.add(pageViews + 0)
	
	// update pageViews stat
	pageViewsInSes.each() {
		stat.updateCount (stat.numPageViewsInAllSes, it)
	}
	
	if(sessionHasD){
		pageViewsInSes.each() {
			stat.updateCount (stat.numPageViewsInSesWD, it)
		}
	} else {
		pageViewsInSes.each() {
			stat.updateCount (stat.numPageViewsInSesWoD, it)
		}
	}
	
	if(sessionHasD && sessionHasS){
		pageViewsInSes.each() {
			stat.updateCount (stat.numPageViewsInSesWSD, it)
		}
	}
	
}

def root =  "/Users/shoeseal/DATA/koders/"
  	// "/home/sushil/Scratch/LogAnalysis/ese-data/"    
def ipFile = "100-susers.txt"   
	//"neg-test2.txt"
	// "all-susers.txt"

SessionCreatorESE sc = new SessionCreatorESE(root,ipFile)
// sc.s.disable = true
// sc.intervalInMin = Integer.parseInt("${args[0]}").intValue() 
sc.walkAllSessions(compRepeatStat)

stat.writeCount (root + "PageViewsSes" + "-" + sc.intervalInMin +".txt", 
		stat.numPageViewsInAllSes, "PageViewsSes\tSearchReqs")
		
stat.writeCount (root + "PageViewsSesWoD" + "-" + sc.intervalInMin +".txt", 
		stat.numPageViewsInSesWoD, "PageViewsSesWoD\tSearchReqs")
		
stat.writeCount (root + "PageViewsInSesWD" + "-" + sc.intervalInMin +".txt", 
		stat.numPageViewsInSesWD, "PageViewsInSesWD\tSearchReqs")

stat.writeCount (root + "PageViewsInSesWSD" + "-" + sc.intervalInMin +".txt", 
		stat.numPageViewsInSesWSD, "PageViewsInSesWSD\tSearchReqs")		
		
stat.writeCount (root + "numPageViewsInSesB4D" + "-" + sc.intervalInMin +".txt", 
		stat.numPageViewsInSesB4D, "PageViewsInSesB4D\tDownloads")
		
stat.writeCount (root + "numSB4D" + "-" + sc.intervalInMin +".txt", 
		stat.numSB4D, "numSB4D\tDownloads")

println "DONE!"		