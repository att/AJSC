/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.rest;

import org.apache.camel.component.restlet.RestletHeaderFilterStrategy;
import org.restlet.engine.header.HeaderConstants;


public class AjscRestletHeaderFilterStrategy extends RestletHeaderFilterStrategy {
	
	public AjscRestletHeaderFilterStrategy() {
		getOutFilter().add("Content-length");
		// Remove the restlet headers from the out message.
	    getOutFilter().add(HeaderConstants.ATTRIBUTE_HEADERS);
				
    }
	 
}
