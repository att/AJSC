/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ProviderMetadata;

/**
 * This class provides a default implementation for <code>ProviderMetadata</code>.
 * <p>
 * By default, this class examines the provider properties to obtain the values of the specified metadata. If the
 * properties does not contain the value requested, a default value is supplied.
 * </p>
 * <p>
 * The specific provider implementation can extend this class to provide specific metadata capabilities as needed, or to
 * obtain metadata from other sources.
 * </p>
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */

public class DefaultProviderMetadata implements ProviderMetadata {

    /**
     * The application logger to be used
     */
    private Logger appLogger;

    /**
     * A map of the component names by type of component and the version that supports that component
     */
    private Map<String, String> componentVersions = new HashMap<String, String>();

    /**
     * The CDP configuration object
     */
    private Configuration configuration;

    /**
     * The context object for this metadata
     */
    private Context context;

    /**
     * @param context
     *            The context that we provide the metadata for
     */
    public DefaultProviderMetadata(Context context) {
        configuration = ConfigurationFactory.getConfiguration();
        appLogger = configuration.getApplicationLogger();
        this.context = context;
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getClientVersion()
     */
    @Override
    public String getClientVersion() {
        return context.getProperties().getProperty(ProviderProperties.PROPERTY_PROVIDER_CLIENT_VERSION, "").trim();
    }

    /**
     * @param componentType
     *            The type of component that we want to know the resolved version for
     * @return The resolved version, if it was stored in the component versions table (supposed to be)
     */
    public String getComponentVersion(String componentType) {
        return componentVersions.get(componentType);
    }

    /**
     * Set the version for the specified component type in the resolved versions table
     * 
     * @param componentType
     *            The type of component
     * @param version
     *            The version we have resolved to
     */
    public void setComponentVersion(String componentType, String version) {
        componentVersions.put(componentType, version);
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getComputeVersion()
     */
    @Override
    public String getComputeVersion() {
        return componentVersions.get(COMPUTE);
    }

    /**
     * @return the value of configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @return the value of context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getIdentityVersion()
     */
    @Override
    public String getIdentityVersion() {
        return componentVersions.get(IDENTITY);
    }

    /**
     * @throws NotLoggedInException
     *             If the context has not been logged in
     * @see com.att.cdp.zones.ProviderMetadata#getImageVersion()
     */
    @Override
    public String getImageVersion() throws NotLoggedInException {
        return componentVersions.get(IMAGE);
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getNetworkVersion()
     */
    @Override
    public String getNetworkVersion() {
        return componentVersions.get(NETWORK);
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getObjectVersion()
     */
    @Override
    public String getObjectVersion() {
        return componentVersions.get(OBJECT);
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getProviderName()
     */
    @Override
    public String getProviderName() {
        return context.getProperties().getProperty(ProviderProperties.PROPERTY_PROVIDER_NAME, "").trim();
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getSnapshotVersion()
     */
    @Override
    public String getSnapshotVersion() {
        return componentVersions.get(SNAPSHOT);
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getStackVersion()
     */
    @Override
    public String getStackVersion() {
        return componentVersions.get(STACK);
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#getVolumeVersion()
     */
    @Override
    public String getVolumeVersion() {
        return componentVersions.get(VOLUME);
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isBareMetalImplementation()
     */
    @Override
    public boolean isBareMetalImplementation() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_BARE_METAL, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isComputeSupported()
     */
    @Override
    public boolean isComputeSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_COMPUTE, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isFirewallSupported()
     */
    @Override
    public boolean isFirewallSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_FW, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isIdentitySupported()
     */
    @Override
    public boolean isIdentitySupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_IDENTITY, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isImageSupported()
     */
    @Override
    public boolean isImageSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_IMAGE, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isLoadBalancerSupported()
     */
    @Override
    public boolean isLoadBalancerSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_LB, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isNetworkSupported()
     */
    @Override
    public boolean isNetworkSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_NETWORK, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isObjectSupported()
     */
    @Override
    public boolean isObjectSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_OBJECT, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isResumeServerSupported()
     */
    @Override
    public boolean isResumeServerSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_RESUME_SERVER, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isVolumeSupported()
     */
    @Override
    public boolean isSnapshotSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_SNAPSHOT, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isStackSupported()
     */
    @Override
    public boolean isStackSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_STACK, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isStartServerSupported()
     */
    @Override
    public boolean isStartServerSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_START_SERVER, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isStopServerSupported()
     */
    @Override
    public boolean isStopServerSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_STOP_SERVER, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isSuspendServerSupported()
     */
    @Override
    public boolean isSuspendServerSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_SUSPEND_SERVER, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isVolumeSupported()
     */
    @Override
    public boolean isVolumeSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_VOLUME, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isMoveServerSupported()
     */
    @Override
    public boolean isMoveServerSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_MOVE_SERVER, "false").trim());
    }

    /**
     * @see com.att.cdp.zones.ProviderMetadata#isMigrateServerSupported()
     */
    @Override
    public boolean isMigrateServerSupported() {
        return Boolean.parseBoolean(context.getProperties()
            .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_MIGRATE_SERVER, "false").trim());
    }
    
    /**
     * @see com.att.cdp.zones.ProviderMetadata#isKeypairReadSupported()
     */
    @Override
    public boolean isKeypairReadSupported() {
    	return Boolean.parseBoolean(context.getProperties()
                .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_KEYPAIR_READ, "true").trim());
    }
    
    @Override
    public String[] getImmutablePropertiesKeys() {
    	String str = context.getProperties().getProperty(ProviderProperties.PROPERTY_PROVIDER_CONFIGURATION_KEYS);
    	if (str != null) {
    		return str.split(",");
    	}
    	return null;
    }
   

    
	@Override
	public boolean isFloatingIpPoolsSupported() {
        return Boolean.parseBoolean(context.getProperties()
                .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_FLOATING_IP_POOL, "false").trim());
	}	

	@Override
	public boolean isSSHSupported() {
		  return Boolean.parseBoolean(context.getProperties()
	                .getProperty(ProviderProperties.PROPERTY_PROVIDER_SUPPORTS_SSH, "true").trim());
	}
	
	@Override
	public boolean isHostNameToBeGenerated() {
		return Boolean.parseBoolean(context.getProperties()
                .getProperty(ProviderProperties.PROPERTY_PROVIDER_HOSTNAME_GENERATED, "true").trim());
	
	}
	
    @Override
    public boolean isNetworkMetadataSupported() {
        return Boolean.parseBoolean(context.getProperties()
                .getProperty(ProviderProperties.PROPERTY_PROVIDER_GETNETWORKMETADATA, "true").trim());
    
    }
}
