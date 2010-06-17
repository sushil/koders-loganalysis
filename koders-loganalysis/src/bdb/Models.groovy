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

/**
 * 
 */
package bdb



/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */
 public class Query implements Serializable {

	 static final long serialVersionUID = 1086812855978390307L;
	 def sessionid
	 def queryterms
	 def bestmatchtopic
	 def matchedtopics //n,t1,t1w1,...,tn,tnwn
	 boolean isSearch

	 Query(sid, qtrms, tpc, mtpcs, iss){
	 	sessionid = sid
	 	queryterms = qtrms
	 	bestmatchtopic = tpc
	 	matchedtopics = mtpcs
	 	isSearch = iss
	 }

	 public String toString() {

	        return "[Query: sessionid=" + sessionid + 
	                 " queryterms=" + queryterms + 
	                 " bestmatchtopic=" + bestmatchtopic + 
	                 " matchedtopics=" + matchedtopics +
	                 " isSearch=" + isSearch + "]";
	 }
}