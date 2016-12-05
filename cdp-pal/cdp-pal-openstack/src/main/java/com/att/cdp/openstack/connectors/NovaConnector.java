/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.connectors;

import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.zones.ContextFactory;
import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.nova.Nova;

/**
 * @since Oct 21, 2013
 * @version $Id$
 */
public class NovaConnector extends Connector {

    /**
     * The nova client
     */
    private Nova nova;

    /**
     * Creates a new nova connector and configures it.
     * 
     * @param context
     *            The context we are servicing
     */
    public NovaConnector(OpenStackContext context) {
        super(context);

        String version = context.getProviderMetadata().getComputeVersion();
        setEndpoint(context.getProperties().getProperty(ContextFactory.PROPERTY_COMPUTE_URL) + "/"
            + context.getTenantId());
        getLogger().debug(
            String.format("Initializing Nova connector for version [%s] and url [%s]", version, getEndpoint()));

        /*
         * Allow the specification of a client connector to override the default mechanism of the service
         * loader. This is needed to support use within an OSGi container.
         */
        nova = new Nova(getEndpoint(), getClientConnector());

        nova.setLogger(getLogger());
        nova.token(getAccess().getToken().getId());

        /*
         * configure the client object to set any defined proxy and trusted host list
         */
        String proxyHost = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_HOST);
        String proxyPort = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_PORT);
        String trustedHosts = context.getProperties().getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS, "");
        if (proxyHost != null && proxyHost.length() > 0) {
            nova.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_HOST, proxyHost);
            nova.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_PORT, proxyPort);
        }
        if (trustedHosts != null) {
            nova.getProperties()
                .setProperty(com.woorea.openstack.common.client.Constants.TRUST_HOST_LIST, trustedHosts);
        }
    }

    /**
     * JavaBean accessor to obtain the value of nova
     * 
     * @return the nova client
     */
    public Nova getClient() {
        return nova;
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
    	nova.token(getAccess().getToken().getId());
    	
    }
}
