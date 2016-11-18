/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ajsc.common.CommonNames;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

// The kitchen sink class.
public class UtilLib {
	final static Logger logger = LoggerFactory.getLogger(UtilLib.class);
	private static final String OPEN_PARENTHESIS = "(";
	public static final String CLOSE_PARENTHESIS = ")";
	public static final String SEMICOLON = ";";

	public static boolean isNullOrEmpty(String s) {
		return (s == null || s.trim().isEmpty());
	}

	public static String ifNullThenEmpty(String s) {
		return (isNullOrEmpty(s) ? "" : s);
	}

	public static String jsonpWrapper(String jsonpFunction, String json) {
		String jsonWrapperResponse = json;
		if (jsonpFunction != null && !jsonpFunction.trim().isEmpty()
				&& json != null && !json.trim().isEmpty()) {
			jsonWrapperResponse = jsonpFunction.trim() + OPEN_PARENTHESIS
					+ json + CLOSE_PARENTHESIS + SEMICOLON;
		}
		return jsonWrapperResponse;
	}

	public static String uCaseFirstLetter(String s) {
		String ns = null;
		if (s != null && !s.isEmpty()) {
			String firstLetter = s.substring(0, 1).toUpperCase();
			String remainingString = s.substring(1);
			ns = firstLetter + remainingString;
		}
		return ns;
	}

	public static XMLGregorianCalendar epochToXmlGC(long epoch) {
		try {
			DatatypeFactory dtf = DatatypeFactory.newInstance();
			GregorianCalendar gcal = new GregorianCalendar();
			gcal.setTimeInMillis(epoch);
			gcal.setTimeZone(TimeZone.getTimeZone("Z"));
			XMLGregorianCalendar xgc = dtf.newXMLGregorianCalendar(gcal);
			return xgc;
		} catch (Exception e) {
			// Do nothing!!! - return a null;
		}
		return null;
	}

	public static String getClientApp() {
		return "ajsc-csi-restful~" + SystemParams.instance().getAppName();
	}

	public static String dme2UriToLocaldme2Uri(String dme2Uri, String version,
			String envContext, String routeOffer) {
		boolean serviceNameFound = false;
		String uriParts[] = ifNullThenEmpty(dme2Uri).split("/");
		String serviceName = "";
		for (String uriPart : uriParts) {
			if (ifNullThenEmpty(uriPart).startsWith("service=")) {
				serviceNameFound = true;
				serviceName = uriPart.substring("service=".length());
				break;
			}
		}

		String newDme2Uri = dme2Uri;
		if (serviceNameFound) {
			newDme2Uri = "dme2://DME2LOCAL" + "/service=" + serviceName
					+ "/version=" + version + "/envContext=" + envContext
					+ "/routeOffer=" + routeOffer;
		}
		return newDme2Uri;
	}

	public static String getErrorResponseBodyType(String acceptHeader) {
		try {
			boolean acceptJson = false;
			boolean acceptXml = false;

			String acceptValues[] = ifNullThenEmpty(acceptHeader).split(",");
			for (String acceptValue : acceptValues) {
				String splitValue[] = ifNullThenEmpty(acceptValue).split(";");
				if (splitValue != null && splitValue.length > 0
						&& ifNullThenEmpty(splitValue[0]).length() > 0) {
					if (splitValue[0].endsWith("/json"))
						acceptJson = true;
					else if (splitValue[0].endsWith("/xml"))
						acceptXml = true;
					else if (splitValue[0].equals("*/*")) {
						acceptJson = true;
						acceptXml = true;
					}
				}
			}

			if (acceptJson)
				return CommonNames.BODY_TYPE_JSON;
			else if (acceptXml)
				return CommonNames.BODY_TYPE_XML;
		} catch (Exception e) {
			// Do nothing - just use Json
		}
		return CommonNames.BODY_TYPE_JSON;
	}

