/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.connectors;

import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.zones.ContextFactory;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.glance.Glance;

/**
 * This class provides support to connect to Glance from an open stack service implementation that enacpsulates the
 * access mechanism and keeps that version agnostic.
 * 
 * @since Oct 21, 2013
 * @version $Id$
 */
public class GlanceConnector extends Connector {

    /**
     * A reference to the Glance client we create and configure
     */
    private Glance glance;

    /**
     * Creates a new Glance connector and configures it.
     * 
     * @param context
     *            The context we are servicing
     */
    public GlanceConnector(OpenStackContext context) {
        super(context);

        try {
            String version = context.getProviderMetadata().getImageVersion();
            setEndpoint(context.getProperties().getProperty(ContextFactory.PROPERTY_IMAGE_URL));
            getLogger().debug(
                String.format("Initializing Glance connector for version [%s] and url [%s]", version, getEndpoint()));

            /*
             * Allow the specification of a client connector to override the default mechanism of the service
             * loader. This is needed to support use within an OSGi container.
             */
            glance = new Glance(getEndpoint(), getClientConnector());
            glance.setLogger(getLogger());
            glance.token(getAccess().getToken().getId());

            /*
             * configure the client object to set any defined proxy and trusted host list
             */
            String proxyHost = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_HOST);
            String proxyPort = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_PORT);
            String trustedHosts = context.getProperties().getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS, "");
            if (proxyHost != null && proxyHost.length() > 0) {
                glance.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_HOST, proxyHost);
                glance.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_PORT, proxyPort);
            }
            if (trustedHosts != null) {
                glance.getProperties().setProperty(com.woorea.openstack.common.client.Constants.TRUST_HOST_LIST,
                    trustedHosts);
            }

        } catch (NotLoggedInException e) {
            getLogger().error(EELFResourceManager.format(e));
        }
    }

    /**
     * JavaBean accessor to obtain the value of the glance client
     * 
     * @return the glance client
     */
    public Glance getClient() {
        return glance;
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
    	glance.token(getAccess().getToken().getId());
    	
    }
}
