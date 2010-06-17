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

public class SSApplication extends Application {
	 
	 static final String ATTR_SAMPLES = "samples"
	 //static final String ROOT_URI = "file://Users/shoeseal/sandbox/ews/loganalysis-scripts/src/samplesviz/samplesviz/applet/"
	 // static final String ROOT_URI = "file:///opt.skb/applet/"
	 static String f_activity = "/Users/shoeseal/Scratch/LogAnalysis/koders-data/tc_all/1226541912234/fsessions_9999_test.tsv"
	 
	 Samples s = new Samples(f_activity)
	 
	 Map env = new HashMap<String, Object>()
	 
	 private Component starter
	 
	 public SSApplication(Context context, Component c){
		 super(context)
		 
		 starter = c
		 
	 	 println "loading session file.."
	 	 s.loadFile()
	 	 s.topic = "Struts"
	 
	 	 context.getAttributes().put(ATTR_SAMPLES, s)
	 }
	 
	 public Restlet createRoot() {
	   	// Create a root router  
	    Router router = new Router(getContext());  
	  
	    // Attach the handlers to the root router  
	    router.attach("/random_sample?topic={topic}", new RandomSample());  
	    router.attach("/current_topic", new CurrentTopic());  
	    router.attach("/list_topics", new ListTopics());  
//	    router.attach("/samplevis/", new Directory(getContext(), ROOT_URI));
//	    router.attach("/admin/stop", this)
	  
	    // Return the root router  
	    return router;  
   }
	 
//	 def void handle(Request request, Response response) {
//		   	if (request.method == Method.GET) {
//		   		s.closeDb()
//		   		response.setEntity(">> shutting down server.. bye \n", MediaType.TEXT_PLAIN)
//		   		stopStarter()
//		   		
//			} else {
//				 The request method is not GET, so set an error response status
//				response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED)
//				response.setAllowedMethods([Method.GET] as Set)
//			}
//		}
//	 
//	private stopStarter(){
//		 starter.stop()
//	 }
}