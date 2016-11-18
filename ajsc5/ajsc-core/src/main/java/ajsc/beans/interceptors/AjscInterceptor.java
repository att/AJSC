/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.beans.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface AjscInterceptor {
	public boolean allowOrReject(HttpServletRequest req, HttpServletResponse resp, Map<?,?> paramMap) throws Exception;
}
