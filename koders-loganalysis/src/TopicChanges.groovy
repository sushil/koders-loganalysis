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
 * @author skb
 * arguments: <home_folder> <run_folder> <T|F>
 * example:   /Users/shoeseal/ tc-test  T==trace/F=don't_trace
 * 
 */

import java.util.Date;
import java.util.GregorianCalendar;
import java.lang.Math;	

// home folder
// def home = "/Users/shoeseal/"
def home = "${args[0]}"
// def runFolder = "tc-test"
def runFolder = "${args[1]}"
Globals.TRACE = "${args[2]}".equals("T")?true:false

// data folder
def fData = home + "Scratch/LogAnalysis/koders-data/" + runFolder + "/"
def fOutputData = fData + System.currentTimeMillis() + "/"

// files

// needs to be sorted by user ids
def fUserActivities = fData + "uact.tsv"

def fTermTopicDist = fData + "ttd.tsv"
def fTopicCode = fData + "tc.code"

// uid n=#topics E t1 tp1 ... tn tpn
def fUserTopicList = fData + "utl.tsv"


// data structures
def topicCode = []        // [code1 .. coden]
def userTopicList = [:]   // [uid: [t1 .. tn]]  // mapped list has just the topics for the user
def userTopicProb = [:]   // [uid: [t1_prob .. tn_prob]] // mapped list has all the topics for the user

def sessions = []         // [ [session_id, search/d, query, matched_topic, uid, timestamp] .. ]
def sessionStat = []      // [ [session_id : [uid, first_topic, last_topic, num_dwnlds, num_search, num_topics]] .. ]
def topicSessionStat = [:] // [ session_interval: [#sessions_started_with #sessions_ended_with #dwnlds_ended_with] .. ]

int[] topicSearchActCount = new int[Constants.NUM_OF_TOPICS]  // [ #search-act-for-topic-1 .. #search-act-topic-n ]
double[] topicSearchActWeightCount = new double[Constants.NUM_OF_TOPICS]  // [ #search-act-for-topic-1 .. #search-act-topic-n ]

int[] topicUserCount =  new int[Constants.NUM_OF_TOPICS] // 

def topicFollowedCount = [:] // [session_interval: [ 51*51]]
def topicFollowedCountWeighted = [:] // [session_interval: [ 51*51]]


// cache for sym KL Div
def termProbDistForQTerms =  [:] //  [ <qterms> : <double array; [term_1_prob .. term_n_prob]>   ] 
def eucDistQTermsVsTopics = [:] //  [ <qterms> : <double array; [topic_1_dist .. topic_n_dist]> ]

 // [QTrms: simTopicidx ] this is per user


// aggregate session stat
// def termsList = []     // [term1 term2 ... ]ok

def termsIdxMap    = [:]	  // [ term:idx .. ]
def topicTermDist  = []    // [ [topic1-term1-prob t1t2p ... ] [ t2t1p ... ] .. ]
def topicTermRanks = []   // [ [topic1-term1-rank t1t2R .. ] .. ]

AggregateSessionStat aggStat = new AggregateSessionStat()

