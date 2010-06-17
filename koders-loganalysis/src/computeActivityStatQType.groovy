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

InflDictionary.loadDict(
		"${args[2]}"		
		// "/Users/shoeseal/DATA/koders/dict-test.txt"
		// "/home/sushil/Scratch/LogAnalysis/ese-data/dict.txt"
		)

ActivityStatQTypeESE stat = new ActivityStatQTypeESE()
CheckWord.RemoveOperatorB4TypeDetect = true

def compActQtypeStat = {session, sessionId ->
	
	// update counts for first query type in session 
	String[] firstActParts = session[0].split("\t")
	if(firstActParts[6] == "1000"){
		SessionDataIO.updateCount(stat.queryTypeStartsSes
				,CheckWord.getQueryType(firstActParts[8])) 
	}
	
	
	String lastQType = ""
	def qTypePattern = []
	
	session.each(){ line ->
		String[] parts = line.split("\t")
		String atype = parts[6]
		String query = parts[8]
		String lang = parts[2]
		
		String qtype = ""
		
		if(atype == "1000"){
			++stat.totalS
			
			qtype = CheckWord.getQueryType(query)
			qTypePattern.add(qtype)
			
			SessionDataIO.updateCount(stat.qtypeQCount, qtype)
			SessionDataIO.updateCount(stat.langFilterQueryCount, lang)
			
		}
		else { 
			++stat.totalD
			
			//			if(qTypePattern.length()>0){
			//				
			//			}
			
			if(qTypePattern.size()==0)
				qTypePattern.add('0')
			
			SessionDataIO.updateCount(stat.qtypePatternDCount, 
					SessionDataIO.compressSequence(qTypePattern))
			
			qTypePattern.clear()
			
			if(lastQType != ""){
				SessionDataIO.updateCount(stat.queryTypeFollowsDownloads, 
						lastQType)
			}
		}
		lastQType = qtype
	}
}

def root = "${args[0]}"
def fip  = "${args[1]}"

SessionCreatorESE sc = new SessionCreatorESE(root,fip)
// sc.intervalInMin = Integer.parseInt("${args[3]}").intValue() 
sc.walkAllSessions(compActQtypeStat)

println "----"
println "total search act: " + stat.totalS
println "total download act: " + stat.totalD
println "----"

SessionDataIO.writeCount (root + "langFilterQueryCount" + "-" + sc.intervalInMin +".txt", 
		stat.langFilterQueryCount, "langFilter\tQueryCount")

SessionDataIO.writeCount (root + "qtypeQCount" + "-" + sc.intervalInMin +".txt", 
		stat.qtypeQCount, "qtype\tQCount")
		
SessionDataIO.writeCount (root + "queryTypeStartsSes" + "-" + sc.intervalInMin +".txt", 
		stat.queryTypeStartsSes, "queryTypeStartsSes\tSesCount")		
		
SessionDataIO.writeCount (root + "queryTypeFollowsDownloads" + "-" + sc.intervalInMin +".txt", 
		stat.queryTypeFollowsDownloads, "queryTypeFollowsDownloads\tDownloadCount")		

SessionDataIO.writeCount (root + "qtypePatternDCount" + "-" + sc.intervalInMin +".txt", 
		stat.qtypePatternDCount, "qtypePattern\tDownloadCount")		

CheckWord.RemoveOperatorB4TypeDetect = false
		
println "DONE!"		
		