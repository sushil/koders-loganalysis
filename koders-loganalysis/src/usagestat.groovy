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


// i/p:  activity_id, timestamp, user_id, terms
// 	  sorted first on user_id, then on timestamp

def root = "/Users/shoeseal/sandbox/ews/loganalysis-scripts/data/"
def fIp = root + "usage-stats-terms.txt"

def fReader = new FileReader(fIp)

def usrStat = [:] // [uid : [max_terms, max_consecutive_Q_repeats ] ]
def termStat = [:] // [term : #users using it]

def cur_user = ""
def current_users_terms = [] // [term1 term2 ..]

def cur_usr_Q_repeat= 0
def cur_usr_max_Q_repeat = 0

def current_Q = ""

fReader.readLines().each{ line ->
	

	String[] lineItems = line.trim().toString().split("\t", 4)
	if (lineItems.length != 4) {
	} else {
	String query = lineItems[3]
	String[] terms = query.split("\\s")
	
	def new_user = lineItems[2]
	def new_terms = terms.length

	// new user
	if(! new_user.equals(cur_user)){
 	
		// update term stat based on current user's terms
		current_users_terms.each(){ term ->
			if(termStat.containsKey(term))
				termStat[term] = termStat[term] + 1
			else
				termStat[term] = 1
		}


		// update the max consecutive queries for current_user
		if(cur_user!=""){
			usrStat[cur_user] = 
				[ usrStat[cur_user][0],
			  		cur_usr_max_Q_repeat ]
		}

		current_users_terms.clear()

		cur_usr_max_Q_repeat = 0		
		// make a record for this new user
		usrStat[new_user] = [new_terms, cur_usr_max_Q_repeat]

	} else {
	// not a new user
		
		def old_max_terms = usrStat[new_user][0]
		
		// update new max # of terms for this user
		if(new_terms > old_max_terms)
			usrStat[new_user][0] = new_terms

		// this query same as pervious one
		if(query.equals(current_Q)){
			cur_usr_Q_repeat = cur_usr_Q_repeat + 1
		}else{
		// this query different than previous one
			if(cur_usr_max_Q_repeat < cur_usr_Q_repeat){
				cur_usr_max_Q_repeat =
					cur_usr_Q_repeat
			}

			cur_usr_Q_repeat = 0
		}
			
	}

	terms.each() { _term ->
		if(! current_users_terms.contains(_term))
				current_users_terms.add(_term)
	}
	
	cur_user = new_user
	current_Q = query
}
}

// write op

println "# users " + usrStat.size()
println "# terms " + termStat.size()

def f_usrStat = root + "usrstat.txt"
def f_termStat = root + "termstat.txt"

new File(f_usrStat).withWriter { f_usrStat_out ->
	f_usrStat_out.writeLine("uid\tterms\trepeat")
	usrStat.each() { _uid, _udata ->
		f_usrStat_out.write(_uid + "\t" + _udata[0] + "\t" + _udata[1])
		f_usrStat_out.writeLine("")
	}
}

new File(f_termStat).withWriter { f_termStat_out ->
	f_termStat_out.writeLine("term\tusers")
	termStat.each() { _term, _users ->
		f_termStat_out.write(_term + "\t" + _users)
		f_termStat_out.writeLine("")
	}
}

println "!!! DONE !!!"