	public static String getStartTimestamp(String epoch) {
		long stime = Long.parseLong((String) epoch);
		XmlCalendar cal = new XmlCalendar(new Date(stime));
		XMLGregorianCalendar initTime = null;
		try {
			initTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
					cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND),
					Math.round(cal.get(Calendar.ZONE_OFFSET) / 1000 / 60));
		} catch (Exception ex) {
			initTime = null;
		}
		if (initTime == null)
			return null;
		else
			return initTime.toString();
	}

	public static String getServiceName(String servletPath, String pathInfo) {

		String servicename = "N/A";
		try {
			String serviceType = servletPath.replaceAll("\\/", "");
			String SERVLET_URL_PATTERN = System.getProperty(
					"APP_SERVLET_URL_PATTERN").replaceAll("\\/", "");
			String RESTLET_URL_PATTERN = System.getProperty(
					"APP_RESTLET_URL_PATTERN").replaceAll("\\/", "");
			String componentType = "";
			String namespace = System
					.getProperty(CommonNames.SOACLOUD_NAMESPACE);

			String pathinfoArr[] = pathInfo.split("\\/");
			int arrLength = pathinfoArr.length;
			String input = "";
			if (serviceType.equalsIgnoreCase("services")
					|| serviceType.equalsIgnoreCase(SERVLET_URL_PATTERN)) {
				componentType = CommonNames.COMPONENT_TYPE_SERVLET;
				input = getInput(pathinfoArr, arrLength, componentType,
						pathInfo);

				servicename = concatenateName(namespace, input);
			} else if (serviceType.equalsIgnoreCase("rest")
					|| serviceType.equalsIgnoreCase(RESTLET_URL_PATTERN)) {
				componentType = CommonNames.COMPONENT_TYPE_RESTLET;
				input = getInput(pathinfoArr, arrLength, componentType,
						pathInfo);
				servicename = concatenateName(namespace, input);
			}

		} catch (Exception e) {

			logger.error(e.getMessage());

		}

		return servicename;
	}

	public static Boolean compareValues(String fromURL, String fromGRM) {

		if (fromURL.equals(fromGRM) || fromGRM.startsWith("{")
				|| fromGRM.startsWith("[")) {
			return true;
		} else
			return false;
	}

	public static String getInput(String pathinfoArr[], int arrLength,
			String componentType, String pathInfo) {
		Set<String> endpointSet = null;

		if (componentType.equalsIgnoreCase("rest")) {
			endpointSet = DME2Helper.restletEndpointSet;
		} else {
			endpointSet = DME2Helper.serviceEndpointSet;
		}
		HashSet<String> setBasedArrLenth = new HashSet<String>();
		HashMap setBasedCharMap = new HashMap();
		HashSet<String> setBasedValues = new HashSet<String>();
		AntPathMatcher pathMatcher = new AntPathMatcher();

		String inputBasedonLength[];
		int globalvalue = 0;
		for (String s : endpointSet) {
			int dif = StringUtils.getLevenshteinDistance(pathInfo, s);

			if (globalvalue == 0 || globalvalue > dif) {
				globalvalue = dif;
				setBasedCharMap.put(globalvalue, s);
			}

			inputBasedonLength = s.split("\\/");
			int i = inputBasedonLength.length;
			if (arrLength == i) {
				setBasedArrLenth.add(s);
			}
		}

		String inputBasedOnValues[];
		for (String s1 : setBasedArrLenth) {
			inputBasedOnValues = s1.split("\\/");

			int j = 1;
			while (compareValues(pathinfoArr[j], inputBasedOnValues[j])) {
				j++;
				if (j >= arrLength) {
					break;
				}
			}
			if (j == arrLength) {
				setBasedValues.add(s1);
			}
		}
		String input = "";

		if (setBasedValues.size() == 1) {
			for (String s2 : setBasedValues) {
				input = s2;
			}
		} else {
			for (String s2 : setBasedValues) {
				if (pathMatcher.match(pathInfo, s2)) {
					input = s2;
				}
			}
		}
		if (input.isEmpty()) {
			input = (String) setBasedCharMap.get(globalvalue);

		}
		return "/" + componentType + input;
	}

	public static String concatenateName(String namespace, String input) {

		String serviceName = "";
		if (System.getProperty("AJSC_SERVICE_NAMESPACE") != null
				&& System.getProperty("AJSC_SERVICE_VERSION") != null) {
			serviceName = namespace + "."
					+ System.getProperty("AJSC_SERVICE_NAMESPACE") + "-"
					+ System.getProperty("AJSC_SERVICE_VERSION") + input;
		} else {
			serviceName = namespace + input;
		}
		return serviceName;
	}
	
	public static String getServiceName(HttpServletRequest request ) {
		String serviceName = "N/A";
		if (request.getHeader("X-CSI-MethodName") != null) {
			serviceName = request.getHeader("X-CSI-MethodName");
		}
		return serviceName;
	}

}
