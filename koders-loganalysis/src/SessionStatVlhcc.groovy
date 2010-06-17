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


def root = //  "${args[0]}" 
	// "/Users/shoeseal/sandbox/ews/loganalysis-scripts/data/sessionstat/"
	"/Users/shoeseal/DATA/koders/sessionstat/"
def sfPath = root + 
	//  "${args[1]}" 
	// "top.100.txt"
	"uid-type-ts.test.txt"

File sessionFile = new File(sfPath)



println "counting lines"
// count lines so that we can track progress 
new File(sfPath).eachLine { Status.lines = Status.lines + 1 }

println "started processing " + Status.lines + " lines"
println "building session stat: "

Status.reset(Status.lines)

UserSessionBuilder sessionBuilder = new UserSessionBuilder()
sessionBuilder.loadFile(sessionFile)

// counts to print

// Activities count
// -----------------

// How many total activities are there in a session
// total activities in session
def numActsInSessions = [:] // [ num_activities : count]

// How many search activities are made before a download
// search that precedes download in session
def numSearchB4DinSessions = [:] // [ num_search : count ]

//How many downloads are made in one session ?
//download activities that follow a search session
def numDownloadsInSSessions = [:] // counts of consecutive downloads that follow after search in sessions

// How many search activities are there in sessions without downloads ?
// search activities in session without downloads
def numSearchWODinSessions = [:] // [ num : count ]

// How many download activities are there in case where session starts with download
// download activities w/o search in session (some sessions start with downloads, this count is for those)
def numDownloadWOSinSessions = [:] // [ num : count ]



// Duration count
// --------------

// How much time is spent in each session
def numSessionsWithDuration = [:] // [ session_duration : count]
// How much time is spent searching before a download
def numSearchSessionPartWithDuration = [:] // [ time_spent_in_search_b4_d : count]
// How much time is spent in consecutive downloads
def numDInSessionWithSWithDuration = [:]
// How much time is spent in search where there are no downloads
def numSearchSessionPartWoDDuration = [:]
// How much time is spent in downloads without preceeding search? This is the
// case where the session starts with download
def numDInSessionWithoutSWithDuration = [:]

println ""
println "updating: "

Status.reset(sessionBuilder.userSessionStats.size())

sessionBuilder.userSessionStats.each(){ k, v ->
	
	/** debug
	print k + " "
	println v.numSInSWithoutD + " " + v.numDInSWithoutS
	print "S: "
	v.numSBeforeD.each(){ 
		print it + " "
	}
	println ""
	print "D: "
	v.numDinD.each(){
			print it + " "
	}
	println ""
	println ""
	*/
	
	updateCountFromSession(v.numActs, numActsInSessions)
	updateCountFromSession(v.numSBeforeD, numSearchB4DinSessions)
	updateCountFromSession(v.numSInSWithoutD, numSearchWODinSessions)
	updateCountFromSession(v.numDInSWithoutS, numDownloadWOSinSessions)
	updateCountFromSession(v.numDinD, numDownloadsInSSessions)
	
	updateCountFromSession(v.sDurationBeforeD, numSearchSessionPartWithDuration)
	updateCountFromSession(v.sDuration, numSessionsWithDuration )
	updateCountFromSession(v.sDurationWoD, numSearchSessionPartWoDDuration )
	updateCountFromSession(v.sDurationInD, numDInSessionWithSWithDuration )
	updateCountFromSession(v.sDurationInDWoS, numDInSessionWithoutSWithDuration )
	
	
	Status.update()
}

println ""
println "--"

writeFile(root + "act.txt", numActsInSessions, "act\tcount" )
numActsInSessions.clear()
numActsInSessions = null

writeFile(root + "sB4D.txt", numSearchB4DinSessions, "sB4D\tcount" )
numSearchB4DinSessions.clear()
numSearchB4DinSessions = null

writeFile(root + "sWoD.txt", numSearchWODinSessions, "sWoD\tcount" )
numSearchWODinSessions.clear()
numSearchWODinSessions = null

writeFile(root + "dWoS.txt", numDownloadWOSinSessions, "dWoS\tcount" )
numDownloadWOSinSessions.clear()
numDownloadWOSinSessions = null

writeFile(root + "dAfterS.txt", numDownloadsInSSessions, "dAfterS\tcount" )
numDownloadsInSSessions.clear()
numDownloadsInSSessions = null

writeFile(root + "sB4D_dur.txt", numSearchSessionPartWithDuration, "sB4D.dur\tcount" )
numSearchSessionPartWithDuration.clear()
numSearchSessionPartWithDuration=null