// Gets the uid and the processed query terms list
// 	and returns the idx that matches the most.
// pQTerms has all whitespaces replaced with space before loadSessions calls this
def getMostSimTopic = { uid, pQTerms ->

	def q = pQTerms
	def topicIdList = userTopicList[uid] 

	double maxScore = 0.0
	double minScore = -1.0
	int idx = -1
	
    //	 create entry if cache if not there
    /*
	if(eucDistQTermsVsTopics[pQTerms]==null) {
		eucDistQTermsVsTopics[pQTerms] = new double[Constants.NUM_OF_TOPICS]
		// bootstrap the cache entry
		for(int i=0; i<Constants.NUM_OF_TOPICS; i++){
			eucDistQTermsVsTopics[pQTerms][i] = -1
			// println "eucDistQTermsVsTopics[i] : " + pQTerms + " " + i + " " + eucDistQTermsVsTopics[pQTerms][i]
		}
	}
	*/	
	
	def findTermIndex = { term ->
		// println "found term: " + term
		def foundIdx = -1 
		
	    def _result = termsIdxMap[term]
		if (_result != null)
			foundIdx = _result
		// println "at: " + foundIdx
		return foundIdx
	}
	
	def getEuclideanDistance = { double[] pdist, double[] qdist ->
		
		// debug print "distance: " + MyUtil.euclidean(pdist, qdist)
		return MyUtil.euclidean(pdist, qdist)
		
	}
	
	
	def getTermProbDistForQueryTerms = { queryTerms ->
		
		// look in cache
		double[] qtermsProbDist = termProbDistForQTerms[queryTerms]
		
		// not found in cache
		if (qtermsProbDist == null){
			//debug print queryTerms + "prob dist not found for: " + queryTerms  
			
			//{{{ start: compute
			
			qtermsProbDist = new double[termsIdxMap.size()]
			queryTerms.split(" ").each() { t->
				if(t.length()>0){
					int tIdx = findTermIndex(t)
					qtermsProbDist[tIdx] = qtermsProbDist[tIdx] + 1 
				}
			}
			
			// normalize freq to prob
			double sum = 0
			for(int i=0; i < qtermsProbDist.length; i++) {
				sum = sum + qtermsProbDist[i]
			}
			
			for(int i=0; i < qtermsProbDist.length; i++){
				qtermsProbDist[i] = qtermsProbDist[i]/sum 
			}
			
			//}}} end: compute
			
			// update cache
			// termProbDistForQTerms[queryTerms] = qtermsProbDist
		}
		
		/*
		// debug
		else
			print queryTerms + "prob dist found for: " + queryTerms
		*/
		
		return qtermsProbDist
	}
	
	// this is the only working sim function for now
	def getSimBasedOnTermRanksOnUserTopics = {
		def weightedTopics = [:] // [topic-idx : weight/similarity_score]
			
		// calculate similarity score for the candidate topic
		topicIdList.each(){ topicTermDistIdx ->   
		 
			 double simScore = 1.0
			// double simScore = 0
			
			 // println "topic index: " + topicTermDistIdx
			 // for this topic aggrerate the score for each term
			 // in the query
			 q.split(" ").each() { t ->
			 	if(t.length()>0) {
			    	int tIdx = findTermIndex(t)
			    	// term from the query must be found in the term list
			    	// println t + " " + tIdx
			    	if(tIdx < 0) print t + " : in Query: " + q
			    	assert tIdx >= 0
			    	
			    	// debug: print " topictermdist: " + topicTermDist[topicTermDistIdx][tIdx]
			    	// P(R) / P(!R)
			    	// simScore = simScore * (topicTermDist[topicTermDistIdx][tIdx] / (1 - topicTermDist[topicTermDistIdx][tIdx]))
			    	
			    	simScore = simScore * topicTermDist[topicTermDistIdx][tIdx]
			    	
			    	// debug: print " topictermrank: " + topicTermRanks[topicTermDistIdx][tIdx]
			    	// simScore = simScore + topicTermRanks[topicTermDistIdx][tIdx] + 1
			    	// debug: print " simscore: " + simScore
			 	}
			 }
			 
			 assert simScore != 1
			
			 if (idx==-1) idx = topicTermDistIdx // bootstrap
			 
			 // use the topic prob for user too in similarity score
			 // debug: print " userTopicProb: " + userTopicProb[uid][topicTermDistIdx] + "\n"
			 simScore = simScore * userTopicProb[uid][topicTermDistIdx] 
			 
			 weightedTopics[topicTermDistIdx] = simScore
			 
//			 debug: println "topicTermDist-idx: " + topicTermDistIdx + " simscore: " + simScore + " maxscore: " + maxScore
			if (simScore > maxScore) { 
				   maxScore = simScore
				   idx = topicTermDistIdx 
			}
		}
		
		// println q + " :MS " + maxScore + ", " + idx
		// return idx
		
		return [idx, weightedTopics]
	}
	
	def getSimBasedOnEucDistOnUserTopics = {
		
		// debug print "terms: " + pQTerms
		double simScore = 0.0
		
		topicIdList.each() { topicTermDistIdx ->
		
			// check score in cache
//			double _score = eucDistQTermsVsTopics[pQTerms][topicTermDistIdx].doubleValue()
			
			// debug print " _score: " + _score
			
			// found score in the cache
//			if(_score > -1){ 
//				simScore = _score
				// debug println " found_score: " + _score
//			} 
			// did not find the score in the cache
//			else {
			
				double[] qtermProbDist = getTermProbDistForQueryTerms(pQTerms)
				// double[] ttermProbDist = MyUtil.ListToDoubleArray(topicTermDist[topicTermDistIdx])
				double[] ttermProbDist = topicTermDist[topicTermDistIdx]
				simScore = getEuclideanDistance(qtermProbDist, ttermProbDist)
				
				assert simScore > 0
				//debug: println " simscore from euc: " + simScore
				
				// update score cache
//				eucDistQTermsVsTopics[pQTerms][topicTermDistIdx] = simScore
				
//			}
			// lower the KL div similar are the topics
			
			// bootsrap. simScores are never -ve
			if(minScore <= -1) {
				//debug: println "minscore bootstrapped to: " + simScore 
				minScore = simScore
				idx = topicTermDistIdx
			}
			
			
			if (simScore < minScore) { 
			   minScore = simScore
			   idx = topicTermDistIdx 
			}
		
		} // end: for each topic this user serached in
		
		return idx
	}
	
	// def _uidTrmsTopicSim = [:] // [uid : [qterms: t] ]
	
	
	def getSimBasedOnLangModel = {
	// SC(Q, D_i) = PI_tj-belongsto-Q P(t_j | M_Di) * PI_tj-notbelongsto-Q (1 - P(tj | M_Di))
	
	// debug: println "cache size: " + Globals.QTrmsTopicSim.size() + ", q: " + q
	// Globals.QTrmsTopicSim
    if(! (Globals.QTrmsTopicSim[q]==null) ){
    	// debug
    	// println "..found sim topic in map" 
    	idx = Globals.QTrmsTopicSim[q]
    	 
    } else{
			 
	// calculate similarity score for the candidate topic
		topicIdList.each(){ topicTermDistIdx ->   
		
			 /// def simScore = 1
			 double simScore = 0
			 
			 // terms in query
			 def termIdxsInQuery = []
			 TreeSet uniqTermIdxsInQuery = new TreeSet()
			 
			 
			 q.split(" ").each() { t ->
			 	if(t.length()>0) {
			    	int tIdx = findTermIndex(t)
			    	termIdxsInQuery.add(tIdx)
			    	uniqTermIdxsInQuery.add(tIdx)
			    	// term from the query must be found in the term list
			    	// println t + " " + tIdx
			    	//if(tIdx < 0) 
			    		// debug: println t + " : in Query: " + q + " :" + tIdx
			    	assert tIdx >= 0
			 	}
			 } // end q.split
			 
			 // {{{ debug: print "uniqTermIdxsInQuery: "
			 // uniqTermIdxsInQuery.each() {print it + " "}
			 // println ""
			 // }}}
			 
			 int[] termIdxsNotInQueryArr = new int[termsIdxMap.size() - uniqTermIdxsInQuery.size()]
			 
			 int _arrI = 0
			 (0..<termsIdxMap.size()).each() { _i ->
 				 if (uniqTermIdxsInQuery.size()>0 && _i == uniqTermIdxsInQuery.first()){
 					uniqTermIdxsInQuery.remove(uniqTermIdxsInQuery.first())
 				 } else {
 					termIdxsNotInQueryArr[_arrI] = _i
 					 _arrI++
 				 }
			 }
			 
			 assert _arrI == termIdxsNotInQueryArr.length 
			 
			 // first bootstrap
			 def termIdxsNotInQuery = new HashSet(termsIdxMap.size(), 1)
			 // get all the indices
			 termsIdxMap.entrySet().each() { entry ->
				 termIdxsNotInQuery.add(entry.getValue())
			 }
			 // remove those in the query
			 termIdxsInQuery.each() { _t ->
			 	// debug: print _t + " "
			 	termIdxsNotInQuery.remove(_t)
			 }
			 // debug: print " << t in Q"
			 
			 // {{{ debug: println " "
				 // termIdxsNotInQuery.each() { __t ->
				 // debug: print __t + " "
				 // }
			 // }}} debug: println " << t not in Q"
			
			 double probTermsBelongToQuery = 1.0
			 double probTermsNotBelongToQuery = 1.0
			 
			 termIdxsInQuery.each(){ _termInQ ->
			 	probTermsBelongToQuery = probTermsBelongToQuery * topicTermDist[topicTermDistIdx][_termInQ]
			 }
			 
			 // using array instead of a map
			 for (int j=0; j<termIdxsNotInQueryArr.length; j++){
				 probTermsNotBelongToQuery = probTermsNotBelongToQuery * (1.0 - topicTermDist[topicTermDistIdx][termIdxsNotInQueryArr[j]])
			 }
			 
			 /*
			 termIdxsNotInQuery.each(){ _termNotInQ ->
			 	// // debug: println _termNotInQ + " "
			 	probTermsNotBelongToQuery = probTermsNotBelongToQuery * (1.0 - topicTermDist[topicTermDistIdx][_termNotInQ])
			 }
			 */
			 
			 simScore = probTermsBelongToQuery * probTermsNotBelongToQuery
			 
			 assert (simScore > 0.0 && simScore < 1.0)
			 
			// // debug: println "topicTermDist-idx: " + topicTermDistIdx + " simscore: " + simScore + " maxscore: " + maxScore
			if (simScore > maxScore) { 
				   maxScore = simScore
				   idx = topicTermDistIdx 
			}
		}// end topicIdList.each()
		
		// debug: println "updating.. " + "Globals.QTrmsTopicSim[" + q + "] =" + idx
		Globals.QTrmsTopicSim[q] = idx
    	}// end else, not found in map
		// // debug: println q + " :MS " + maxScore + ", " + idx
		return idx
	
	}
	
	// idx = getSimBasedOnLangModel()
	// idx = getSimBasedOnTermRanksOnUserTopics()
	// idx = getSimBasedOnEucDistOnUserTopics()
	
	getSimBasedOnTermRanksOnUserTopics()

}

