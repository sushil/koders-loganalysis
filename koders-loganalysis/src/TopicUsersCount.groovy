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

import java.util.HashSet

def statFile = "${args[0]}"
def outFile = statFile.replace(".tsv","_tusers.tsv")

def topicUsers = [:] // [ [topic1 : [uid1 uid2 ..] ] .. ]

def fr = new FileReader(statFile)
// skip header
String terms = fr.readLine()

fr.readLines().each(){ line ->
	def cols = line.split("\t")
	def uid = cols[4]
	def topic = cols[5]
	
	if(!topicUsers.containsKey(topic)){
		topicUsers[topic] = new HashSet()
	}
	
	topicUsers[topic].add(uid)
	
}

/*
topicUsers.each(){ t, u ->
	print t
	print "\t" + u.size()
	println ""
}
*/


// write stat for all runs
new File(outFile).withWriter { out ->
	// header
	// out.writeLine("Topic\tnum_users")
	topicUsers.each(){ topic, users ->
		out.write(topic + "\t")
		out.write(users.size() + "")
		out.writeLine("")
	}
}

println "done."