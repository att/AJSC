/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.utils;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.aft.dme2.api.DME2EndpointRegistry;
import com.att.aft.dme2.api.DME2Manager;

public class DME2Helper {

	private static final Logger logger = LoggerFactory
			.getLogger(DME2Helper.class);
	private static String hostaddr;
	private static int port = 0;
	private static String grmNamespace;
	private static String envContext;
	private static String routeOffer;
	private static String ajscServiceNamespace;
	private static String ajscServiceVersion;
	private static String serviceVersion;
	private static String protocol;
	private static String restletUriPattern;
	private static String appContextPath;
	private static String reversedGRMNamespace;
	private static String servletUriPattern;
	public static final String SERVICE_TYPE_RESTLET = "/rest";
	public static final String SERVICE_TYPE_SERVLET = "/servlet";
	public static Set<String> restletEndpointSet = new HashSet<String>();
	public static Set<String> serviceEndpointSet = new HashSet<String>();

	public static void init() {

		try {
			hostaddr = System.getProperty("server.host");

			if (hostaddr == null) {
				InetAddress addr = InetAddress.getLocalHost();
				hostaddr = addr.getCanonicalHostName();
			}

			String serverPort;

			// If enableSSL property is true & SOACLOUD_PROTOCOL property is
			// HTTPS, then use HTTPS port to register services into GRM.
			// Otherwise use HTTP port.
			String enableSSL = System.getProperty("enableSSL");

			if (enableSSL != null && enableSSL.equalsIgnoreCase("true")) {
				serverPort = System.getProperty("AJSC_HTTPS_PORT");
			} else {
				serverPort = System.getProperty("server.port");
			}
			port = (serverPort != null) ? Integer.valueOf(serverPort) : 8080;
			grmNamespace = System.getProperty("SOACLOUD_NAMESPACE");
			ajscServiceNamespace = System.getProperty("AJSC_SERVICE_NAMESPACE");
			ajscServiceVersion = System.getProperty("AJSC_SERVICE_VERSION");
			envContext = System.getProperty("SOACLOUD_ENV_CONTEXT");
			routeOffer = System.getProperty("SOACLOUD_ROUTE_OFFER");
			serviceVersion = System.getProperty("SOACLOUD_SERVICE_VERSION");

			if (enableSSL != null && enableSSL.equalsIgnoreCase("true")) {
				protocol = "https";
			} else {
				protocol = "http";
			}
			protocol = System.getProperty("SOACLOUD_PROTOCOL") != null ? System
					.getProperty("SOACLOUD_PROTOCOL") : "http";
			reversedGRMNamespace = reverseGRMNamespace(grmNamespace);
			servletUriPattern = System.getProperty("APP_SERVLET_URL_PATTERN");
			restletUriPattern = System.getProperty("APP_RESTLET_URL_PATTERN");
			appContextPath = System.getProperty("APP_CONTEXT_PATH");
		} catch (Exception e) {
			logger.error("Exception occurred while initializing  ", e
					.getStackTrace().toString());
		}
	}

