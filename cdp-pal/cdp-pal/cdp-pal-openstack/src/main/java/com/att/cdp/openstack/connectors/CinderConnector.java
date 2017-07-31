/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.connectors;

import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.zones.ContextFactory;
import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.cinder.Cinder;
import com.woorea.openstack.glance.Glance;

/**
 * This class provides support to connect to Cinder from an open stack service implementation that enacpsulates the
 * access mechanism and keeps that version agnostic.
 * 
 * @since Oct 21, 2013
 * @version $Id$
 */

public class CinderConnector extends Connector {

    /**
     * A reference to the Cinder client we create and configure
     */
    private Cinder cinder;

    /**
     * Creates a new Cinder connector and configures it.
     * 
     * @param context
     *            The context we are servicing
     */
    public CinderConnector(OpenStackContext context) {
        super(context);

        String version = context.getProviderMetadata().getVolumeVersion();
        setEndpoint(context.getProperties().getProperty(ContextFactory.PROPERTY_VOLUME_URL) + "/"
            + context.getTenantId());
        getLogger().debug(
            String.format("Initializing Cinder connector for version [%s] and url [%s]", version, getEndpoint()));

        /*
         * Allow the specification of a client connector to override the default mechanism of the service
         * loader. This is needed to support use within an OSGi container.
         */
        cinder = new Cinder(getEndpoint(), getClientConnector());
        cinder.setLogger(getLogger());
        cinder.token(getAccess().getToken().getId());

        /*
         * configure the client object to set any defined proxy and trusted host list
         */
        String proxyHost = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_HOST);
        String proxyPort = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_PORT);
        String trustedHosts = context.getProperties().getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS, "");
        if (proxyHost != null && proxyHost.length() > 0) {
            cinder.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_HOST, proxyHost);
            cinder.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_PORT, proxyPort);
        }
        if (trustedHosts != null) {
            cinder.getProperties().setProperty(com.woorea.openstack.common.client.Constants.TRUST_HOST_LIST,
                trustedHosts);
        }
    }

    /**
     * JavaBean accessor to obtain the value of the Cinder client
     * 
     * @return the glance client
     */
    public Cinder getClient() {
        return cinder;
    }
    
    /**
     * Updates the expired token by re-authenticating, getting a new unexpired token (in the access object), and then
     * updating the token held by this client wrapped by this connector to use the new token.
     * 
     * @see com.att.cdp.openstack.connectors.Connector#updateToken()
     */
    @Override
    public void updateToken() {
    	super.updateToken();
    	cinder.token(getAccess().getToken().getId());
    	
    }
}
