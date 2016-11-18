/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;

import ajsc.beans.interceptors.AjscInterceptor;
import ajsc.common.CommonNames;

public class InterceptorFilter implements Filter {

	private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
	private static final String PRE_PROCESSOR_CONFIG_FILE = "PreProcessorInterceptors.properties";
	private static final String POST_PROCESSOR_CONFIG_FILE = "PostProcessorInterceptors.properties";
	private static final int RESPONSE_NOT_SET=0;

	private static final String AAF_USER_ROLES_CONFIG_FILE = "AAFUserRoles.properties";
	static final Logger logger = LoggerFactory.getLogger(InterceptorFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest hReq = (HttpServletRequest) req;
		HttpServletResponse res = (HttpServletResponse) resp;
		Map<Object, Object> paramMap = new HashMap<Object, Object>();

		Map<String, String> preProcessorMap = AJSCPropertiesMap.getProperties(PRE_PROCESSOR_CONFIG_FILE);
		Map<String, String> postProcessorMap = AJSCPropertiesMap.getProperties(POST_PROCESSOR_CONFIG_FILE);
		Map<String, String> aafUserRolesMap = AJSCPropertiesMap.getProperties(AAF_USER_ROLES_CONFIG_FILE);

		// add the start time of this request to the paramMap. This will be
		// needed during post-processing of the request

		paramMap.put(CommonNames.REQUEST_START_TIME, System.currentTimeMillis());
		/*
		 * long ltime = System.nanoTime()/1000000; String stime =
		 * Long.toString(ltime);
		 * 
		 * paramMap.put(CommonNames.REQUEST_START_TIME, ltime);
		 */

		req.setAttribute(CommonNames.ATTR_START_TIME, Long.valueOf(System.currentTimeMillis()).toString());
		// req.setAttribute(CommonNames.ATTR_START_TIME,stime);

		// Pre-processing logic starts

		String pathInfo = hReq.getPathInfo();
		ArrayList<String> aafRoles = null;
		if (aafUserRolesMap != null) {
			aafRoles = urlMappingResolver(aafUserRolesMap, pathInfo);
		}
		ArrayList<String> preInterceptorClasses = urlMappingResolver(preProcessorMap, pathInfo);

		logger.debug("*** Pre-Process Interceptors being applied ***");

		paramMap.put("PRE_PROCESS", new Boolean(true));
		Boolean aafRoleEnabled = false;
		if (aafRoles != null && aafRoles.size() != 0) {
			aafRoleEnabled = verifyRoles(aafRoles, hReq, res, paramMap);
		}

		if (aafRoles == null || (aafRoles.size() == 0 && !aafRoleEnabled) || (aafRoles.size() != 0 && aafRoleEnabled)) {

			invokeInterceptors(preInterceptorClasses, hReq, res, paramMap);

			paramMap.remove("PRE_PROCESS");
			
			// Pre-processing logic ends
		
			
			byte[] bytes = null;
			
			if(res.getStatus() == HttpServletResponse.SC_OK){
				
				if (paramMap.get(HttpServletResponse.SC_FORBIDDEN) == null
						&& paramMap.get(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) == null) {

					CharResponseWrapper wrappedResponse = new CharResponseWrapper((HttpServletResponse) res);
					chain.doFilter(req, wrappedResponse);

					// Post-processing logic starts
					bytes = wrappedResponse.getByteArray();
					req.setAttribute("resMsgSize", bytes.length);

					
				}
			}
				
			ArrayList<String> postInterceptorClasses = urlMappingResolver(postProcessorMap, pathInfo);

			logger.debug("*** Post-Process Interceptors being applied ***");

			paramMap.put("POST_PROCESS", new Boolean(true));

			invokeInterceptors(postInterceptorClasses, hReq, res, paramMap);

			paramMap.remove("POST_PROCESS");

			if (res.getStatus() == HttpServletResponse.SC_OK )
			{ 
				res.getOutputStream().write(bytes); 
			}
				
				paramMap.remove(HttpServletResponse.SC_FORBIDDEN);
				paramMap.remove(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			

			
		} else {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			logger.info("User Does not have correct Authorization....");

		}
	}

	private ArrayList<String> urlMappingResolver(Map<String, String> p, String pathinfo) {

		ArrayList<String> interceptorClasses = new ArrayList<String>();

		PathMatcher pathMatcher = new AntPathMatcher();

		for (Map.Entry<String, String> entry : p.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			String[] valueArray = value.split(",");

			if (pathMatcher.match(key, pathinfo)) {
				for (String val : valueArray) {
					if (!interceptorClasses.contains(val)) {
						interceptorClasses.add(val);
					}
				}
			}
		}

		return interceptorClasses;
	}

	private Boolean verifyRoles(ArrayList<String> interceptorClasses, HttpServletRequest hReq, HttpServletResponse res,
			Map<? super Object, ? super Object> paramMap) {
		Boolean value = false;
		if (!interceptorClasses.isEmpty()) {

			for (String interceptorClass : interceptorClasses) {
				try {
					if (hReq.isUserInRole(interceptorClass)) {
						value = true;
						break;
					} else
						value = false;
				} catch (Exception e) {

				}
			}
		}
		return value;
	}

	private void invokeInterceptors(ArrayList<String> interceptorClasses, HttpServletRequest hReq,
			HttpServletResponse res, Map<? super Object, ? super Object> paramMap) {

		// System.out.println("<<<<<<<<<<<<Interceptor classes list ->"+interceptorClasses);

		if (!interceptorClasses.isEmpty()) {

			for (String interceptorClass : interceptorClasses) {
				try {
					if(interceptorClass != null){
						AjscInterceptor interceptor = (AjscInterceptor) Class.forName(interceptorClass.trim())
								.getDeclaredMethod(GET_INSTANCE_METHOD_NAME).invoke(null, null);
	
						if (!interceptor.allowOrReject(hReq, res, paramMap)) {
	
							Boolean hasPre_Process = (Boolean) paramMap.get("PRE_PROCESS");
							Boolean hasPost_Process = (Boolean) paramMap.get("POST_PROCESS");
	
							if (hasPre_Process != null && hasPre_Process) {
								if(res.getStatus() == RESPONSE_NOT_SET){
									// return a 403 (Forbidden error) back to the client
									logger.error("Invalid request. Please verify your request & try once again.");
									paramMap.put(HttpServletResponse.SC_FORBIDDEN,
											"Invalid request. Please verify your request & try once again.");
									res.setStatus(HttpServletResponse.SC_FORBIDDEN);
								}
							} else if (hasPost_Process != null && hasPost_Process) {
								if(res.getStatus() == RESPONSE_NOT_SET){
									logger.error("AJSC processed the request successfully. But there may be an issue in the downstream system(s) while processing the request.");
									// res.setStatus(HttpServletResponse.SC_CONTINUE);//SC_CONTINUE
									// (slowness & status = 200 not desirable),
									// SC_NO_CONTENT (no body in response), SC_ACCEPTED
									res.setStatus(HttpServletResponse.SC_ACCEPTED);
								}
							}
							break;
						}
					}
				} catch (Exception e) {

					logger.error("Exception occurred in invokeInterceptors(...) of InterceptorFilter :", e);
					// return a 500 (Internal Server error) back to the client
					paramMap.put(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Internal server error occured. Please try again.");
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					break;
				}
			}
		}

	}

	private static class ByteArrayServletStream extends ServletOutputStream {
		ByteArrayOutputStream baos;

		ByteArrayServletStream(ByteArrayOutputStream baos) {
			this.baos = baos;
		}

		public void write(int param) throws IOException {
			baos.write(param);
		}

	}

	private static class ByteArrayPrintWriter {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		private PrintWriter pw = new PrintWriter(baos);

		private ServletOutputStream sos = new ByteArrayServletStream(baos);

		public PrintWriter getWriter() {
			return pw;
		}

		public ServletOutputStream getStream() {
			return sos;
		}

		byte[] toByteArray() {
			return baos.toByteArray();
		}
	}

	public class CharResponseWrapper extends HttpServletResponseWrapper {
		private ByteArrayPrintWriter output;
		private boolean usingWriter;
		final Map<String, List<String>> headers = new HashMap<String, List<String>>();

		public CharResponseWrapper(HttpServletResponse response) {
			super(response);
			usingWriter = false;
			output = new ByteArrayPrintWriter();
		}

		public byte[] getByteArray() {
			return output.toByteArray();
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			// will error out, if in use
			if (usingWriter) {
				super.getOutputStream();
			}
			usingWriter = true;
			return output.getStream();
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			// will error out, if in use
			if (usingWriter) {
				super.getWriter();
			}
			usingWriter = true;
			return output.getWriter();
		}

		public String toString() {
			return output.toString();
		}
	}
	

}