	/**
	 * This method registers AJSC Apache Camel route to GRM.
	 * 
	 * @param endpointUrl
	 * @param dme2Manager
	 * @throws Exception
	 */
	public void registerServiceToGRM(String endpointUrl,
			DME2Manager dme2Manager, boolean isExplicitUrl) throws Exception {

		logger.debug("Processing Endpoint: " + endpointUrl);

		DME2EndpointRegistry svcRegistry = dme2Manager.getEndpointRegistry();
		String endpointUrlWithoutQueryParam = null;
		String serviceType = null;
		if (!isExplicitUrl) {
			serviceType = getServiceType(endpointUrl);
			logger.debug("Derived Service Type: " + serviceType);
			if (serviceType != null && !serviceType.isEmpty()) {

				// Get endpoint Url without Query Parameter

				if (SERVICE_TYPE_RESTLET.equalsIgnoreCase(serviceType)) {
					endpointUrlWithoutQueryParam = getRestEndpointUrlWithoutQueryParam(endpointUrl);
					restletEndpointSet.add(endpointUrlWithoutQueryParam);
				} else if (SERVICE_TYPE_SERVLET.equalsIgnoreCase(serviceType)) {
					endpointUrlWithoutQueryParam = getServletEndpointUrlWithoutQueryParam(endpointUrl);
					serviceEndpointSet.add(endpointUrlWithoutQueryParam);
				}
				logger.debug("Derived endpointUrlWithoutQueryParam: "
						+ endpointUrlWithoutQueryParam);

			}

		} else {
			endpointUrlWithoutQueryParam = endpointUrl;
		}

		if (endpointUrlWithoutQueryParam != null) {
			// Get Service Name
			String serviceName = getServiceName(endpointUrlWithoutQueryParam,
					serviceType);

			String path = null;

			if (endpointUrlWithoutQueryParam.equals("/"))
				path = endpointUrlWithoutQueryParam;

			// Publish
			try {
				svcRegistry
						.publish(serviceName, path, hostaddr, port, protocol);
				logger.info("Service Registered to GRM (Service Name,Path,HostAddress,Port,Protocol): "
						+ serviceName
						+ ","
						+ path
						+ ","
						+ hostaddr
						+ ","
						+ port + "," + protocol);
			} catch (Exception e) {
				logger.error("Exception occured while registering service to GRM (Service Name,Path,HostAddress,Port,Protocol): "
						+ serviceName
						+ ","
						+ path
						+ ","
						+ hostaddr
						+ ","
						+ port + "," + protocol);
				SystemErrorHandlerUtil.callSystemExit(e);
			}
		}

	}
	
	/**
	 * Register SOAP based endpoint into GRM as per CSI norm
	 * 
	 * @param endpointUrl
	 * @param serviceName
	 * @param DME2Manager
	 * @throws Exception
	 */
	public void registerSOAPServiceToGRM(String endpointUrl,
			String configuredServiceName, DME2Manager dme2Manager)
			throws Exception {

		logger.debug("Processing SOAP based endpoint: " + endpointUrl);

		String serviceNamespace = grmNamespace + ".";

		String serviceName = "service=" + serviceNamespace
				+ configuredServiceName + "/version=" + serviceVersion
				+ "/envContext=" + envContext + "/routeOffer=" + routeOffer;

		DME2EndpointRegistry svcRegistry = dme2Manager.getEndpointRegistry();

		// Publish
		try {
			svcRegistry.publish(serviceName, endpointUrl, hostaddr, port,
					protocol);
			logger.info("SOAP service registered to GRM (Service Name,Path,HostAddress,Port,Protocol): "
					+ serviceName
					+ ","
					+ endpointUrl
					+ ","
					+ hostaddr
					+ ","
					+ port + "," + protocol);
		} catch (Exception e) {
			logger.error("Exception occured while registering the SOAP service to GRM (Service Name,Path,HostAddress,Port,Protocol): "
					+ serviceName
					+ ","
					+ endpointUrl
					+ ","
					+ hostaddr
					+ ","
					+ port + "," + protocol);
			SystemErrorHandlerUtil.callSystemExit(e);
		}

	}

	/**
	 * Build the Service endpoint from camel route endpointUrl.
	 * 
	 * @param endpointUrl
	 * @return
	 */
	private String getServletEndpointUrlWithoutQueryParam(String endpointUrl) {
		String endpointUrlWithoutQueryParam = endpointUrl.split("\\?")[0];

		if (endpointUrlWithoutQueryParam.contains("att-dme2-servlet:///")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("att-dme2-servlet:///", "/");
		} else if (endpointUrlWithoutQueryParam.contains("att-dme2-servlet://")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("att-dme2-servlet://", "/");
		} else if (endpointUrlWithoutQueryParam.contains("att-dme2-servlet:/")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("att-dme2-servlet:/", "/");
		}

		logger.debug("endpointUrlWithoutQueryParam"
				+ endpointUrlWithoutQueryParam);
		return endpointUrlWithoutQueryParam;
	}

