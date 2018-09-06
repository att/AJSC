/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ajsc.ComputeService;
import ajsc.utils.AjscUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class AjscCamelServlet extends
		org.apache.camel.component.servlet.CamelHttpTransportServlet {/*
	static final Logger logger = LoggerFactory
			.getLogger(AjscCamelServlet.class);

	public static final String PATH_SEPARATER = "/";

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (request.getMethod().equalsIgnoreCase("head")
				|| request.getHeader("DME2HealthCheck") != null)

		{
			try {

				logger.debug("path Info" + request.getPathInfo());

				String serviceUri = null;

				if (request.getServletPath() == null
						|| request.getServletPath().toString().isEmpty()
						|| request.getServletPath().toString().equals("/*")) {
					serviceUri = request.getPathInfo();
				} else {
					serviceUri = request.getPathInfo().replace(
							request.getServletPath(), PATH_SEPARATER);
				}

				logger.debug("serviceUri" + serviceUri);

				logger.debug("endpointUriMap"
						+ ComputeService.endpointUriMap.toString());

				if (ComputeService.endpointUriMap.containsKey(serviceUri)) {
					response.setStatus(200); // Success
				} else {
					response.setStatus(503); // Service unavailable
				}
			} catch (Exception e) {
				logger.error("Exception occurred in Servlet DME2HealthCheck"
						+ ":" + AjscUtil.getStackTrace(e));

			}

		} else {

			super.service(request, response);
		}

	}
*/}
