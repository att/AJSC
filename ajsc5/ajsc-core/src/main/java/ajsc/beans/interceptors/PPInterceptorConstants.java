/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.beans.interceptors;

public class PPInterceptorConstants 
{
	public static final String SYSPROP_USEPPSERVICE = "usePPService";
	public static final String SYSPROP_PPXML = "PartnerProfileXML";
	public static final String SYSPROP_SVCVER = "PartnerProfileServiceVersion";
	public static final String SYSPROP_SVCRO = "PartnerProfileServiceRouteOffer";
	public static final String SYSPROP_PPPOL = "PPServicePoliciesFile";
	public static final String SYSPROP_PPREFRESH = "PPRefreshInterval";
	public static final int DEFAULT_PPREFRESH = 30;
	public static final String HTTP_HEADER_SERVICE_NAME = "X-CSI-ServiceName";
	public static final String HTTP_HEADER_USER_NAME = "X-CSI-UserName";
}
