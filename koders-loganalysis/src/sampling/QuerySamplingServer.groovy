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

// #!/usr/bin/env groovy -classpath ../lib/org.restlet.jar:../lib/com.noelios.restlet.jar:../bin-groovy:../lib/je-3.3.75.jar
import org.restlet.*;
import org.restlet.data.*; 
import samplequeries.SSApplication;

// Create a component  
Component component = new Component();  
component.getServers().add(Protocol.HTTP, 3000);
 

// Create an application  
SSApplication application = new SSApplication(component.getContext().createChildContext(), component)
if(args.length > 0 )	
	application.f_activity = args[0]
// component.getClients().add(Protocol.FILE);

// Attach the application to the component and start it  
component.getDefaultHost().attach(application);  
component.start();