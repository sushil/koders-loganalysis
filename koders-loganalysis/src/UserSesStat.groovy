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
 
 def userSessionStat = [:] // [ uid : [#sessions avg_duration sd_duration first==last? #topics-from-sim-match #topics-from-topic-model]]  
 
 /*
  * s_id    s_d     query   matched_topic   uid     TS
1       S       tetris  0       2642475 gameMob ( gameMob lucene searchEng BIRT )       2642475 2007-07-20 12:47:53.573
1       S       pingu   1       2642475 lucene  ( gameMob lucene searchEng BIRT )       2642475 2007-07-20 12:55:18.633

  */
 
 // start: per user data 
 // duration is ( last activity's timestamp - first activity's timestamp ) // for each session
 def durationsInSession = [] // [duration_in_secs .. .. till_nth_session]
 def previousUid = -1 
 def firstSessionTS = ""
 def lastSessionTS = ""
  
 def numOfSessions = 1
 def firstTopic = -1
 def lastTopic = -1
 // end: per user data
 
 
 fsessionReader.splitEachLine("\t") { cols ->
  // get a new entry
  def uid = cols[4] // 4
  def sid = cols[0] // 0
  def matchedTopicCode = cols[3] // 3
  def topicListFromLDA = cols[6] // 6
  def timeStamp = cols[8] // 8
  
  if (!uid.equals(previousUid)){
	  // found a new user
	  
	  // update stats for prev user
	  if(firstTopic.equals(lastTopic)){
		  // first == last for previousUid
	  }
	  
	  
	  // reset old valuesfor this new uid
	  firstSessionTS = timeStamp
	  lastSessionTS = timeStamp
	  firstTopic = matchedTopicCode
	  lastTopic = matchedTopicCode

	  // rest durations for this user now
	  durationsInSession = []
	  
  } else {
	  
	  lastTopic = matchedTopicCode
	  
	  // entry is for the same previous user
	  int _duration = TimeUtil.secDiff(previousSessionTS,timeStamp)
	  
  }
  
  // done with this entry
  previousUid = uid
  lastSessionTS = timeStamp
	 
 }