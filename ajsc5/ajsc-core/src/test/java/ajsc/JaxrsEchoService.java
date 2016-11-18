/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.att.ajsc.beans.PropertiesMapBean;

import ajsc.ServicePropertiesMap;

@Path("/jaxrs-services")
public class JaxrsEchoService {
    @GET
    @Path("/echo/{input}")
    @Produces("text/plain")
    public String ping(@PathParam("input") String input) {
        return "Hello, " + input + ".";
    }
    
    @GET
    @Path("/property/{fileName}/{input:.*}")
    @Produces("text/plain")
    public String getProperty(@PathParam("fileName") String fileName, @PathParam("input") String input) {
    	String val=null;
    	try {
    		val = ServicePropertiesMap.getProperty(fileName, input);
    		if(val == null || val.isEmpty() || val.length() < 1){
    			val = PropertiesMapBean.getProperty(fileName, input);
    		}
    	}
    	catch(Exception ex) {
    		System.out.println("*** Error retrieving property "+input+": "+ex);
    	} 	 
    	if (val ==null) {
   		 	return "Property is not available";
    	}
    	return "Property value is, " + val +".";
    }  
    
}