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

int nSessions = 0
int nQueries = 0
int nSesWithS = 0
int nSesWithD = 0


int sumQuerySize = 0
int sumAInSes = 0
int sumSInSes = 0
int sumDInSes = 0

def calc = {session, sessionId ->
	int dcount = 0
	int scount = 0
	int acount = 0
	boolean foundS = false
	boolean foundD = false
	
	session.each(){ line ->
		String[] parts = line.split("\t")
		String atype = parts[6]
		if(atype == "1000"){ 
			
			foundS = true
			String query = parts[8]
			int qlength = query.split("\\s").length                    
			sumQuerySize = sumQuerySize + qlength
			
			++scount
			++nQueries
		}
		else if(atype == "1001"){
			foundD = true
			++dcount
		}
		
		++acount	
		
	}
	sumSInSes = sumSInSes + scount
	sumAInSes = sumAInSes + acount
	sumDInSes = sumDInSes + dcount
	
	++nSessions
	if(foundS) ++nSesWithS
	if(foundD) ++nSesWithD
	
}


def root   = "${args[0]}"  
def ipFile = "${args[1]}"

SessionCreatorESE ss = new SessionCreatorESE(root,ipFile)
ss.walkAllSessions(calc)

println "----"
println "average query size: " + ((double) sumQuerySize/(double) nQueries)
println "average s in ses: "   +     ((double) sumSInSes/(double) nSessions)
println "average s in ses w S: "   + ((double) sumSInSes/(double) nSesWithS)
println "average d in ses: "   + ((double) sumDInSes/(double) nSessions)
println "average d in ses w D: "   + ((double) sumDInSes/(double) nSesWithD)
println "average a in ses: "   + ((double) sumAInSes/(double) nSessions)
println "----"


