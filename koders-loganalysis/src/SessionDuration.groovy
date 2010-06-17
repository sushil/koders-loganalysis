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


/**
 * input: fsessions_XX.tsv
 */

/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */

 def fsessionsFile = "${args[0]}"
 def fsessionReader = new FileReader(fsessionsFile) 
 def userSessionDuration = [] // uid sid duration 
 /*
  * s_id    s_d     query   matched_topic   uid     TS
1       S       tetris  0       2642475 gameMob ( gameMob lucene searchEng BIRT )       2642475 2007-07-20 12:47:53.573
1       S       pingu   1       2642475 lucene  ( gameMob lucene searchEng BIRT )       2642475 2007-07-20 12:55:18.633

  */
 

 def firstSessionTS = ""
 def lastSessionTS = ""
 def previousSid = "" def previousUid = -1
 
  
 fsessionReader.splitEachLine("\t") { cols ->
  // get a new entry
  def uid = cols[4] // 4
  def sid = cols[0] // 0
  def timeStamp = cols[8] // 8
  
  if (uid.equals(previousUid)){
	  
	  if (!(previousSid.equals(sid))){
		  		  long interval = TimeUtil.secDiff(firstSessionTS, lastSessionTS)		  //debug:		  // println "uid: " + previousUid + ", sid: " + previousSid + ", interval: " + interval + " f: " + firstSessionTS + ", l:" + lastSessionTS		  userSessionDuration.add([previousUid, previousSid, interval])
		  
		  // reset old values for 
		  firstSessionTS = timeStamp
		  lastSessionTS = timeStamp
		  
		  
	  } else {
		  lastSessionTS = timeStamp
	  	  }	  
  }   // new user  else {
	  	  if(!firstSessionTS.equals("")){		  long interval = TimeUtil.secDiff(firstSessionTS, lastSessionTS)		  //debug:		  // println "uid: " + previousUid + ", sid: " + previousSid + ", interval: " + interval + " f: " + firstSessionTS + ", l:" + lastSessionTS
		  
		  userSessionDuration.add([previousUid, previousSid, interval])	  }	  // new user      // reset old values for this new uid	  firstSessionTS = timeStamp	  lastSessionTS = timeStamp
  }
  
  // done with this entry
  previousUid = uid
  previousSid = sid
  lastSessionTS = timeStamp
	 
 }
 
 def fileUserSessionDuration = fsessionsFile + "_duration.tsv"
 
 new File(fileUserSessionDuration).withWriter { out ->
	 // header
	 out.writeLine("uid\tsid\tduration")
	 userSessionDuration.each() { data ->
	 	
	 	out.write([data[0] + "\t"])
	 	out.write([data[1] + "\t"])
	 	out.write([data[2] + "\t"])
	    out.writeLine("")
	 }
 }
 
 println "done"