/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.config;

import static java.util.Collections.singletonMap;
import static org.glassfish.jersey.servlet.ServletProperties.JAXRS_APPLICATION_CLASS;

import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionTrackingMode;

import org.camunda.bpm.admin.impl.web.AdminApplication;
import org.camunda.bpm.admin.impl.web.bootstrap.AdminContainerBootstrap;
import org.camunda.bpm.cockpit.impl.web.CockpitApplication;
import org.camunda.bpm.cockpit.impl.web.bootstrap.CockpitContainerBootstrap;
import org.camunda.bpm.engine.rest.filter.CacheControlFilter;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.camunda.bpm.spring.boot.starter.webapp.filter.LazySecurityFilter;
import org.camunda.bpm.tasklist.impl.web.TasklistApplication;
import org.camunda.bpm.tasklist.impl.web.bootstrap.TasklistContainerBootstrap;
import org.camunda.bpm.webapp.impl.engine.EngineRestApplication;
import org.camunda.bpm.webapp.impl.security.auth.AuthenticationFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;


@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(CamundaBpmAutoConfiguration.class)
public class CamundaBpmWebappInitializer implements ServletContextInitializer {

  private static final EnumSet<DispatcherType> DISPATCHER_TYPES = EnumSet.of(DispatcherType.REQUEST);

  private static final String AJSC_CADI_PROPS_FILE = "cadi.properties";
  
  private ServletContext servletContext;
  
  @Value("${camunda.bpm.webapp.security-config-file:/META-INF/securityFilterRules.json}")
  private String securityConfigFile;
  
  @Value("${com.att.ajsc.camunda.contextPath:/camunda}")
  private String CAMUNDA_SUFFIX;
  
  @Value("${ajsc.enableCADIAAF:true}")
  private String CADI_AAF_ENABLED;
  
  private final Logger log = Logger.getLogger(CamundaBpmWebappInitializer.class.getName());
  
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    this.servletContext = servletContext;
    servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
    servletContext.addListener(new CockpitContainerBootstrap());
    servletContext.addListener(new AdminContainerBootstrap());
    servletContext.addListener(new TasklistContainerBootstrap());
    
    String cadiPropFile = System.getProperty("com.att.ajsc.cadi.file");
   	if(cadiPropFile==null){
   		cadiPropFile = "etc/config/cadi.properties"; //Set default value
   	}
   	
  /* 	if(CADI_AAF_ENABLED.equalsIgnoreCase("true")){    		
   		registerFilter("cadiFilter", CadiFilter.class, singletonMap("cadi_prop_files", cadiPropFile),CAMUNDA_SUFFIX+"/app/*");
   	    registerFilter("Authentication Filter", AttCamundaAuthenticationFilter.class, CAMUNDA_SUFFIX+"/*");
   	}else{
   		registerFilter("Authentication Filter", AuthenticationFilter.class, CAMUNDA_SUFFIX+"/*");
   	}  */ 	
   
    registerFilter("Security Filter", LazySecurityFilter.class, singletonMap("configFile", securityConfigFile), CAMUNDA_SUFFIX+"/*");   
    registerFilter("Engines Filter", com.att.ajsc.camunda.config.filter.LazyProcessEnginesFilter.class, CAMUNDA_SUFFIX+"/app/*");
    registerFilter("CacheControlFilter", CacheControlFilter.class, CAMUNDA_SUFFIX+"/api/*");

    registerServlet("Cockpit Api", CockpitApplication.class, CAMUNDA_SUFFIX+"/api/cockpit/*");
    registerServlet("Admin Api", AdminApplication.class, CAMUNDA_SUFFIX+"/api/admin/*");
    registerServlet("Tasklist Api", TasklistApplication.class, CAMUNDA_SUFFIX+"/api/tasklist/*");
    registerServlet("Engine Api", EngineRestApplication.class, CAMUNDA_SUFFIX+"/api/engine/*");    
    
  }

  private FilterRegistration registerFilter(final String filterName, final Class<? extends Filter> filterClass, final String... urlPatterns) {
    return registerFilter(filterName, filterClass, null, urlPatterns);
  }

  private FilterRegistration registerFilter(final String filterName, final Class<? extends Filter> filterClass, final Map<String, String> initParameters,
      final String... urlPatterns) {
    FilterRegistration filterRegistration = servletContext.getFilterRegistration(filterName);

    if (filterRegistration == null) {
      filterRegistration = servletContext.addFilter(filterName, filterClass);
      filterRegistration.addMappingForUrlPatterns(DISPATCHER_TYPES, true, urlPatterns);

      if (initParameters != null) {
        filterRegistration.setInitParameters(initParameters);
      }

    }

    return filterRegistration;
  }

  private ServletRegistration registerServlet(final String servletName, final Class<?> applicationClass, final String... urlPatterns) {
    ServletRegistration servletRegistration = servletContext.getServletRegistration(servletName);

    if (servletRegistration == null) {
      servletRegistration = servletContext.addServlet(servletName, ServletContainer.class);
      servletRegistration.addMapping(urlPatterns);
      servletRegistration.setInitParameters(singletonMap(JAXRS_APPLICATION_CLASS, applicationClass.getName()));

    }

    return servletRegistration;
  }
}
