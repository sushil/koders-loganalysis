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


package samplequeries;


import java.util.HashSet;
import java.io.Serializable;
import bdb.*;



class RandomGen {

Random r = new Random()

int max

def next(){
	return r.nextInt(max) 
}

}

public class Samples{

BdbAdapter bdbAdapter = null

def sessionFileName
def file = new FileReader(sessionFileName)
def topic = null

final String f_topics = "/_topics"
final String f_dbStat = "/_bdb.properties"
final String f_dbLoc  = "/_bdb/activities"
String f_dataFolder = ""

/**
	index:  0                       1       2       3       4                   5           6               7  
	header: S						s_id	s_d		query	matched_topic_code	uid			matched_topic	user_topics
	data:   2007-07-20 12:47:53.573	0		S		tetris	0					2642475		gameMob			4,gameMob,0.9975622554911273,
*/

// def activities = [:] // lineNum maps query
HashSet<String> topics = new HashSet<String>()
// initialized with the size of activities
RandomGen rg

int lines = 0

public Samples(sessionFileName){
	this.sessionFileName = sessionFileName
	f_dataFolder = new File(this.sessionFileName).getParent()
	f_dbStat = f_dataFolder + f_dbStat
	f_dbLoc = f_dataFolder + f_dbLoc
	f_topics = f_dataFolder + f_topics
}

def closeDb(){
	bdbAdapter.dispose()
}

def loadFile(){
	
	if(!new File(f_dbLoc).exists())
		assert new File(f_dbLoc).mkdirs()
		
	bdbAdapter = BdbAdapter.getInstance(f_dbLoc)
	
	// flag to recreate the berkeley DB .. needs a better one
	if(!new File(f_dbStat).exists()){
		loadBdbFromFile(sessionFileName, bdbAdapter)
	}
	
	// load number of activities
	Properties dbP = new Properties()
	dbP.load(new FileInputStream(f_dbStat))
	def rsize = dbP.getProperty("activities.records.size")
	lines = new Integer(rsize).intValue()
	rg = new RandomGen(max:lines)
	
	// load topics
	new FileReader(f_topics).readLines().each { line ->
		topics.add(line)
	}
	
}

def String changeTopic(String t){
	if(topics.contains(t)){
		topic = t
		return "Topic Changed: " + topic
	}
	else
		return "Error:" + t + " is not a valid topic."
}

def String listTopics(){
	StringBuilder _sb = new StringBuilder()
	
	topics.each(){
		_sb.append(it)
		_sb.append("\n")
	}
	
	return _sb.toString()
}

def String sortAndFormatTopics(String row){
    String[] rowitems = row.split(",")
    
    int itemCount = new Integer(rowitems[0]).intValue()
    
    def weightedItems = []
    
    (0..itemCount-1).each() {
        int _start = it * 3 + 1
        weightedItems.add(new WeightedItem(rowitems[_start], rowitems[_start+1]))    
    }
    
    StringBuffer matchedTopics = new StringBuffer()
    
    weightedItems.sort{it.weight}.reverse().eachWithIndex(){ it, i -> 
        if(i>0) {
            matchedTopics.append(",")
        } 
    
        matchedTopics.append(it.name)
        matchedTopics.append(",")
        matchedTopics.append(String.format('%.4f', it.weight)) 
    }
    
    return matchedTopics.toString()
        
}

def String getNextSample(){
	
	StringBuilder _sb = new StringBuilder()
	
	int _startLine = getTopicQueryLineNum()
	int _endLine = _startLine
	int _endLineBack = _startLine - 1
	
	while(!( (_endLineBack<0)
			|| !(bdbAdapter.getSampleActivity(_startLine).sessionid == bdbAdapter.getSampleActivity(_endLineBack).sessionid)
			|| !bdbAdapter.getSampleActivity(_endLineBack).isSearch )) {
		_sb.append("   " 
				    + bdbAdapter.getSampleActivity(_endLineBack).queryterms
				    + "\t" + bdbAdapter.getSampleActivity(_endLineBack).matchedtopics )
		_sb.append("\n")
		_endLineBack--
	}
	
	// stop on download or next session
	while(!( !bdbAdapter.getSampleActivity(_endLine).isSearch 
			   || !(bdbAdapter.getSampleActivity(_startLine).sessionid == bdbAdapter.getSampleActivity(_endLine).sessionid) )){
		String _label = (_startLine == _endLine)?"-> ":"   " 
		_sb.append(   _label  
					+ bdbAdapter.getSampleActivity(_endLine).queryterms + "\t" 
					+ bdbAdapter.getSampleActivity(_endLine).matchedtopics)
		_sb.append("\n")
		_endLine++
	}
	
	// print download if previous loop terminated on hitting download
	if( !bdbAdapter.getSampleActivity(_endLine).isSearch
			&& bdbAdapter.getSampleActivity(_startLine).sessionid == bdbAdapter.getSampleActivity(_endLine).sessionid ){
		_sb.append("   http://www.koders.com/kv.aspx?fid=" 
				     + bdbAdapter.getSampleActivity(_endLine).queryterms )
		_sb.append("\n")		
	}
	
	return _sb.toString()
}

def printSample(){
	
	println getNextSample()
	
}

private int getTopicQueryLineNum(){
	int _line = rg.next()

	Query _q = bdbAdapter.getSampleActivity(new Integer(_line))
	assert _q!=null
	
	if(_q.bestmatchtopic == topic)
		return _line
	else
		return getTopicQueryLineNum()
}

private void loadBdbFromFile(String filename, BdbAdapter bdbAdapter) {
   
   println "-- loading data into BDB --"
	
   HashSet<String> _topics = new HashSet<String>()
   
	LineNumberReader lineNumberReader = null;
   
   try {
       
       //Construct the LineNumberReader object
       lineNumberReader = new LineNumberReader(new FileReader(filename));
       
       int _lineNum = 0
       String line = null;
       
       // throw the header
       line = lineNumberReader.readLine()
       
       while ((line = lineNumberReader.readLine()) != null) {
           
       	String[] cols = line.trim().toString().split("\t");
			if(cols.length == 8) {
				String _matchedTopics = "";
				
				if (cols[2].equals("S"))
					_matchedTopics = sortAndFormatTopics(cols[7])
					
				Query q = new Query(cols[1], cols[3], cols[6], _matchedTopics, cols[2]=="S")
				_topics.add(cols[6])
				bdbAdapter.addQueryInSampleActivities(new Integer(_lineNum++), q)
			}
       }
       
       
       println " ..writing size in properties"
       // write properties
       new File(f_dbStat).write("activities.records.size=" + _lineNum)
       println " ..writing topics"
       // write topics
       new File(f_topics).withWriter { out ->
       	_topics.eachWithIndex() { _t, i ->
       		if(! _t.equals("NA") ) {
           		if(i==(_topics.size() - 1)) 
           			out.write(_t)
           		else
           			out.writeLine(_t)
       		}
       	}
       }
       
   } catch (FileNotFoundException ex) {
       ex.printStackTrace();
   } catch (IOException ex) {
       ex.printStackTrace();
   } finally {
       //Close the BufferedWriter
       try {
           if (lineNumberReader != null) {
               lineNumberReader.close();
           }
       } catch (IOException ex) {
           ex.printStackTrace();
       }
   }
   
   println "-- loaded data into BDB --"
}


}

class WeightedItem {

    public String name;
    public double   weight;

    public WeightedItem(String name, String weight){
        this.name = name;
        try{
        	this.weight = new Double(weight).doubleValue();
        }
        catch(NumberFormatException nfe){
        	// TODO is this enough ?
        	this.weight = -1.0;
        }

    }

}
