/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.connectors;

import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.impl.AbstractOpenStackIdentityService;
import com.att.cdp.zones.ContextFactory;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.quantum.Quantum;

/**
 * This class provides support to connect to Quantum from an open stack service implementation that encapsulates the
 * access mechanism and keeps that version agnostic.
 * 
 * @since Oct 21, 2013
 * @version $Id$
 */
public class KeystoneConnector extends Connector {

    /**
     * A reference to the Glance client we create and configure
     */
    private com.woorea.openstack.keystone.Keystone keystone;

    /**
     * Creates a new Keystone connector and configures it.
     * 
     * @param context
     *            The context we are servicing
     */
    public KeystoneConnector(OpenStackContext context) {
        super(context);

        String version = context.getProviderMetadata().getNetworkVersion();
        setEndpoint(context.getProperties().getProperty(ContextFactory.PROPERTY_NETWORK_URL));
        getLogger().debug(
            String.format("Initializing Quantum connector for version [%s] and url [%s]", version, getEndpoint()));

        AbstractOpenStackIdentityService service = (AbstractOpenStackIdentityService) context.getIdentityService();
        keystone = service.getKeystone();
        keystone.setLogger(getLogger());
        keystone.token(getAccess().getToken().getId());

        /*
         * It is not necessary to setup the proxy and certificate options because the keystone client has already been
         * created and is authenticated during login, where all this is already set up. Keystone is special because we
         * had to set these properties before logging in, so lets just keep using them in that client, making the
         * "connector" just a proxy to the existing keystone client.
         */
    }

    /**
     * JavaBean accessor to obtain the value of the quantum client
     * 
     * @return the quantum client
     */
    public Keystone getClient() {
        return keystone;
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
    	keystone.token(getAccess().getToken().getId());
    	
    }
}
