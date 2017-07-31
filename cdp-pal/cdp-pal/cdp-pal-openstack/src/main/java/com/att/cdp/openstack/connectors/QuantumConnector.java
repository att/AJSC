/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.connectors;

import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.zones.ContextFactory;
import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.nova.Nova;
import com.woorea.openstack.quantum.Quantum;

/**
 * This class provides support to connect to Quantum from an open stack service implementation that encapsulates the
 * access mechanism and keeps that version agnostic.
 * 
 * @since Oct 21, 2013
 * @version $Id$
 */
public class QuantumConnector extends Connector {

    /**
     * A reference to the Glance client we create and configure
     */
    private Quantum quantum;

    /**
     * Creates a new Glance connector and configures it.
     * 
     * @param context
     *            The context we are servicing
     */
    public QuantumConnector(OpenStackContext context) {
        super(context);

        String version = context.getProviderMetadata().getNetworkVersion();
        setEndpoint(context.getProperties().getProperty(ContextFactory.PROPERTY_NETWORK_URL));
        getLogger().debug(
            String.format("Initializing Quantum connector for version [%s] and url [%s]", version, getEndpoint()));

        /*
         * Allow the specification of a client connector to override the default mechanism of the service
         * loader. This is needed to support use within an OSGi container.
         */
        quantum = new Quantum(getEndpoint(), getClientConnector());
        quantum.setLogger(getLogger());
        quantum.token(getAccess().getToken().getId());

        /*
         * configure the client object to set any defined proxy and trusted host list
         */
        String proxyHost = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_HOST);
        String proxyPort = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_PORT);
        String trustedHosts = context.getProperties().getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS, "");
        if (proxyHost != null && proxyHost.length() > 0) {
            quantum.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_HOST, proxyHost);
            quantum.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_PORT, proxyPort);
        }
        if (trustedHosts != null) {
            quantum.getProperties().setProperty(com.woorea.openstack.common.client.Constants.TRUST_HOST_LIST,
                trustedHosts);
        }
    }

    /**
     * JavaBean accessor to obtain the value of the quantum client
     * 
     * @return the quantum client
     */
    public Quantum getClient() {
        return quantum;
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
    	quantum.token(getAccess().getToken().getId());
    	
    }
}
