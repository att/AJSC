/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.config;

import javax.sql.DataSource;

import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.att.ajsc.camunda.util.CamundaHistoryEventHandler;

@Configuration
public class CamundaEngineConfiguration {

	 /* 
	 * Camunda Identity databse DataSource configuration
	 */
	  @Primary
	  @Bean(name="camundaBpmDataSource")
	  @ConfigurationProperties(prefix="spring.datasource")
	  public DataSource dataSource() {	      
		  return DataSourceBuilder
			        .create()
			        .build();
	  }
	  
	  @Bean(name="historyEventHandler")
	  public static HistoryEventHandler HistoryEventHandlerConfiguration() {	    
		  return new CamundaHistoryEventHandler();
	  }
	 
}