/// START SCRIPT EXECUTION

def start = System.currentTimeMillis()

println "loading term topic distribution"
loadTermTopicDist(fTermTopicDist, termsIdxMap, topicTermDist)
println "loading topic codes"
loadTopicCode(fTopicCode, topicCode)

// debug:
Globals.TOPIC_CODE = topicCode

println "loading user topic list"
loadUserTopicList(fUserTopicList, userTopicList, userTopicProb)

// debug:
Globals.USER_TOPIC_LIST = userTopicList
Globals.USER_TOPIC_PROB = userTopicProb

// {{{ enable this if using topic term ranks in computing similarity 
println "building topic term ranks"
buildTopicTermRanks(topicTermDist, topicTermRanks)
// }}}

// checkTermSearch(fUserActivities, termsIdxMap)


// loadSessions(fUserActivities, sessions, topicSessionStat, getMostSimTopic, aggStat, 3)


/*
println "Topic Terms Distribution"
topicTermDist.each(){ t ->
	t.each() {print it + "\t"}
	println ""
}
println " --------- "
println "Topic Terms Rank"
topicTermRanks.each(){ t ->
	t.each() {print it + "\t"}
	println ""
}
println " --------- "
*/

// generate sessions with these many intervals
// special values:
//    1    ==  a day's interval
//    9999 ==  all user's activities in one session (per user)
def runIntervals = [1, 180, 9999]
// def runIntervals = [ 1 ]

