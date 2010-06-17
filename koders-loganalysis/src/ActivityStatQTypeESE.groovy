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

public class ActivityStatQTypeESE{
	
	// 
	int totalS
	int totalD
	
	// lang | #query
	def langFilterQueryCount = [:]
	
	// count queries (activities)
	// qtype  | #queries
	//    NatLang Q == L
	//    Code Q    == C
	//    Hybrid Q  == H 
	def qtypeQCount = [:]
	
	// queryType | #sessionsStart with query type
	def queryTypeStartsSes = [:]
	
	// count the downlod activities
	//    prevQueryType | #Downloads
	def queryTypeFollowsDownloads = [:]
	
	// query type pattern that precedes download
	def qtypePatternDCount = [:]
	
	def kodersExampleQ = ['"public event"',
	'foreachtag',
	'xmlre*',
	'xmlre* -xmlreader',
	'smtp server',
	'cdef:parser',
	'mdef:insert',
	'idef:configuration',
	'cdef:tree   mdef:insert']                          
	
}