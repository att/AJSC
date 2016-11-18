/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.restlet;

import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.engine.header.Header;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessRestletHeaders {

	static final Logger logger = LoggerFactory
			.getLogger(ProcessRestletHeaders.class);

	public static void processHeaders(Exchange e) throws Exception {

		e.setOut(e.getIn());
		Message msg_out = e.getOut();
		if (e.getIn().getHeader("org.restlet.http.headers") != null) {
				HttpRequest httpRequest = e.getIn().getHeader(
						RestletConstants.RESTLET_REQUEST, HttpRequest.class);
				Series<Header> headerList = (Series<Header>)httpRequest.getAttributes().get("org.restlet.http.headers");
				Set<String> headerNames = headerList.getNames();

				for (String headerName : headerNames) {
					String val = headerList.getFirstValue(headerName);
					msg_out.setHeader(headerName, val);
					logger.debug("headerName: " + headerName + " value: " + val);
				}
				msg_out.removeHeader("org.restlet.http.headers");
				if (e.getIn().getHeader("CamelHttpQuery") != null) {

					String queryString = e.getIn().getHeader("CamelHttpQuery",
							String.class);
					String[] params = queryString.split("&");
					for (String param : params) {
						String[] splitParam = param.split("=");
						msg_out.setHeader(splitParam[0], splitParam[1]);
						logger.debug("headerName: " + splitParam[0]
								+ " value: " + splitParam[1]);
					}

				}
				msg_out.removeHeader("Content-Length");
		}
	}
}
