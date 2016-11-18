/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Before;
import org.junit.Test;

import ajsc.filters.InterceptorFilter;

import com.att.ajsc.csi.restmethodmap.RefresheableSimpleRouteMatcher;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;

public class InterceptorFilterTest extends BaseTestCase {
	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		super.setUp();
		
	}
	
	
	@Test
	public void doFilterTest() throws Exception{
		File pre=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PreProcessorInterceptors.properties");
		File post=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PostProcessorInterceptors.properties");
		File aaf=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"AAFUserRoles.properties");
		
		AJSCPropertiesMap.refresh(pre);
		AJSCPropertiesMap.refresh(post);
		AJSCPropertiesMap.refresh(aaf);

		InterceptorFilter iF=new InterceptorFilter();
		ServletDummy sd=new ServletDummy();
		sd.setPathInfo("/services");
		sd.setMethod("GET");
		sd.setRequestURI("/rest/test1/v1/helloWorld");
		File file = new File(TEST_RSC_DIR+"/appprops/methodMapper.properties");
		RefresheableSimpleRouteMatcher.refresh(file);
		
		 servletResDummy srd=new servletResDummy();
		 srd.setServletOutputStream(new ServletOutputStream() {
				
				@Override
				public void write(int b) throws IOException {
					// TODO Auto-generated method stub
					
				}
			});
		iF.doFilter(sd, srd, new a());
		
		
		
	}
	
	
	@Test
	public void oneLiners() throws ServletException{
		InterceptorFilter iF=new InterceptorFilter();
		iF.destroy();
		iF.init(new FilterConfig() {
			
			@Override
			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getInitParameter(String name) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getFilterName() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
	}
	
	
	@Test
	public void verifyRolestest() throws Exception{
		File pre=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PreProcessorInterceptors.properties");
		File post=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PostProcessorInterceptors.properties");
		File aaf=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"AAFUserRoles.properties");

		
		AJSCPropertiesMap.refresh(pre);
		AJSCPropertiesMap.refresh(post);
		AJSCPropertiesMap.refresh(aaf);

		InterceptorFilter iF=new InterceptorFilter();
		ServletDummy sd=new ServletDummy();
		servletResDummy srd=new servletResDummy();
		sd.setUserInRole(true);
		sd.setPathInfo("services");
		sd.setMethod("GET");
		sd.setRequestURI("/rest/test1/v1/helloWorld");
		File file = new File(TEST_RSC_DIR+"/appprops/methodMapper.properties");
		RefresheableSimpleRouteMatcher.refresh(file);
		
        srd.setServletOutputStream(new ServletOutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		iF.doFilter(sd, srd, new a());
		
		sd.setUserInRole(false);

		iF.doFilter(sd, srd, new a());
		
	}
	
	@Test
	public void invokeInterceptorsTest() throws Exception{
		File pre=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PreProcessorInterceptors.properties");
		File post=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PostProcessorInterceptors.properties");
		File aaf=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"AAFUserRoles.properties");

		AJSCPropertiesMap.refresh(pre);
		AJSCPropertiesMap.refresh(post);
		AJSCPropertiesMap.refresh(aaf);

		InterceptorFilter iF=new InterceptorFilter();
		ServletDummy sd=new ServletDummy();
		servletResDummy srd=new servletResDummy();
		
		sd.setMethod("GET");
		sd.setRequestURI("/rest/test1/v1/helloWorld");
		File file = new File(TEST_RSC_DIR+"/appprops/methodMapper.properties");
		RefresheableSimpleRouteMatcher.refresh(file);
		
		srd.setServletOutputStream(new ServletOutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		sd.setUserInRole(true);
		sd.setPathInfo("services");
		iF.doFilter(sd, srd, new a());
		
	}
	
	@Test
	public void invokeInterceptorsTestForErrorResponse() throws Exception{
		File pre=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PreProcessorInterceptors.properties");
		File post=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PostProcessorInterceptors.properties");
		File aaf=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"AAFUserRoles.properties");

		
		
		AJSCPropertiesMap.refresh(pre);
		AJSCPropertiesMap.refresh(post);
		AJSCPropertiesMap.refresh(aaf);

		InterceptorFilter iF=new InterceptorFilter();
		ServletDummy sd=new ServletDummy();
		sd.setMethod("GET");
		sd.setRequestURI("/rest/test1/v1/helloWorld");
		File file = new File(TEST_RSC_DIR+"/appprops/methodMapper.properties");
		RefresheableSimpleRouteMatcher.refresh(file);
		
		servletResDummy srd=new servletResDummy();
		srd.setServletOutputStream(new ServletOutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		sd.setUserInRole(true);
		sd.setPathInfo("services");
		iF.doFilter(sd, srd, new a());
		assertEquals(401,srd.getStatus());
		
	}
	
	private class a implements FilterChain{

		@Override
		public void doFilter(ServletRequest request, ServletResponse response)
				throws IOException, ServletException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
}