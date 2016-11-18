/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.restlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.engine.adapter.HttpServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajsc.ComputeService;

public class RestletSpringServlet extends
		org.restlet.ext.spring.SpringServerServlet {

	static final Logger logger = LoggerFactory
			.getLogger(RestletSpringServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the attribute key containing a reference to the current
	 * component.
	 */
	private static final String COMPONENT_KEY = "org.restlet.component";

	private static final String NAME_SERVER_ATTRIBUTE = "org.restlet.attribute.server";

	/** The default value for the NAME_SERVER_ATTRIBUTE parameter. */
	private static final String NAME_SERVER_ATTRIBUTE_DEFAULT = "org.restlet.ext.servlet.ServerServlet.server";

	// private static final String NAME_SERVER_ATTRIBUTE_DEFAULT =
	// "ajsc.restlet.RestletServerServlet.server";

	/**
	 * The Servlet context initialization parameter's name containing a boolean
	 * value. "true" indicates that all applications will be attached to the
	 * Component's virtual hosts with the Servlet Context path value.
	 */
	private static final String AUTO_WIRE_KEY = "org.restlet.autoWire";

	/** The default value for the AUTO_WIRE_KEY parameter. */
	private static final String AUTO_WIRE_KEY_DEFAULT = "true";

	/**
	 * Name of the attribute containing the computed offset path used to attach
	 * applications when (and only when) the auto-wiring feature is set, is
	 * added to the component's context.
	 */
	private static final String NAME_OFFSET_PATH_ATTRIBUTE = "org.restlet.ext.servlet.offsetPath";

	private volatile transient boolean isFirstRequest = true;

	public static final String PATH_SEPARATER = "/";

	public HttpServerHelper getServer(HttpServletRequest request) {

		// Lazy initialization with double-check.
		HttpServerHelper result;
		// // Find the attribute name to use to store the server
		// // reference
		final String serverAttributeName = getInitParameter(
				NAME_SERVER_ATTRIBUTE, NAME_SERVER_ATTRIBUTE_DEFAULT + "."
						+ getServletName());

		Component component = getComponent();

		// System.out.println("isFirstRequest:" +isFirstRequest);
		// if ((!isFirstRequest) && isNewRouteAdded(request, component)) {
		// result = createServer(request, component);
		// getServletContext().setAttribute(serverAttributeName, result);
		// } else {

		// System.out.println("Super getServer");
		result = super.getServer(request);

		// }

		isFirstRequest = false;

		return result;
	}


	/**
	 * Indicates if the Component hosted by this Servlet is the default one or
	 * one provided by the user.
	 * 
	 * @return True if the Component is the default one, false otherwise.
	 */
	private boolean isDefaultComponent() {
		// The Component is provided via an XML configuration file.
		Client client = createWarClient(new Context(), getServletConfig());
		Response response = client.handle(new Request(Method.GET,
				"war:///WEB-INF/restlet.xml"));
		if (response.getStatus().isSuccess() && response.isEntityAvailable()) {
			return false;
		}

		// The Component is provided via a context parameter in the "web.xml"
		// file.
		String componentAttributeName = getInitParameter(COMPONENT_KEY, null);
		if (componentAttributeName != null) {
			return false;
		}

		return true;
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.debug("servlet:service" + request.getMethod());
		//System.out.println("servlet:service" + request.getMethod());
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

				logger.debug("path Info" + request.getPathInfo());
				logger.debug("endpointUriMap"
						+ ComputeService.endpointUriMap.toString());
				
//				System.out.println("path Info" + request.getPathInfo());
//				System.out.println("serviceUri" + serviceUri);

//				System.out.println("endpointUriMap"
//						+ ComputeService.endpointUriMap.toString());
				if (ComputeService.endpointUriMap.containsKey("restlet:"+serviceUri)) {
					logger.debug("DME2HealthCheck -setting response status 200");
					response.setStatus(200); // Success
				} else {
					logger.debug("DME2HealthCheck -setting response status 503");
					response.setStatus(503); // Service unavailable
				}
			} catch (Exception e) {
				logger.error("Exception occurred in Servlet DME2HealthCheck"
						+ ":" + getStackTrace(e));

			}

	

		} else {

			super.service(request, response);
		}
	}
	public static String getStackTrace(Throwable aThrowable) {
		final StringWriter result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
 }
}
