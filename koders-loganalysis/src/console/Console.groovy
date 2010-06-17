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

package console

/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */
public class Console{

	 def static readLine() { 
		 return getNextLine() 
	 } 
	 
	 def static readString() { 
		 return getNextToken() 
	 }
	 
	 def static readInteger() { 
		 return getNextToken().toInteger() 
	 } 
	 
	 def static readDouble() { 
		 return getNextToken().toDouble() 
	 } 
	 
	 def static readBoolean() { 
		 return (getNextToken()=="true") 
	 } 
	 
	 private static String getNextToken() { 
		 if(inputLine==null) 
			 readInputLine()
			 
		 while(inputIndex==numberOfTokens) 
			 readInputLine()
			 
		return inputTokens[inputIndex++] 
	 }
	 
	 private static String getNextLine() { 
		 if(inputLine==null) 
			 readInputLine() 	 
		
		while(inputIndex==numberOfTokens) 
			readInputLine() 
			
		def line=inputTokens[inputIndex..<numberOfTokens].join(' ') 
		inputIndex=numberOfTokens 
		return line 
	 }
	 
	 private static void readInputLine() { 
	 
		 inputLine= new BufferedReader(new InputStreamReader(System.in)).readLine() 
	 
		 inputTokens=inputLine.tokenize() 
		 numberOfTokens=inputTokens.size() 
		 inputIndex=0 
	 } 
	 
	 //----- properties ------------------ 
	 private static String inputLine = null 
	 private static List inputTokens = null 
	 private static int numberOfTokens = 0 
	 private static int inputIndex = -1  
}
