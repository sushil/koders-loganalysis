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

import org.restlet.*;
import org.restlet.data.*; 

public class RandomSample extends Restlet {
	
	def void handle(Request request, Response response) {
	   	
		Samples _s = getApplication().getContext().getAttributes().get(SSApplication.ATTR_SAMPLES)
		String _topic = request.getAttributes().get("topic")
		String _result = _s.changeTopic(_topic)
		
		if(! _result.startsWith("Error:"))
			_result = _s.getNextSample()
		
		if (request.method == Method.GET) {
			response.setEntity( _result, MediaType.TEXT_PLAIN)
	   	} else {
	   		// The request method is not GET, so set an error response status
			response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED)
			response.setAllowedMethods([Method.GET] as Set)
		}
	}
 }
 
public class CurrentTopic extends Restlet {
	 
	 def void handle(Request request, Response response) {
		   	if (request.method == Method.GET) {
		   		Samples _s = getApplication().getContext().getAttributes().get(SSApplication.ATTR_SAMPLES)
				String _result = _s.topic
		   		response.setEntity( _result, MediaType.TEXT_PLAIN)
			} else {
				// The request method is not GET, so set an error response status
				response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED)
				response.setAllowedMethods([Method.GET] as Set)
			}
		} 
 }
 
public class ListTopics extends Restlet {
	 
	 def void handle(Request request, Response response) {
		   	if (request.method == Method.GET) {
		   		Samples _s = getApplication().getContext().getAttributes().get(SSApplication.ATTR_SAMPLES)
				String _result = _s.listTopics()
		   		response.setEntity( _result, MediaType.TEXT_PLAIN)
			} else {
				// The request method is not GET, so set an error response status
				response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED)
				response.setAllowedMethods([Method.GET] as Set)
			}
		}
 }


 
 