/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.utils;

import static ajsc.utils.UtilLib.isNullOrEmpty;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemParams 
{
	private String pid_ = "N/A";
	private String cluster_ = "N/A";
	private String routeOffer_ = "N/A";
	private String instanceName_ = "N/A";
	private String appName_ = "N/A";
	private String appVersion_ = "N/A";
	private String ipAddress_ = "N/A";
	private String hostName_ = "N/A";
	private String vtier_ = "N/A";
	private String env_ = "N/A";
	private String namespace;
	private String serviceVersion;
	private String serviceName;
	
	
	private static final String PID = "Pid";
	private static final String LRM_HOST = "lrmHost";
	private static final String ROUTE_OFFER = "routeOffer";
	private static final String APP_NAME = "appName";
	private static final String APP_VERSION = "appVersion";
	private static final String LRM_RO = "lrmRO";
	private static final String LRM_ENV = "lrmEnv";
	private static final String LRM_NAME = "lrmRName";
	private static final String LRM_VER = "lrmRVer";
	private static final String HYDRA_APPL_CTXT = "VERSION_HYDRAAPPLDATA_ENVCONTEXT";
	private static final String SOACLOUD_NAMESPACE ="SOACLOUD_NAMESPACE";
	private static final String SOACLOUD_SERVICE_VERSION ="AJSC_SERVICE_VERSION";
	private static final String SOACLOUD_ROUTE_OFFER ="SOACLOUD_ROUTE_OFFER";
	private static SystemParams sparams_ = null;
	
	
	private SystemParams()
	{
		try
		{
			int i = -1;
			
			pid_ = System.getProperty(PID, "N/A");
			
			if(System.getProperty(APP_NAME) == null){
				
				appName_ = System.getProperty(LRM_NAME,"N/A");
			}
			else{
				
				appName_ = System.getProperty(APP_NAME,"N/A");
				
			}
			env_ = System.getProperty(LRM_ENV, "N/A");
			
			hostName_ = System.getProperty(LRM_HOST);
			if ( isNullOrEmpty(hostName_) )
			{
				try
				{
					hostName_ = InetAddress.getLocalHost().getHostName();
				}
				catch (UnknownHostException uhe)
				{
					hostName_ = "N/A";
				}
			}
			i = hostName_.indexOf('.');
			if ( i > 0 ) 
				vtier_ = hostName_.substring(0, i);
			else
				vtier_ = hostName_;
			
			try
			{
				ipAddress_ = InetAddress.getLocalHost().getHostAddress();
			}
			catch (UnknownHostException uhe)
			{
				ipAddress_ = "N/A";
			}
			if(System.getProperty(APP_VERSION) == null){
				
				appVersion_ = System.getProperty(LRM_VER,"N/A");		
			}
			else{
				appVersion_ = System.getProperty(APP_VERSION,"N/A");
				
			}
			
			i = appVersion_.indexOf('.');
			if ( i > 0 ) appVersion_ = appVersion_.substring(0, i);

			cluster_ = System.getProperty(LRM_RO, "N/A");
			
			if(System.getProperty(ROUTE_OFFER) == null){
				
				routeOffer_ = System.getProperty(SOACLOUD_ROUTE_OFFER,"N/A");
			
			}
			else{
				routeOffer_ = System.getProperty(ROUTE_OFFER,"N/A");
			
			}
			String versionApplEnv = System.getProperty(HYDRA_APPL_CTXT);
			if ( !isNullOrEmpty(versionApplEnv) )
			{
				String splits[] = versionApplEnv.split("\\/");
				
				if ( splits != null && splits.length == 3 )
				{
					if ( "N/A".equals(appVersion_) )
					{
						appVersion_ = splits[0];
						i = appVersion_.indexOf('.');
						if ( i > 0 ) appVersion_ = appVersion_.substring(0, i);
					}
					if ( "N/A".equals(routeOffer_) )
						routeOffer_ = splits[1];
					if ( "N/A".equals(cluster_) )
						cluster_ = splits[1];
				}
			}
			
			serviceName = appName_ +"-" + appVersion_;
			
			StringBuilder sb = new StringBuilder();
			sb.append("ajsc:");
			sb.append(System.getProperty(LRM_NAME,"N/A"));
			sb.append("-");
			sb.append(System.getProperty(LRM_VER,"N/A"));				
			sb.append("-");
			sb.append(routeOffer_);
			sb.append("-");
			sb.append(hostName_);
			sb.append("-");
			sb.append(pid_);
			instanceName_ = sb.toString();
			
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		
	}
	
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public static SystemParams instance()
	{
		if ( sparams_ == null )
		{
			synchronized(SystemParams.class)
			{
				if ( sparams_ == null )
					sparams_ = new SystemParams();
			}
		}
		return sparams_;
	}

	public String getPid()
	{
		return pid_;
	}

	public String getCluster() 
	{
		return cluster_;
	}

	public String getRouteOffer() 
	{
		return routeOffer_;
	}

	public String getInstanceName() 
	{
		return instanceName_;
	}

	public String getAppName() 
	{
		return appName_;
	}

	public String getAppVersion() 
	{
		return appVersion_;
	}

	public String getIpAddress() 
	{
		return ipAddress_;
	}

	public String getHostName() 
	{
		return hostName_;
	}

	public String getVtier() 
	{
		return vtier_;
	}
	
	public String getEnvContext()
	{
		return env_;
	}
	
	// USE ONLY FOR TESTING
	public static void deleteInstance()
	{
		sparams_ = null;
	}
	
}
