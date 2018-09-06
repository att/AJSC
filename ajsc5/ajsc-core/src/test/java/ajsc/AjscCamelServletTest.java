/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.att.aft.dme2.api.DME2Manager;

import ajsc.common.CommonNames;
import ajsc.servlet.AjscCamelServlet;
import ajsc.utils.DME2Helper;
import static org.junit.Assert.*;

public class AjscCamelServletTest extends BaseTestCase {/*

	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		super.setUp();
		
	}
	@Test
	public void RegisterTest() throws ServletException, IOException{
		AjscCamelServlet aj=new AjscCamelServlet();
		ServletDummy request=new ServletDummy();
		request.setHeader("DME2HealthCheck", "healthy");
		servletResDummy response=new servletResDummy();
		request.setPathInfo("/example");
		request.setMethod("head");
		ComputeService.endpointUriMap.put("/example", "example");
		aj.service(request, response);
		//System.out.println(response.getStatus());
		assertEquals(200, response.getStatus());
		
		ServletDummy request2=new ServletDummy();
		request2.setPathInfo("/services");
		request2.setMethod("head");
		request2.setHeader("DME2HealthCheck", "healthy");
		request2.setServletPath("/services");
		ComputeService.endpointUriMap.put("/a", "/");
		aj.service(request2, response);
		assertEquals(503, response.getStatus());
		

		ServletDummy request3=new ServletDummy();
		request3.setPathInfo("/services");
		request3.setMethod("nope");
		aj.service(request3, response);
	}
*/}
