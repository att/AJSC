/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemErrorHandlerUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(SystemErrorHandlerUtil.class);
	
	public static void callSystemExit(Exception exception) {
		logger.error("Error occurred.",exception);
		if("false".equalsIgnoreCase(System.getProperty("CONTINUE_ON_LISTENER_EXCEPTION"))){
			System.exit(0);
		}
	}
		
	

}
