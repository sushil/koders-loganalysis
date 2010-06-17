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

def root =  "/Users/shoeseal/DATA/koders/"
           //  "/home/sushil/Scratch/LogAnalysis/ese-data/"
def fIp = root + 
	// "alltest"
	// "all-susers.txt"
	//   "all-susers-testneg.txt"
	"neg-test2.txt"

def fReader = new FileReader(fIp)

//session duration counts
def d2s = [:] // download to search triggers session change 
def min6  = [:] // six min interval triggers session change


def getDuration(cur, last){

  return TimeUtil.minDiff(last, cur);
}

def updateCount(amap, akey){
  if(amap.containsKey(akey)){
    amap[akey] = amap[akey] + 1
  } else {
    amap[akey] = 1
  }
}


def writeCount(filename, amap, intervaltype){
  new File(filename).withWriter {out ->
  out.writeLine(intervaltype + "\tcount")
  amap.each {interval, count ->
    out.writeLine( String.format('%d',interval) + "\t" + count)
  }

}
}

// start script

def lastUid = ""
def lastActType = ""

def firstActTS = ""
def lastActTS = ""

def S2D_sesStartTS = ""	
def min6_sesStartTS = ""
	
          
	
//skip header
fReader.readLine()

fReader.readLines().each {line ->

  String[] lineItems = line.trim().toString().split("\t", 9)
  if (lineItems.length != 9) {
	  println "[DISCARDING] " + line
  } else {

    def curUid = lineItems[0]
    def curTS  = lineItems[7]

    def curActType = lineItems[6]

    if(lastActTS=="") lastActTS = curTS
    def duration = getDuration(curTS, lastActTS)

    // computing session duration when D->S breaks a session
    if(S2D_sesStartTS == "") S2D_sesStartTS = curTS
    if( (curUid == lastUid) && (lastActType == "1001" && curActType == "1000") ||
    		(curUid != lastUid)){
         def sesDuration = TimeUtil.secDiff(S2D_sesStartTS, lastActTS)
         if(sesDuration < 0) println line
         updateCount(d2s, sesDuration)
         S2D_sesStartTS = curTS
    }

    
    // computing session duration when 6 min interval breaks a session
    if(min6_sesStartTS == "") min6_sesStartTS = curTS
    if( (curUid == lastUid) && (TimeUtil.minDiff(lastActTS,curTS)>6) ||
    		(curUid != lastUid)){
         def sesDuration = TimeUtil.secDiff(min6_sesStartTS, lastActTS)
         
         // debug error condition
         if(sesDuration < 0) {
                println "--"
                println line
                println sesDuration + " " + min6_sesStartTS + " " + lastActTS
                println TimeUtil.secDiff(min6_sesStartTS, lastActTS)
        }

         
         updateCount(min6, sesDuration)
         min6_sesStartTS = curTS
    }
   
    // updates for next state
    lastActType = curActType
    lastUid = curUid
    lastActTS = curTS
  }
  
}


def fOp = root + "op_d2s-count.txt"
writeCount(fOp, d2s, "d2s")
fOp = root + "op_min6-count.txt"
writeCount(fOp, min6, "min6")


println "----\nDONE!"

