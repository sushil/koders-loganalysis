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

def printSessions = {session, sessionId ->
	println "----"
	session.each(){ line ->
		println sessionId + "\t" + line
	}
}

int sc = 0
def countSessions = {session, sessionId ->
	println sessionId
	++sc
}

SessionCreatorESE ss = new SessionCreatorESE("/Users/shoeseal/DATA/koders/","100-susers.txt")
ss.s.disable = true
ss.walkAllSessions(printSessions)
// ss.walkAllSessions(countSessions)
println sc

println "done"





