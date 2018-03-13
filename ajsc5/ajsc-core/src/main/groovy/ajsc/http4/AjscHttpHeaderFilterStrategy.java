/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.http4;

import org.apache.camel.http.common.HttpHeaderFilterStrategy;

public class AjscHttpHeaderFilterStrategy 
extends HttpHeaderFilterStrategy {
	protected void initialize() {
        
        super.initialize();
        getOutFilter().remove("date");
//getOutFilter().remove("content-length");
//getOutFilter().remove("content-type");
//// Add the filter for the Generic Message header
//// http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.5
//getOutFilter().remove("cache-control");
//getOutFilter().remove("connection");
//getOutFilter().remove("date");
//getOutFilter().remove("pragma");
//getOutFilter().remove("trailer");
//getOutFilter().remove("transfer-encoding");
//getOutFilter().remove("upgrade");
//getOutFilter().remove("via");
//getOutFilter().remove("warning");
//

}
	 
}
