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

package sampling

// #!/usr/bin/env groovy -classpath ../bin-groovy:../lib/je-3.3.75.jar

/**
 * 
 */
import console.*


import bdb.*;
import samplequeries.*;
/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */

 
if(args.length<1){
	println "..needs an input, will exit"
	System.exit(0)
}
 
Samples s = new Samples("${args[0]}")
println "loading session file.."
s.loadFile()
s.topic = "Struts"

println "loaded " + s.lines + " lines."

def ip = ""

def usage =  "Q for quit, TC:<topic> to pool from another topic, TL: to list topics" +
	  		  "\n.. anything else to continue\n>>> "

println "\n Current Topic: " + s.topic
println   " -------------"
s.printSample()
print usage	  

while(!(ip=Console.readString()).equals("Q")){
	
	if(ip.startsWith("TC:")) {
		
		String _t = ip.replaceFirst("TC:","")
		println s.changeTopic(_t)
			
	} else if(ip.startsWith("TL:")) {

		println s.listTopics()

	} else {
		println "\n Current Topic: " + s.topic
		println   " -------------"
		s.printSample()
	}
	
	println ""
	print usage
	
}

s.closeDb()
print "bye."



