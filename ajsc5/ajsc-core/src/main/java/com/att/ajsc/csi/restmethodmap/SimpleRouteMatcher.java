/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.restmethodmap;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleRouteMatcher implements RouteMatcher {

	private static Logger logger = LoggerFactory
			.getLogger(SimpleRouteMatcher.class);

	private List<RouteEntry> routes;

	public List<RouteEntry> getRoutes() {
		return routes;
	}

	private static class RouteEntry {

		private String service;
		private HttpMethod httpMethod;
		private String path;
		private String logicalMethod;
		private String dme2Url;
		private String type;
		private String serviceName;
		private String passThroughRespCode;

		public final static int NO_MATCH = -1;
		private final static int PERFECT_MATCH = 2;
		private final static int PARAM_MATCH = 1;

		/*
		 * For a wildcard method match - clone with the incoming method to
		 * match.
		 */
		private RouteEntry cloneWithHttpMethod(HttpMethod method) {
			RouteEntry re = new RouteEntry();
			re.service = this.service;
			re.path = this.path;
			re.logicalMethod = this.logicalMethod;
			re.httpMethod = method;
			re.dme2Url = this.dme2Url;
			re.passThroughRespCode = this.passThroughRespCode;

			return re;
		}

		private int matches(HttpMethod httpMethod, String path) {
			int match = 0;
			/*
			 * No special scoring for starstar. Don't define the same URI with a
			 * wildcard with a star and with an http method
			 */
			if (this.httpMethod == httpMethod) {
				match = matchPath(path);
			}
			return match;
		}

		private int matchPath(String path) { // NOSONAR
			if (!this.path.endsWith("*")
					&& ((path.endsWith("/") && !this.path.endsWith("/")) // NOSONAR
					|| (this.path.endsWith("/") && !path.endsWith("/")))) {
				// One and not both ends with slash
				return NO_MATCH;
			}
			if (this.path.equals(path)) {
				// Paths are the same
				return Integer.MAX_VALUE;
			}

			// check params
			List<String> thisPathList = SparkUtils
					.convertRouteToList(this.path);
			List<String> pathList = SparkUtils.convertRouteToList(path);
			int matchStrength = 0;

			int thisPathSize = thisPathList.size();
			int pathSize = pathList.size();

			if (thisPathSize == pathSize) {
				for (int i = 0; i < thisPathSize; i++) {
					String thisPathPart = thisPathList.get(i);
					String pathPart = pathList.get(i);

					if ((i == thisPathSize - 1)
							&& (thisPathPart.equals("*") && this.path
									.endsWith("*"))) {
						// wildcard match
						return matchStrength;
					}

					if ((!SparkUtils.isParam(thisPathPart))
							&& !thisPathPart.equals(pathPart)
							&& !thisPathPart.equals("*")) {
						return NO_MATCH;
					}

					if (thisPathPart.equals(pathPart))
						matchStrength += PERFECT_MATCH;

					if (SparkUtils.isParam(thisPathPart))
						matchStrength += PARAM_MATCH;
				}
				// All parts matched
				return matchStrength;
			} else {
				// Number of "path parts" not the same
				// check wild card:
				if (this.path.endsWith("*")) {
					if (pathSize == (thisPathSize - 1) && (path.endsWith("/"))) {
						// Hack for making wildcards work with trailing slash
						pathList.add("");
						pathList.add("");
						pathSize += 2;
					}

					if (thisPathSize < pathSize) {
						for (int i = 0; i < thisPathSize; i++) {
							String thisPathPart = thisPathList.get(i);
							String pathPart = pathList.get(i);
							if (thisPathPart.equals("*")
									&& (i == thisPathSize - 1)
									&& this.path.endsWith("*")) {
								// wildcard match
								return matchStrength;
							}
							if (!SparkUtils.isParam(thisPathPart)
									&& !thisPathPart.equals(pathPart)
									&& !thisPathPart.equals("*")) {
								return NO_MATCH;
							}
							if (thisPathPart.equals(pathPart))
								matchStrength += PERFECT_MATCH;

							if (SparkUtils.isParam(thisPathPart))
								matchStrength += PARAM_MATCH;
						}
						// All parts matched
						return matchStrength;
					}
					// End check wild card
				}
				return NO_MATCH;
			}
		}

		public String toString() {
			return httpMethod.name() + ", " + path + ", " + logicalMethod;
		}

		public String getDme2Url() {
			return dme2Url;
		}

		public String getType() {
			return type;
		}

		public String getServiceName() {
			return serviceName;
		}

		public String getPassThroughRespCode() {
			return passThroughRespCode;
		}

		public void setPassThroughRespCode(String passThroughRespCode) {
			this.passThroughRespCode = passThroughRespCode;
		}

	}

	public SimpleRouteMatcher() {
		routes = new ArrayList<RouteEntry>();
	}

	@Override
	public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod,
			String path) {
		int matchStrength = RouteEntry.NO_MATCH;
		RouteEntry targetRoute = null;
		for (RouteEntry entry : routes) {
			RouteEntry entryToMatch = entry;
			if (HttpMethod.starstar.equals(entry.httpMethod))
				entryToMatch = entry.cloneWithHttpMethod(httpMethod);
			int nmatch = entryToMatch.matches(httpMethod, path);
			if (nmatch > matchStrength && entry.httpMethod.name().equalsIgnoreCase(httpMethod.name())) {
				targetRoute = entry;
				matchStrength = nmatch;
			}
		}
		if (targetRoute != null) {
			return new RouteMatch(targetRoute.service, targetRoute.httpMethod,
					targetRoute.logicalMethod, targetRoute.path, path,targetRoute.passThroughRespCode,
					matchStrength);
		} else {
			return null;
		}
	}

	@Override
	public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod,
			String path) {
		int matchStrength = RouteEntry.NO_MATCH;
		List<RouteMatch> matchSet = new ArrayList<RouteMatch>();
		for (RouteEntry entry : routes) {
			if ((matchStrength = entry.matches(httpMethod, path)) > RouteEntry.NO_MATCH) {
				matchSet.add(new RouteMatch(entry.service, httpMethod,
						entry.logicalMethod, entry.path, path, entry.passThroughRespCode,matchStrength));
			}
		}
		return matchSet;
	}

	@Override
	public void parseValidateAddRoute(String service, String httpMethod,
			String url, String logicalMethod, String dme2url, String type,
			String serviceName,String passThroughRespCode) {
		try {
			HttpMethod method;
			try {
				if ("*".equals(httpMethod))
					method = HttpMethod.starstar;
				else
					method = HttpMethod.valueOf(httpMethod);
			} catch (IllegalArgumentException e) {
				logger.error("The @Route value: " + url
						+ " has an invalid HTTP method part: " + httpMethod
						+ ".", e);
				return;
			}
			addRoute(service, method, url, logicalMethod, dme2url, type,
					serviceName,passThroughRespCode);
		} catch (Exception e) {
			logger.error("The @Route value: " + url
					+ " is not in the correct format", e);
		}

	}

	private void addRoute(String service, HttpMethod method, String url,
			String logicalMethod, String dme2url, String type,
			String serviceName,String passThroughRespCode) {
		RouteEntry entry = new RouteEntry();
		entry.service = service;
		entry.httpMethod = method;
		entry.path = url;
		entry.logicalMethod = logicalMethod;
		entry.dme2Url = dme2url;
		entry.type = type;
		entry.serviceName = serviceName;
		entry.passThroughRespCode = passThroughRespCode;
		// Adds to end of list
		routes.add(entry);
	}

	@Override
	public void clearRoutes() {
		routes.clear();
	}

}