	private String getRestEndpointUrlWithoutQueryParam(String endpointUrl) {
		String endpointUrlWithoutQueryParam = endpointUrl.split("\\?")[0];
		if (endpointUrlWithoutQueryParam.contains("restlet:///")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("restlet:///", "/");
		} else if (endpointUrlWithoutQueryParam.contains("restlet://")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("restlet://", "/");
		} else if (endpointUrlWithoutQueryParam.contains("restlet:/")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("restlet:/", "/");
		}

		logger.debug("endpointUrlWithoutQueryParam"
				+ endpointUrlWithoutQueryParam);
		return endpointUrlWithoutQueryParam;
	}

	private String getServiceType(String endpointUrl) {
		String serviceType = "";
		if (endpointUrl.contains("att-dme2-servlet:///")) {
			serviceType = SERVICE_TYPE_SERVLET;
		} else if (endpointUrl.contains("att-dme2-servlet://")) {
			serviceType = SERVICE_TYPE_SERVLET;
		} else if (endpointUrl.contains("att-dme2-servlet:/")) {
			serviceType = SERVICE_TYPE_SERVLET;
		} else if (endpointUrl.contains("restlet:///")) {
			serviceType = SERVICE_TYPE_RESTLET;
		} else if (endpointUrl.contains("restlet://")) {
			serviceType = SERVICE_TYPE_RESTLET;
		} else if (endpointUrl.contains("restlet:/")) {
			serviceType = SERVICE_TYPE_RESTLET;
		}

		return serviceType;
	}

	/**
	 * This method builds dme2 service name in RESTful URI format from
	 * endpointUrlWithoutQueryParam. Since DME2 service name does not contains
	 * "-" and "." characters. It replaces those characters with "-" character.
	 * 
	 * @param endpointUrlWithoutQueryParam
	 * @return
	 * @throws Exception
	 */
	private String getServiceName(String endpointUrlWithoutQueryParam,
			String serviceType) throws Exception {

		String serviceId = ajscServiceNamespace + "-" + ajscServiceVersion;

		// Since DME2 service name does not contains "-" and "." characters. It
		// replaces those characters with "-" character
		serviceId = serviceId.replaceAll("\\.", "-");
		serviceId = serviceId.replaceAll("_", "-");

		String urlPattern = null;

		if (SERVICE_TYPE_RESTLET.equalsIgnoreCase(serviceType)) {
			urlPattern = restletUriPattern;
		} else if (SERVICE_TYPE_SERVLET.equalsIgnoreCase(serviceType)) {
			urlPattern = servletUriPattern;
		}

		// Build context path
		String contextPath = null;
		if (appContextPath != null && !appContextPath.isEmpty()
				&& !appContextPath.equals("/")) {

			if (urlPattern == null || urlPattern.isEmpty()) {

				contextPath = appContextPath + endpointUrlWithoutQueryParam;

			} else {

				contextPath = appContextPath + urlPattern
						+ endpointUrlWithoutQueryParam;
			}

		} else {
			if (urlPattern == null || urlPattern.isEmpty()) {

				contextPath = endpointUrlWithoutQueryParam;

			} else {

				contextPath = urlPattern + endpointUrlWithoutQueryParam;
			}

		}
		// Build Service Name in RESTful URI format
		String serviceName = null;

		serviceName = "http://" + serviceId.trim() + "." + reversedGRMNamespace
				+ contextPath + "?version=" + serviceVersion + "&envContext="
				+ envContext + "&routeOffer=" + routeOffer;

		logger.debug(" serviceName=" + serviceName);
		return serviceName;
	}

	private static String reverseGRMNamespace(String namespace) {
		String reversedGRMNamespace = null;
		String[] namespaceArray = namespace.split("\\.");

		for (int i = namespaceArray.length - 1; i >= 0; i--) {
			if (i == namespaceArray.length - 1) {
				reversedGRMNamespace = namespaceArray[i];

			} else {
				reversedGRMNamespace = reversedGRMNamespace.concat(".").concat(
						namespaceArray[i]);
			}

		}
		return reversedGRMNamespace;
	}
}
