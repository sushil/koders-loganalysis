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


int SfD = 0
int DfD = 0
int NfD = 0

def compRepeatStat = {session, sessionId ->
	def pageViewsInSes = []
	
	String prevAType = ""
	
	// a session can be:  ..S.Q1 S.Q1 S.Q2 D D S.Q3 S.Q1 ...
	
	session.each(){ line ->
		String[] parts = line.split("\t")
		String atype = parts[6]
		                     
		if(atype == "1001"){
			if(prevAType == "") ++NfD
			if(prevAType == "1000") ++SfD
			if(prevAType == "1001") ++DfD 
		}
		
		prevAType = atype
		                
	}
}

def root   = "${args[0]}"  
def ipFile = "${args[1]}"

SessionCreatorESE sc = new SessionCreatorESE(root,ipFile)
// sc.s.disable = true
// sc.intervalInMin = Integer.parseInt("${args[2]}").intValue() 
sc.walkAllSessions(compRepeatStat)


println "----"
println "# search follows download:   "   + SfD
println "# downlods follows download: " + DfD
println "# nothing follows download:  "  + NfD
println "----"