def aggStatForRuns = [:] // [session_interval : #sessions #search #downloads #started-with-D #started-with-S #ended-with-D #ended-with-S #D-only-ses #s-only-ses ]

Globals.NUM_OF_TERMS = termsIdxMap.size()
println "total terms in Vocabulary: " + Globals.NUM_OF_TERMS

// count activities
int _l = 0
new File(fUserActivities).eachLine { _l++ }
Globals.NUM_OF_ACTIVITIES = _l
println "reading acivities from: " + fUserActivities
println "total activities: " + Globals.NUM_OF_ACTIVITIES


println "creating output folder: " + fOutputData
new File(fOutputData).mkdir()



//== Reports ==
runIntervals.each() { interval ->
	println "loading sessions with interval: " + interval + " mins"
	aggStat = new AggregateSessionStat()	sessions = []	//print sessions.size() + "=\t"+ interval +"=="
	
	loadSessions( fUserActivities, sessions, topicSessionStat, 
			      getMostSimTopic, aggStat, interval, 
			      topicSearchActCount, topicSearchActWeightCount, 
			      topicFollowedCount, topicFollowedCountWeighted,
			      termProbDistForQTerms, eucDistQTermsVsTopics)
	
	println "writing reports for sessions with interval: " + interval + " mins"
	//	 write out session statistics
	def fSessionStat = "fsessionStat_" + interval + ".tsv"
	new File(fOutputData + fSessionStat).withWriter { out ->
		out.writeLine("Agg. stat")
		out.writeLine("total sessions: " + aggStat.totalSessions)
		out.writeLine("search activities: " + aggStat.numOfSearch)
		out.writeLine("download activities: " + aggStat.numOfDownload)
		out.writeLine("sessions started with dwnlds: " + aggStat.sessionsStartedWithDwnlds)
		out.writeLine("sessions started with searches: " + (aggStat.totalSessions - aggStat.sessionsStartedWithDwnlds) )
		out.writeLine("sessions ended with dwnlds: " + aggStat.sessionsEndedWithDwnlds)
		out.writeLine("sessions ended with searches: " + (aggStat.totalSessions - aggStat.sessionsEndedWithDwnlds) )
		out.writeLine("download only sessions: " + aggStat.downloadOnlySessions )
		out.writeLine("search only sessions: " + aggStat.searchOnlySessions )
		out.writeLine("")
		
		out.writeLine("Topic Session Stat:")
		out.writeLine("Topic\ts-start\ts-end\td-end\tnum-searches\tweighted-searches")
		(0..topicSessionStat[interval].size()-1).each() { tIdx ->
			if (topicSessionStat[interval][tIdx] == null) {
				out.write(topicCode[tIdx] + "\t" + "NA" + "\t" + "NA" + "\t" + "NA")
				out.write("\t" + topicSearchActCount[tIdx])
			} else {	
				out.write(topicCode[tIdx])
				topicSessionStat[interval][tIdx].each() {out.write("\t" + it)}
				
				out.write("\t" + topicSearchActCount[tIdx])
				out.write("\t" + topicSearchActWeightCount[tIdx])
			}
			out.writeLine("")
		}
				Integer _num = new Integer(interval)		
		aggStatForRuns[_num] = [    		                            aggStat.totalSessions, 		                            aggStat.numOfSearch, 		                            aggStat.numOfDownload, 
		                            aggStat.sessionsStartedWithDwnlds, 		                            (aggStat.totalSessions - aggStat.sessionsStartedWithDwnlds),
		                            aggStat.sessionsEndedWithDwnlds, 		                            (aggStat.totalSessions - aggStat.sessionsEndedWithDwnlds),
		                            aggStat.downloadOnlySessions, 		                            aggStat.searchOnlySessions		                         ]
		
	}
	
	//	 write out sessions
	def fSessions = "fsessions_"+ interval +".tsv"
	new File(fOutputData + fSessions).withWriter { out ->

		// [session_id, _sOrD, _trmOrFid, _topicIdx, _uid, now, _wtopics]
		out.writeLine("TS\ts_id\ts_d\tquery\tmatched_topic_code\tuid\tmatched_topic\tuser_topics")
		
		sessions.each() { r ->
			out.write(r[5] + "\t")
			(0..r.size()-3).each(){ out.write(r[it] + "\t") }
			if(r[3]==-1) 
				out.write("NA")
			else
				out.write(topicCode[r[3]])	
					
			def uTList = userTopicList[r[4]]
		    out.write("\t")
		    out.write(uTList.size()+"") 
		    uTList.each() { ut -> 
		    	out.write("," + topicCode[ut] + "," + r[6][ut] + "," + Globals.USER_TOPIC_PROB[r[4]][ut]) 
		    }
			out.writeLine("")
		}
	}
}

