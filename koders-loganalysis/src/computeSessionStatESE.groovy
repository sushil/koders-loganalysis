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

def calc = {session, sessionId ->
	int dcount = 0
	int scount = 0
	session.each(){ line ->
		String[] parts = line.split("\t")
		String atype = parts[6]
		if(atype == "1000") 
			++scount
		else if(atype == "1001")
			++dcount
	}
	
	if(session[0].split("\t")[6] == "1001"){ 
		++stat.numSesStartD
	}
	
	if (dcount == 0) { 
		++stat.numSesWoD
		stat.updateCount(stat.numSCountInSesWoD, scount)
	} else {
		stat.updateCount(stat.numSCountInSesWD, scount)
	}
	
	if (scount == 0) { 
		++stat.numSesWoS
		stat.updateCount(stat.numDCountInSesWoS, dcount)
	}
	
	if (scount > 0 && dcount > 0) { 
		++stat.numSesWSD
		stat.updateCount(stat.numDCountInSesWSD, dcount)
		stat.updateCount(stat.numSCountInSesWSD, scount)
	}
	
	
	
	++stat.numSes
	
	stat.updateCount(stat.numActCountInAllSes, session.size())
	stat.updateCount(stat.numSCountInAllSes, scount)
	stat.updateCount(stat.numDCountInAllSes, dcount)
}


def root = "/Users/shoeseal/DATA/koders/"
def ipFile = "neg-test2.txt"

SessionCreatorESE ss = new SessionCreatorESE(root,ipFile)
ss.walkAllSessions(calc)

/*
stat.printCount (stat.numActCountInAllSes, "allActs")
stat.printCount (stat.numSCountInAllSes, "allS")
stat.printCount (stat.numDCountInAllSes, "allD")
stat.printCount (stat.numSCountInSesWoD, "SInSesWoD")
stat.printCount (stat.numSCountInSesWD, "SInSesWD")
stat.printCount (stat.numDCountInSesWoS, "DInSesWoS")
*/

stat.writeCount (root + "allActs.txt", stat.numActCountInAllSes, "allActs\tSesCount")
stat.writeCount (root + "allS.txt", stat.numSCountInAllSes, "allS\tSesCount")
stat.writeCount (root + "allD.txt", stat.numDCountInAllSes, "allD\tSesCount")
stat.writeCount (root + "SInSesWoD.txt", stat.numSCountInSesWoD, "SInSesWoD\tSesCount")
stat.writeCount (root + "SInSesWD.txt", stat.numSCountInSesWD, "SInSesWD\tSesCount")
stat.writeCount (root + "DInSesWoS.txt", stat.numDCountInSesWoS, "DInSesWoS\tSesCount")
stat.writeCount (root + "numSCountInSesWSD.txt", stat.numSCountInSesWSD, "numSCountInSesWSD\tSesCount")
stat.writeCount (root + "numDCountInSesWSD.txt", stat.numDCountInSesWSD, "numDCountInSesWSD\tSesCount")

println "----"
println "num sessions: " + stat.numSes
println "num sessions w/o download: " + stat.numSesWoD
println "num sessions w/o search: " + stat.numSesWoS
println "num sessions w both search/download: " + stat.numSesWSD
println "num sessions start with download: " + stat.numSesStartD
println "----"
println "DONE!"

/*
allActs	count
13	1
4	1
1	2
allS	count
0	1
1	3
allD	count
13	1
3	1
0	2
SInSesWoD	count
1	2
SInSesWD	count
0	1
1	1
DInSesWoS	count
13	1
num sessions: 4
num sessions w/o download: 2
num sessions w/o search: 1
num sessions start with download: 2
done

*/