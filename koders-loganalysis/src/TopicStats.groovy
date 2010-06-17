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


int numTopics = 50
int numCT = 20

// related topics matrix
// n1 = how many users searched for t1
// x = how many users who searched t1 also searched for t2
// ======================	
//    | t1  t2  ..  tn
// ======================
// t1 | n1  x   ..  -
// t2 | -   n2  ..
// .. | ..
// tn | ..
int[][] topicStat = new int[numTopics][numTopics+1]
def topicCodes = []

// root folder
filePrefix = "/Users/shoeseal/Scratch/LogAnalysis/koders-data/R/50/"

fNamePre = // "100-.25-.1-1k-"
			  "50t-.5a-.1b-1k-"

// inputs
tcFile = "50t.coded" // "100.cd"
tdFile = fNamePre + "VD.tsv"

// outputs
tdStatFile = fNamePre + "TS.tsv"
tdCorTFile = fNamePre + "TC.tsv"

topicLabelPrefix = "Topic"

fr = new FileReader(filePrefix + tdFile)

println "reading " + tdFile 
fr.readLines().each(){ line ->
	
	// fields in input file: 
	// uid, n+1, Entropy. t1. tp1, .. tn, tpn
	String[] fields = line.split("\t")
	
	assert fields.length > 5
	
	int uid = Integer.valueOf(fields[0]).intValue()
	int numOfTopicsInLine = Integer.valueOf(fields[1].trim()).intValue() - 1
	
	def topicsInLine = []
	
	for(int i = 3; i<(numOfTopicsInLine*2)+3; i=i+2){
		double tiProb = Double.valueOf(fields[i+1]).doubleValue()
		int ti = Integer.valueOf(fields[i].replaceFirst(topicLabelPrefix, "")).intValue()
		
		// criteria to include this topic for this user
		
		// if(tiProb >= 0.1) //#-1
		// if(tiProb < 0.1)  //#0
		
		// if(tiProb  < 0.05) //#1
		// if(tiProb >= 0.05 && tiProb < 0.1) //#2	
		// if(tiProb >= 0.1  && tiProb < 0.3) //#3
		// if(tiProb >= 0.3) //#4
			
			topicsInLine << (ti - 1)
	}
	
	topicsInLine.each(){ t1 ->
		topicsInLine.each(){ t2 ->
			topicStat[t1][t2] = topicStat[t1][t2] + 1 
		}
	}	
}

print "done creating matrix"

fr = new FileReader(filePrefix + tcFile)

println "reading " + tcFile
fr.readLines().each(){ line ->
	// fields: coded-topic, TopicXXX, ...
	String[] fields = line.split("\t")
	
	topicCodes[Integer.valueOf(fields[1].replaceFirst(topicLabelPrefix, "").trim()).intValue()-1] = fields[0]
}
println "done creating codes"

/*
for(i in 0..numTopics-1){
	println "Topic" + ((int) i+1) + "\t" + topicStat[i][i]  
}
*/

/*
new File(filePrefix + tdStatFile).withWriter { out ->
	// header
	out.write("T\tTn");
	for(i in 0..(numTopics-1)){
		out.write("\t" + topicLabelPrefix + ((int)i+1))
	}
	out.writeLine("")
	
	// data
	for(i in 0..numTopics-1){
		out.write(topicLabelPrefix + ((int) i+1) + "\t" + topicStat[i][i] )
		for(j in 0..numTopics-1){
		out.write("\t" + topicStat[i][j])	
		}
		out.writeLine("")
	}
}
*/
println "writing " + tdCorTFile
new File(filePrefix + tdCorTFile).withWriter { out ->
	// header
	// T #u 1T 1Tp .. nT nTp
	out.write("T\tU")
	for(i in 0..numCT-1){
		out.write("\t"+((int)i+1)+"T\t"+((int)i+1)+"Tp")
	}
	out.writeLine("")
	
	for(i in 0..numTopics-1){
		out.write(topicCodes[i]+"\t"+topicStat[i][i])
		
		double[] percCorTopics = new double[numTopics]
		for(j in 0..numTopics-1){
			percCorTopics[j] = 100 * (topicStat[i][j]/(double)topicStat[i][i])
		}
		int[] topNCorTIdx = Util2.topNIndicesFromArray(percCorTopics, numCT+1)
		
		for(k in 1..topNCorTIdx.length-1){
			// out.write("\t"+topicCodes[topNCorTIdx[k]]+"\t"+percCorTopics[topNCorTIdx[k]])
			out.write("\t"+topicCodes[topNCorTIdx[k]]+"\t"+topicStat[i][topNCorTIdx[k]])
		}
		
		out.writeLine("")
	}
	
}

print("Done.")
