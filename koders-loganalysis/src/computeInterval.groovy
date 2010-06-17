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

def compInterval = {session, sessionId ->
	
	assert session.size() > 0

	def start  = session[0].split("\t")[7]
	def end    = session[session.size()-1].split("\t")[7]
	def sesDuration = TimeUtil.secDiff(start, end)
	stat.updateCount(stat.durationInSecs, sesDuration)
}

def root = // "/Users/shoeseal/DATA/koders/"
  	"/home/sushil/Scratch/LogAnalysis/ese-data/"    
def ipFile = // "neg-test2.txt"
	"all-susers.txt"

SessionCreatorESE sc = new SessionCreatorESE(root,ipFile)
sc.intervalInMin = Integer.parseInt("${args[0]}").intValue() 
sc.walkAllSessions(compInterval)

stat.writeCount (root + "intervalInSec" + "-" + sc.intervalInMin +".txt", stat.durationInSecs, "durationInSecs\tSesCount")
