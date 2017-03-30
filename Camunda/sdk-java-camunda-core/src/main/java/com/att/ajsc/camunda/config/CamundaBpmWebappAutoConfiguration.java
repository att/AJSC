/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.config;

import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.camunda.bpm.spring.boot.starter.webapp.filter.LazyDelegateFilter.InitHook;
import org.camunda.bpm.spring.boot.starter.webapp.filter.LazyInitRegistration;
import org.camunda.bpm.spring.boot.starter.webapp.filter.ResourceLoaderDependingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(CamundaBpmAutoConfiguration.class)
public class CamundaBpmWebappAutoConfiguration extends WebMvcConfigurerAdapter {

  @Value("${com.att.ajsc.camunda.contextPath:/camunda}")
  private String CAMUNDA_SUFFIX;

  @Autowired
  private ResourceLoader resourceLoader;

  @Value("${camunda.bpm.webapp.index-redirect-enabled:true}")
  private boolean isIndexRedirectEnabled;

  @Bean(name = "resourceLoaderDependingInitHook")
  public InitHook<ResourceLoaderDependingFilter> resourceLoaderDependingInitHook() {
    return filter -> filter.setResourceLoader(resourceLoader);
  }

  @Bean
  public LazyInitRegistration lazyInitRegistration() {
    return new LazyInitRegistration();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(CAMUNDA_SUFFIX+"/lib/**").addResourceLocations("classpath:/lib/");
    registry.addResourceHandler(CAMUNDA_SUFFIX+"/api/**").addResourceLocations("classpath:/api/");
    registry.addResourceHandler(CAMUNDA_SUFFIX+"/app/**").addResourceLocations("classpath:/app/");
    super.addResourceHandlers(registry);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    if (isIndexRedirectEnabled) {
      registry.addRedirectViewController("/", CAMUNDA_SUFFIX+"/app/");
      registry.addRedirectViewController(CAMUNDA_SUFFIX+"/", CAMUNDA_SUFFIX+"/app/");
    }
    super.addViewControllers(registry);
  }

}
