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


def root = "${args[0]}"
def fIp  = root + "${args[1]}"

def fReader = new FileReader(fIp)

String lastUid = ""

int uidStartWithD = 0	
int numUsers = 0

// todo count
int numS = 0
int numD = 0
int numDFollowS = 0
int numDFollowD = 0
int numDAsFirstAct4Users = 0

String lastAType = ""

fReader.readLine()	
fReader.readLines().each {line ->
	
	String[] lineItems = line.trim().split("\t", 9)
	
	if (lineItems.length != 9) {
		// println "[DISCARDING] " + line
	} else {
		String [] parts = line.split("\t")
		def uid = parts[0]
		def atype = parts[6]
		
		if(atype == "1000"){ 
			++numS
		}
		else{ 
			++numD
		}
		
		if (uid != lastUid ){ 
			++numUsers
			
			if(atype == "1001"){ 
				++uidStartWithD
				++numDAsFirstAct4Users
			}
		} else {
			// same user
			if(atype == "1001"){
				
				assert lastAType != ""
				
				if(lastAType == "1001"){
					++numDFollowD
				} else if(lastAType == "1000"){
					++numDFollowS
				}
			}
		}
		
		lastUid = uid
		lastAType = atype
	}
}

println "----"
println "# total users: " + numUsers
println "# Users starting with Downloads: " + uidStartWithD
println "# search activities: " + numS
println "# download activities: " + numD
println "# downloads that follow search: " + numDFollowS
println "# downloads that follow downloads: " + numDFollowD
println "# downloads that are first activities for users: " + numDAsFirstAct4Users

println "----\nDONE!"