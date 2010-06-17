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

def termsList = [ "r", "g", "y", "b", "w" ]
def topicTermDist = [ 
           [0.1, 0.1, 0.01, 0.25, 0.54 ],
           [0.003, 0.23, 0.32, 0.25, 0.197],
           [0.25, 0.4, 0.04, 0.12, 0.31]
         ]

def q1 = "r r"
def q2 = "r r r y"
def q3 = "y w"
def q4 = "b b g"

// gets the query termsList and topic indices that belong to a 
// user (document) and returns the idx that matches the most
def getMostSimTopic = { q, topicIdList ->
  
  def maxScore = 0.0
  int idx = 0
  
  def findTermIndex = { term ->
  	def foundIdx = -1 
	for (t in (0..termsList.size()-1)) {
		// println termsList[t] + " : " + term
		if(termsList[t].trim().equals(term.trim())) { 
			foundIdx = t
			break
		}
	 }	
	 return foundIdx
  }

  topicIdList.each(){ topicTermDistIdx ->   
    
    def simScore = 1
    
    q.split("\\s").each() { t ->
       	int tIdx = findTermIndex(t)
       	simScore = simScore * topicTermDist[topicTermDistIdx][tIdx] 
    }
   
   println "topicTermDist-idx: " + topicTermDistIdx + " simscore: " + simScore + " maxscore: " + maxScore
   if (simScore > maxScore) { 
	   maxScore = simScore
	   idx = topicTermDistIdx 
   }
  }
  
  println q + " :MS " + maxScore + ", " + idx
  return idx
  
}


println "q1:"
println  getMostSimTopic(q1, [0,1,2])
println "q2:"
println getMostSimTopic(q2, [0,1,2])
println "q3:"
println getMostSimTopic(q3, [0,2])
println "q4:"
println getMostSimTopic(q4, [0,2])