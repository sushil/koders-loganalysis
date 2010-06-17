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

OperatorsStat stat = new OperatorsStat()

def fReader = new FileReader(fIp)
fReader.readLine()	
fReader.readLines().each {line ->
	
	String[] lineItems = line.trim().split("\t", 9)
	
	if (lineItems.length != 9) {
		// println "[DISCARDING] " + line
	} else {
		String[] parts = line.split("\t")
		String atype = parts[6]
		String query = parts[8]
		                     
		if(atype == "1000"){
			Set<String> operators = CheckWord.getAllOperators(query)
			operators.each { SessionDataIO.updateCount(stat.operatorQueryCounts, it) }
		}
	}
}

SessionDataIO.writeCount (root + "operatorQueryCounts"  +".txt", 
		stat.operatorQueryCounts, "operator\tQueryCounts")
println "DONE!"