// write stat for all runs
new File(fOutputData + "fsessionsRunStat.tsv").withWriter { out ->
	// header
	out.writeLine("ses_interval\tsessions\tsearches\tdownloads\tstarted-with-D\tstarted-with-S\tended-with-D\tended-with-S\tD-only-ses\ts-only-ses")
	aggStatForRuns.each(){ interval, data ->		// print "--"+ interval +"--"
		out.write(interval + "")
		data.each(){
			out.write("\t" + it)
		}
		out.writeLine("")
	}
}

println "writing tables for topic followed"
topicFollowedCount.each(){ key,value ->
	// println "topic followed count for interval: " + key
	new File(fOutputData + "topicFollowed_" + key + ".tsv").withWriter { out ->
		for(int i=0; i<51; i++){
			for(int j=0; j<51; j++){
				
				out.write("\t" + value[i][j])
			}
			
			// don't put an empty line as the last row
			if(i<Constants.NUM_OF_TOPICS) out.writeLine("")
		}
	
	}	
}

println "writing tables for topic followed - weighted"
topicFollowedCountWeighted.each(){ key,value ->
	// println "topic followed count for interval: " + key
	new File(fOutputData + "topicFollowedWeighted_" + key + ".tsv").withWriter { out ->
		for(int i=0; i<51; i++){
			for(int j=0; j<51; j++){
				
				out.write("\t" + value[i][j])
			}
			
			// don't put an empty line as the last row
			if(i<Constants.NUM_OF_TOPICS) out.writeLine("")
		}
	
	}	
}


def end = System.currentTimeMillis()

println "Done processing.\nTotal " + TimeUtil.printElapsedTime(start, end)

// == End: Reports ==

// debug
// topicCode.each() { print it + "\t"}
/*
userTopicList.each() { u, tl ->
	print u + " : "
	tl.each() {print topicCode[it] + "\t"}
	println ""
}
*/

// println("Total sessions: " + sessionStat.size())
println "\n----\ndone"


// == methods ==
def buildTopicTermRanks(topicTermDist, topicTermRanks) {
	topicTermDist.each() { topic ->
		double[] termProb = new double[topic.size()]
		(0..topic.size()-1).each() { idx ->
			termProb[idx] = topic[idx].doubleValue()
		}
		int[] ranks = Util2.elementRank(termProb, topic.size())
		topicTermRanks.add(ranks)
	}
}
	
	
def loadTermTopicDist(fTTD, termsIdxMap, topicTermDist){
	def fr = new FileReader(fTTD)
	// load terms
	String terms = fr.readLine()
	int i = 0
	terms.split("\t").each() {
		termsIdxMap[it] = i++
		// termsList[i++] = it
	}
	// load terms prob dist for each topic
	i = 0
	fr.readLines().each(){ pd ->
		def _prob = []
		pd.split("\t").each(){ p ->
			_prob.add(new Double(p).doubleValue())
		}
		topicTermDist[i++] = _prob
	}
}


def loadUserTopicList(fUserTopicList, userTopicList, userTopicProb){
	def fr = new File(fUserTopicList)
	// uid n=(#topics + 1) E t1 tp1 ... tn tpn
	fr.splitEachLine("\t") { cols ->
		def uid = cols[0]
		int nTopics = new Integer(cols[1]).intValue()
			// file has number of topics + 1
			nTopics = nTopics - 1
		def tList = []
		userTopicProb[uid] = []
		int i = 0
		(0..nTopics-1).each(){ tidx ->
			// strip 'N' from 'TopicN'
			int _t = new Integer(cols[3 + i].replace("Topic","").trim()).intValue()
			// topic numbering starts from 1 in the file
			tList[tidx] = _t - 1
			userTopicProb[uid][_t - 1] = new Double(cols[3 + i + 1]).doubleValue() 
			i = i + 2
		}
		userTopicList[uid] = tList
		
	}
}


def loadTopicCode(fTopicCode, topicCode) {
	def fr = new File(fTopicCode)
	fr.splitEachLine("\t") { cols ->
		int tIdx = new Integer(cols[1].replace("Topic","").trim()).intValue()
		topicCode[tIdx-1] = cols[0]
	}
}