writeFile(root + "act_dur.txt", numSessionsWithDuration, "act.dur\tcount" )
numSessionsWithDuration.clear()
numSessionsWithDuration = null

writeFile(root + "sWoD_dur.txt", numSearchSessionPartWoDDuration, "sWoD.dur\tcount" )
numSearchSessionPartWoDDuration.clear()
numSearchSessionPartWoDDuration = null

writeFile(root + "dAfterS_dur.txt", numDInSessionWithSWithDuration, "dAfterS.dur\tcount" )
numDInSessionWithSWithDuration.clear()
numDInSessionWithSWithDuration = null

writeFile(root + "dWoS_dur.txt", numDInSessionWithoutSWithDuration, "dWoS.dur\tcount" )
numDInSessionWithoutSWithDuration.clear()
numDInSessionWithoutSWithDuration = null
	
println "Done"

// =============================================================================================
def updateCountFromSession(sessionProperty, countMap){
	sessionProperty.each(){ num ->
		if(countMap[num]==null){
			countMap[num]=1
		} else {
			countMap[num] = countMap[num] + 1
		}
	}
}

def writeFile(fileName, sessionProperty, header){
	println "writing file: " + fileName
	assert sessionProperty.size() > 0
	Status.reset(sessionProperty.size())
	new File(fileName).withWriter { out ->
		 // header
		 out.write(header)
		 sessionProperty.each() { k,v ->
		 	out.write("\n" + k + "\t" + v)
		 	Status.update()
		}
	}
	println ""
}

//println "sB4d\tcount"
//numSearchB4DinSessions.each() { k,v ->
//	println k+"\t"+v
//}
//
//println "sWOd\tcount"
//numSearchWODinSessions.each() { k,v ->
//	println k+"\t"+v
//}
//
//println "dWOs\tcount"
//numDownloadWOSinSessions.each() { k,v ->
//	println k+"\t"+v
//}
//
//println "dInS\tcount"
//numDownloadsInSSessions.each() { k,v ->
//	println k+"\t"+v
//}
//
//println "sDurB4D\tcount"
//numSearchSessionPartWithDuration.each() { k,v ->
//	println k+"\t"+v
//}
//
//println "sDur\tcount"
//numSessionsWithDuration.each() { k,v ->
//	println k+"\t"+v
//}
//
//println "sDurWoD\tcount"
//numSearchSessionPartWoDDuration.each() { k,v ->
//	println k+"\t"+v
//}
//
//
//println "numActs\tcount"
//numActsInSessions.each() { k,v ->
//	println k+"\t"+v
//}
//
//
//println "durDWiS\tcount"
//numDInSessionWithSWithDuration.each() { k,v ->
//	println k+"\t"+v
//}
//
//
//println "durDWoS\tcount"
//numDInSessionWithoutSWithDuration.each() { k,v ->
//	println k+"\t"+v
//}


/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */

public class UserSessionBuilder{
	 
	 def userSessionStats = [:] // [uid : UserSessionStat]
	 
	 public void loadFile(file){
		 
		 def activities = []
		 // read file line by line
		 // keep building activities, if new user encountered 
		 // build user stat
		 def fsessionReader = new FileReader(file)
		 
		 // skip header
		 fsessionReader.readLine()
		 Status.processed = Status.processed + 1
		 
		 def pastUid = "-1"
		 fsessionReader.splitEachLine("\t") { cols ->
		
		 
//		 	print cols.size() + " "
//		 	print "1st: " + cols[0] 
//		 	print cols[0]
//		 	Thread.sleep(1000)
		 	
		 println cols
			assert cols.size() == 3
			
		 	def uid = cols[0]
		 	int type = Integer.parseInt(cols[1])
			 def ts = cols[2]
			 
			 // new user or the first line from file
			 if(!uid.equals(pastUid)){
				 
				 // print uid + " " + pastUid + " \n"
				 
				 // new user and not the first line form file
				 if (! (activities.size() < 1) ){
					 // update the existing activities
					 // this will not update the last set of activities
					 userSessionStats[pastUid] = UserSessionStat.buildStat(pastUid, activities)
					 // make space for new activities
					 activities = []
					 
					 
				 }
			 }
			 
			 activities.add(new Activity(type:type,timestamp:ts))
			 pastUid = uid
			 
			 Status.update()
			 
			 
			 
		 }
		 
		 // treat the last line
		 // udpate the last activities
		 userSessionStats[pastUid] = UserSessionStat.buildStat(pastUid, activities)
		 
	 }
	 
 }

 
