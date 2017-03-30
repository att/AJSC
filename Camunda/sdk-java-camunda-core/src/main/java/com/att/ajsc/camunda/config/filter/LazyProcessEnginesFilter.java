/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.config.filter;

import org.camunda.bpm.spring.boot.starter.webapp.filter.LazyDelegateFilter;
import org.camunda.bpm.spring.boot.starter.webapp.filter.ResourceLoaderDependingFilter;

public class LazyProcessEnginesFilter extends LazyDelegateFilter<ResourceLoaderDependingFilter> {

  public LazyProcessEnginesFilter() {
    super(ResourceLoadingProcessEnginesFilter.class);
  }

}