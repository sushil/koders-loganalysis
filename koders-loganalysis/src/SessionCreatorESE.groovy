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


public class SessionCreatorESE{
	
	public Status s 
	
	int intervalInMin = 6
	def root 
	def fIp 
	def fReader
	
	def getDuration(cur, last){
		return TimeUtil.minDiff(last, cur);
	}
	
	public SessionCreatorESE(root2, fip2){
		root =  root2
		if(!root.endsWith("/")) root = root + '/'
		fIp = root + fip2
		
		s = new Status()
		fReader = new FileReader(fIp)
		int linecount = 0
		fReader.readLines().each {
			++linecount
		}
		s.reset(linecount)
		
		fReader = new FileReader(fIp)
	}
	
	def updateCount(amap, akey){
		if(amap.containsKey(akey)){
			amap[akey] = amap[akey] + 1
		} else {
			amap[akey] = 1
		}
	}
	
	def writeCount(filename, amap, intervaltype){
		new File(filename).withWriter {out ->
			out.writeLine(intervaltype + "\tcount")
			amap.each {interval, count ->
				out.writeLine(interval + "\t" + count)
			}
			
		}
	}
	
	// processSession is a closure with two arguments session == list of lines, sessionid
	def walkAllSessions(processSession){
		
		def lastUid = ""
		def lastActType = ""
		def firstActTS = ""
		def lastActTS = ""
		def min6_sesStartTS = ""
		
		def session = []
		int sessionId = 1               
		
		//skip header
		fReader.readLine()
		s.update()
		
		fReader.readLines().each {line ->
			
			String[] lineItems = line.trim().toString().split("\t", 9)
			if (lineItems.length != 9) {
				// debug
				// println "[discarding]" + line
			} else {
				
				def curUid = lineItems[0]
				def curTS  = lineItems[7]
				
				def curActType = lineItems[6]
				
				if(lastActTS=="") lastActTS = curTS
				if(lastUid=="") lastUid = curUid
				
				
				def duration = getDuration(curTS, lastActTS)
				
				// computing session duration when 6 min interval breaks a session
				if(min6_sesStartTS == "") min6_sesStartTS = curTS
				if( (curUid == lastUid) && (TimeUtil.minDiff(lastActTS,curTS) > intervalInMin) ||
				(curUid != lastUid)){
					def sesDuration = TimeUtil.secDiff(min6_sesStartTS, lastActTS)
					
					// processSession session data
					processSession(session,sessionId)
					
					min6_sesStartTS = curTS
					// create new session
					session = []
					sessionId++
				}
				
				session.add(line)
				
				// updates for next state
				lastActType = curActType
				lastUid = curUid
				lastActTS = curTS
			}
			
			s.update()
		}
		
		if (session.size()>0) processSession(session, sessionId)
	}
	
}


