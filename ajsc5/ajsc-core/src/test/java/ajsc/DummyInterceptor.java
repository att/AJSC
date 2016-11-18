/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ajsc.beans.interceptors.AjscInterceptor;

public class DummyInterceptor implements AjscInterceptor{
	
	private static DummyInterceptor classInstance = null;
	private static boolean allow=false;
	
	public static DummyInterceptor getInstance() {
		if(classInstance == null){
			classInstance = new DummyInterceptor();
		}
		return classInstance;
	}
	
	public boolean allowOrReject(HttpServletRequest req, HttpServletResponse resp, Map<?,?> paramMap) throws Exception{
		resp.setStatus(401);		
		return allow;
	}

}