def loadSessions( fAct, sessions, topicSessionStat, getMostSimTopic, 
		          aggStat, sessionInterval, topicSearchActCount, topicSearchActWeightCount, 
		          topicFollowedCount, topicFollowedCountWeighted, 
		          termProbDistForQTerms, eucDistQTermsVsTopics ) {
	
	int activitiesSoFar = 0
	def fr = new File(fAct)
	int session_id = 0
	int yesterday = 0
	int today = 0
	
	topicFollowedCount[sessionInterval] = new int[51][51]
	topicFollowedCountWeighted[sessionInterval] = new double[51][51]
	
	topicSessionStat[sessionInterval] = []
	aggStat.sessionsStartedWithDwnlds = 0
	aggStat.sessionsEndedWithDwnlds = 0
	boolean pickedFirstSearch = true
	
	String now = ""
	String before = ""
	
	// topic session stat updating closures
	// update topicSessionStat 
	// [ [#sessions_started_with #sessions_ended_with #dwnlds_ended_with] .. ]
	
	def incTopicTermStat = { topicIdx, statIdx ->
		def _stat = topicSessionStat[sessionInterval][topicIdx]
		if (_stat == null) {
			_stat = [0, 0, 0]
			topicSessionStat[sessionInterval][topicIdx] = _stat
		}
		topicSessionStat[sessionInterval][topicIdx][statIdx] = topicSessionStat[sessionInterval][topicIdx][statIdx] + 1
	}
	
	def incStartedWith = { tIdx ->
		incTopicTermStat(tIdx, 0)
	}
	
	def incEndedWith = { tIdx ->
		incTopicTermStat(tIdx, 1)
	}
	
	def incDownloadFollowed = { tIdx ->
		incTopicTermStat(tIdx, 2)
	}
	// end: topic session stat updating closures
	
	def lastUid = "-1"
	int lastTopicIdx = -1
	def lastTopicsWeights = [:]
	String previousActivityType = ""
	int previousSearchSessionId = -1
	int previousSessionId = -1
	int numOfSearchesInPreviousSession = 0
	int numOfDownloadsInPreviousSession = 0
	String _sOrD = ""
	
	// 0        1   2       3   4   5   6   7
	// Terms	sID	lang	d	m	y	ts	uid
	fr.splitEachLine("\t") { cols ->
		
		// processed one more activity
		activitiesSoFar++
		// {{{ trace:
		// def _actStart = System.currentTimeMillis()
		// }}}
	
		// println "no of cols in act: " + cols.size()
		if(cols.size() == 8){
			
			// info extracted per line (activity)
			
			// default values for similar topic (also indicates the activity is a download if -1)
			int _topicIdx = -1
			def _wtopics = [:]
			
			// activity type
			_sOrD = ""
			// terms from query or file id from download
			String _trmOrFid = cols[7]
			// language chosen (for search) or project id (for download)
			String _langOrProjId = cols[2].trim()
			// true if download
			boolean _isDownload = (_langOrProjId ==~ /-{0,1}[0-9]+/)
			// user id
			def _uid = cols[0]
			
			// {{{ enable this if using vector based similarity (NOT WORKING anyway)
			//minimize use of cache per user
			if(! _uid.equals(lastUid)) {
				//		termProbDistForQTerms = [:]
				//		eucDistQTermsVsTopics = [:]
				//debug: println "clearing.."
				Globals.QTrmsTopicSim = [:]
			}
			//}}}
			
			// get most similar topic if it is a search activity
			if(!_isDownload) {
				
				String _processedQryTerms = _trmOrFid.trim().replaceAll("[^\\w]"," ").replaceAll("_"," ").trim()
				_processedQryTerms = _processedQryTerms.replaceAll("\\s+"," ")
				
				if (_processedQryTerms.trim().length()==0){
					_topicIdx = 11 //UNKNOWN as the query term is simply blank
					// println "<BLANK>"
				} else {	
					
					// matched topic with highest weight
					_topicIdx = getMostSimTopic(_uid, _processedQryTerms)[0]
					
					// all topics weighted
					// calculate weighted matched-topic list to append at the end
					def __weights = getMostSimTopic(_uid, _processedQryTerms)[1]
					def __wtotal = 0.0
					__weights.values().each(){ __v ->
						__wtotal = __wtotal + __v
					}
					__weights.each() { _tIdx, _weight ->
						_wtopics[_tIdx] =  (_weight/__wtotal)
					}	
					
					
					/*
					// debug
					def _weights = getMostSimTopic(_uid, _processedQryTerms)[1]
					println "uid:" + _uid +" # of matched topics:  " + _weights.size()
					def _wtotal = 0.0
					
					_weights.values().each(){ _v ->
						_wtotal = _wtotal + _v
					}
					
					print "\t"+ _weights.size()
					_weights.each(){ k,v ->
							print "," + Globals.TOPIC_CODE[k] + "," + (v/_wtotal)
					}
					print "\n"
					// end: debug
					 
					*/
					
				}
				
				if (_topicIdx < 0 )
					println "ERROR:" +  _uid + " " + _processedQryTerms
				
				assert _topicIdx >= 0
				
				_sOrD = "S"
				
				// TODO updateSearchCount
				aggStat.numOfSearch++
				
				// only need to get the counts once for each of the intervals
				// but make sure '1' is one of the intervals
				if (sessionInterval==1){
					// update search count based on best topic matched
					topicSearchActCount[_topicIdx] = topicSearchActCount[_topicIdx] + 1
					
					// update search count based on weighted matched-topics
					_wtopics.each(){ _tidx, _w ->
						topicSearchActWeightCount[_tidx] = topicSearchActWeightCount[_tidx] + _w
					}
					
				}
				
				if(!pickedFirstSearch){
					incStartedWith(_topicIdx)
					pickedFirstSearch = true
				} 
				
			} else {
				_sOrD = "D"
				
				// TODO updateDownloadCount
				aggStat.numOfDownload++
				
				// only consider downloads after search in the same session
				if (previousActivityType == 'S' && _uid.equals(lastUid) 
						&& previousSearchSessionId>0 && (previousSearchSessionId == session_id)) {
					assert lastTopicIdx >= 0 
					incDownloadFollowed(lastTopicIdx)
				}
			}
			
			// bootstrap previous activity type
			if(previousActivityType.equals("")) previousActivityType = _sOrD
			
			// compute session
			String _ts = cols[6]
			// for time-based-session, each session can be a day
			now = _ts
			
			// bootstrap before for the first activity
			if (before.equals("")) before = _ts
			// bootstrap uid for the first activity
			if (lastUid == "-1") lastUid = _uid
			
			// if you are using a day as a session
			today = new Integer(_ts.split(" ")[0].replaceAll("-","")).intValue()
			
			// if time-based-session changes or user changes, produce a new session
			
			boolean breakSession = false
			
			if (sessionInterval==1){
				breakSession = ((today > yesterday && _uid.equals(lastUid)) || ! _uid.equals(lastUid))
			} else if (sessionInterval==9999) {
				breakSession = ! _uid.equals(lastUid)
			} else {
				breakSession = ( TimeUtil.minDiff(before, now) >= sessionInterval || ! _uid.equals(lastUid) )
			}
			
			

			// if (today > yesterday || _uid != lastUid) { // if using a day as a session
			
			// 15 mins interval between sessions			// print "== " + sessionInterval + " =="
			/* if ( TimeUtil.minDiff(before, now) >= sessionInterval || ! _uid.equals(lastUid)) { */
			if (breakSession) {	
			        
          			 	
				// preceeding session ends
				if (numOfSearchesInPreviousSession > 0 && lastTopicIdx >= 0) { 
					// updateLastTopic
					incEndedWith(lastTopicIdx)
				}
				
				if (previousActivityType == "D") {
					aggStat.sessionsEndedWithDwnlds++
				}
				
				if (numOfSearchesInPreviousSession == 0 && numOfDownloadsInPreviousSession>0){
					aggStat.downloadOnlySessions++
				}
				
				if (numOfDownloadsInPreviousSession == 0 && numOfSearchesInPreviousSession>0){
					aggStat.searchOnlySessions++
				}
		
				// new session starts	
				session_id++
				numOfSearchesInPreviousSession = 0
				numOfDownloadsInPreviousSession = 0
					
				if (_sOrD == "S") {
					assert _topicIdx >= 0
					incStartedWith(_topicIdx)
					pickedFirstSearch = true
				} else {
					assert _sOrD == "D"
					aggStat.sessionsStartedWithDwnlds++
					pickedFirstSearch = false
				}
			} // if break session
			
			lastUid = _uid
			
			assert _sOrD != ""
			
			
			// making current data the previous one
			yesterday = today	
            before = now
           
			if (_sOrD == "S") {
				numOfSearchesInPreviousSession++
				
				// {{{ trace: print matched topic for query terms
				// print _uid + "\t" + "Q:(" + _trmOrFid + ")\t" + "T:" + Globals.TOPIC_CODE[_topicIdx]
				// def _uTList = Globals.USER_TOPIC_LIST[_uid]
			    // print "\t("
			    // _uTList.each() { print " " + Globals.TOPIC_CODE[it] }
			    // println ")"
			    // }}} end trace
			}
			else
				numOfDownloadsInPreviousSession++
            
			// debug
			// println "sid: " + session_id + " s/d: " + _sOrD + " topic: "+ _topicIdx + " prevAct: " + previousActivityType + " prevSid: " + previousSessionId 
			sessions.add([session_id, _sOrD, _trmOrFid, _topicIdx, _uid, now, _wtopics] )
			
				
			if(previousSessionId == session_id && previousActivityType=="S"){
				if(_sOrD.equals("D")){
					// debug
					// println "download followed search " + lastTopicIdx + " , old value " + topicFollowedCount[sessionInterval][lastTopicIdx][Constants.NUM_OF_TOPICS]
					topicFollowedCount[sessionInterval][lastTopicIdx][Constants.NUM_OF_TOPICS] = topicFollowedCount[sessionInterval][lastTopicIdx][Constants.NUM_OF_TOPICS] + 1
					
					lastTopicsWeights.each(){ tRow, wRow ->
							topicFollowedCountWeighted[sessionInterval][tRow][Constants.NUM_OF_TOPICS]  = topicFollowedCountWeighted[sessionInterval][tRow][Constants.NUM_OF_TOPICS] + wRow 	
					}
					
					//  
					
				} else {
					// debug
				    // println _topicIdx + " > search followed search >" + lastTopicIdx + " , old value " + topicFollowedCount[sessionInterval][lastTopicIdx][_topicIdx]
					topicFollowedCount[sessionInterval][lastTopicIdx][_topicIdx] = topicFollowedCount[sessionInterval][lastTopicIdx][_topicIdx] + 1
					
					lastTopicsWeights.each(){ tRow, wRow ->
						_wtopics.each() { tCol, wCol ->
							topicFollowedCountWeighted[sessionInterval][tRow][tCol]  = topicFollowedCountWeighted[sessionInterval][tRow][tCol] + (wRow * wCol)	
						}
				    }	
					
				}
			}
			
			
			if(_sOrD.equals("S"))  { 
				lastTopicIdx = _topicIdx
				lastTopicsWeights = _wtopics
				
				// println "prev: " + previousSearchSessionId + " curr:" + session_id
				previousSearchSessionId = session_id
			}
			
			previousActivityType = _sOrD
			previousSessionId = session_id
			
			// update session stat
			// [ [session_id : [uid, first_topic, last_topic, num_dwnlds, num_search, num_topics]] .. ]
	
			} // end if col size 8
	
		// {{{ trace
		  // _actEnd = System.currentTimeMillis()
		  // println "processed activity " + activitiesSoFar + " in time: " + TimeUtil.printElapsedTime(_actStart, _actEnd)
		// }}}
		
	} // end split each line
	
	
	// the last line
	if (numOfSearchesInPreviousSession>0 && lastTopicIdx >= 0) incEndedWith(lastTopicIdx)
	
	if (numOfSearchesInPreviousSession == 0 && numOfDownloadsInPreviousSession>0){
		aggStat.downloadOnlySessions++
	}
	
	if (numOfDownloadsInPreviousSession == 0 && numOfSearchesInPreviousSession>0){
		aggStat.searchOnlySessions++
	}
	
	if (_sOrD == 'D') aggStat.sessionsEndedWithDwnlds++
	aggStat.totalSessions = session_id + 1

} // end load sessions



