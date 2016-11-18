/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener

class ContextMgr implements ApplicationListener {
	
	def rmgr
	static boolean initCalled = false

	
	public void onApplicationEvent(ApplicationEvent event) {
		// TODO Auto-generated method stub
		if (event.class == org.springframework.context.event.ContextRefreshedEvent && initCalled == false) {
			initCalled = true
			rmgr.init()
		}
	}

}