public class UserSessionStat{
	 
	def numActs = []
	
	// D = download, S = search
	 
	 // list size should be equal to the number of sessions
	 // that had downloads for this user
	 def numDinD = []
	 def numSBeforeD = []
	 
	 def sDurationBeforeD = []
	 def sDurationWoD = []
	 def sDuration = []
	 def sDurationInD = []
	 def sDurationInDWoS = []
	 
	 // can be only one session with S but no D
	 // counts the num of searches in such session
	 def numSInSWithoutD = []
	 // can be only one session with D but no search
	 // counts the num of downloads in such session
	 def numDInSWithoutS = []
	 
	 def userid
	 
	 // there will be at least one session with one search
	 def sessions = null
	 
	 public void initSessions(userid, activities){
		 this.userid = userid
		 
		 sessions = []
		 buildSessionsFromActivities(activities)
		 updateStatsFromSessions(sessions)
		  
	 }
	 
	 private buildSessionsFromActivities(activities){
		 assert sessions != null
		 // return list of sessions by reading activities
		 // each session 
		 Session s = new Session()
		 int lastActivityType = 0
		 for(Activity a : activities){
			
			 // dump current session, create new one
			 if(lastActivityType == 1001 && a.type == 1000){
				 sessions.add(s)
				 s = new Session()
			 }
			 
			 if(a.type==1000){ 
				 if(lastActivityType == 0 || lastActivityType == 1001){
					 s.ts_ss = a.timestamp
				 }
				 s.ts_se = a.timestamp
				 s.searches = s.searches + 1
			 }
			 else if(a.type==1001){ 
				 if(lastActivityType == 0 || lastActivityType == 1000){
					 s.ts_ds = a.timestamp
				 }
				 s.ts_de = a.timestamp
				 s.downloads = s.downloads + 1
			 }
			 
			 lastActivityType = a.type		 
		}
		 
		// dump the last session
		sessions.add(s)
			
		
		 	
	 }
	 
	 private updateStatsFromSessions(sessions){
		 // look at each session and update the four session fields
		 // of this class
		 for(Session s: sessions){
			 
			 // print s.searches + " " + s.downloads + "\n"
			 
			 numActs.add(s.searches + s.downloads)
			 
			 if(s.downloads>0 && s.searches==0){
				 numSInSWithoutD.add(-9999)
			 }
			 
			 if(s.searches>0){
				 if(s.downloads==0){ 
					 numSInSWithoutD.add(s.searches)
					 sDurationWoD.add(TimeUtil.secDiff(s.ts_ss, s.ts_se))
				 } else {
					 numDinD.add(s.downloads)
					 numSBeforeD.add(s.searches)
					 sDurationBeforeD.add(TimeUtil.secDiff(s.ts_ss, s.ts_se))
					 sDurationInD.add(TimeUtil.secDiff(s.ts_ds, s.ts_de))
				 }
				 
				 
			 
			 } else { 
				 numDInSWithoutS.add(s.downloads)
				 sDurationInDWoS.add(TimeUtil.secDiff(s.ts_ds, s.ts_de))
			 }
			 
			 String _s = "", _e = ""
			 if(s.searches > 0) {
				 _s = s.ts_ss
				 if(s.downloads > 0){ 
					 _e = s.ts_de
				 } else {
					 _e = s.ts_se
				 }
			 } else {
				 _s = s.ts_ds
				 _e = s.ts_de
			 }
			 
			 sDuration.add(TimeUtil.secDiff(_s, _e))
			
		 }
		 
	 }
	 
	 public static UserSessionStat buildStat(uid, activities){
		 UserSessionStat instance = new UserSessionStat()
		 instance.initSessions(uid, activities)
		 return instance
	 }
	 
}

 public class Activity{
	 int type
	 def timestamp
 }
 
 public class Session{
	 // the consecutive searches that start the session
	 // a session could start with a download, in that case
	 // this will be 0
	 // for a user, only the first session can start with a download
	 def searches = 0
	 // the consequetive downlods that follow the searches
	 // can be 0
	 // a session can end without a download, in that case this will be
	 // 0
	 def downloads = 0
	 
	 // session: ss, .. , se, ds, .. , de
	 // timestamp search start
	 def ts_ss = null
	 // timestamp search end
	 def ts_se = null
	 def ts_ds = null
	 def ts_de = null
	 
 }
 
 