// diagnostic

def checkTermSearch(fAct, termsIdxMap) {
	
	def findTermIndex = { term ->
		// println "found term: " + term
		def foundIdx = -1 
		def _r = termsIdxMap[term]
		if(_r!=null) foundIdx = _r
		// println "at: " + foundIdx
		return foundIdx
	}
	
	f = new File(fAct)
	f.splitEachLine("\t") { cols ->
		String _trmOrFid = cols[7]
		
		String _langOrProjId = cols[2].trim()
		boolean _isDownload = (_langOrProjId ==~ /-{0,1}[0-9]+/)
		          
		if(!_isDownload) {
			String _processedQryTerms = _trmOrFid.trim().replaceAll("[^\\w]"," ").replaceAll("_"," ").trim()
			_processedQryTerms = _processedQryTerms.replaceAll("\\s+"," ")
			
			_processedQryTerms.split(" ").each() { trm ->
				
				println trm + "[" + trm.length() + "]"
				if (trm.length() > 0) {
					assert findTermIndex(trm) >= 0
				}
				
			}
			println ""
		}
 }
}


class AggregateSessionStat {
	public int sessionsStartedWithDwnlds = 0
	public int sessionsEndedWithDwnlds = 0
	public int totalSessions = 0
	public int numOfSearch = 0;
	public int numOfDownload = 0;
	public int downloadOnlySessions = 0;
	public int searchOnlySessions = 0;
}

class OneSessionStat {
	String startActType = ""
	String endActType = ""
	int firstSearchTopic = -1
	int lastSearchTopic = -1
	int numOfSearch = 0 
	int numOfDownload = 0
	def uid = "-1"
	String startTimeStamp = ""
	String endTimeStamp = ""
}



class Constants {
	public static int NUM_OF_TOPICS = 50 
}

class Globals {
	public static int NUM_OF_ACTIVITIES = 0
	public static int NUM_OF_TERMS = 0
	
	public static def TOPIC_CODE = null
	public static def USER_TOPIC_LIST = null
	public static def USER_TOPIC_PROB = null
	public static def QTrmsTopicSim = [:]
	
	public static boolean TRACE = false
}



