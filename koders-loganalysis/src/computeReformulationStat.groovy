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


ReformulationStat stat = new ReformulationStat()

def compRefStat = {session, sessionId ->
	
	def lastQuery = ""
	def lastAType = ""
	def refPatternB4D = []
	
	session.each(){ line ->
		String[] parts = line.split("\t")
		String atype = parts[6]
		
		if(atype == "1000"){
			String query = parts[8]
			String reformType = ReformulationStat.getReformulationType(lastQuery, query)
			SessionDataIO.updateCount(stat.reformTypeQueryCount, reformType)
			
			refPatternB4D.add(reformType)
			
			lastQuery = query
			
		} else {
			
			if(lastAType == "1000"){
				assert refPatternB4D.size()!=0
			} else {
				assert refPatternB4D.size()==0
				refPatternB4D.add('0')
			}
			
			SessionDataIO.updateCount(stat.reformulationPatternDCount, 
					SessionDataIO.compressSequence(refPatternB4D))
			refPatternB4D.clear()
		}
		lastAType = atype
	}
}

def root = "${args[0]}"
def fip  = "${args[1]}"

SessionCreatorESE sc = new SessionCreatorESE(root,fip)
// sc.intervalInMin = Integer.parseInt("${args[3]}").intValue() 
sc.walkAllSessions(compRefStat)

SessionDataIO.writeCount (root + "reformTypeQueryCount" + "-" + sc.intervalInMin +".txt", 
		stat.reformTypeQueryCount, "reformType\tQueryCount")

SessionDataIO.writeCount (root + "reformulationPatternDCount" + "-" + sc.intervalInMin +".txt", 
		stat.reformulationPatternDCount, "reformulationPattern\tDCount")
println "DONE!"