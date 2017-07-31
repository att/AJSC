/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.cdp.openstack.connectors;

import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.ServiceCatalog;
import com.att.cdp.zones.ContextFactory;
import com.woorea.openstack.heat.Heat;

/**
 * @since Jan 16, 2015
 * @version $Id$
 */

public class HeatConnector extends Connector {

    /**
     * The heat client
     */
    private Heat heat;

    /**
     * @param context
     *            The context we are servicing
     */
    @SuppressWarnings("nls")
    public HeatConnector(OpenStackContext context) {
        super(context);
        ServiceCatalog catalog = context.getServiceCatalog();

        setEndpoint(catalog.resolveServiceURL(ServiceCatalog.OS_STACK_SERVICE_TYPE) + "/" + context.getTenantId());

        /*
         *  Allow the specification of a client connector to override the default mechanism of the service
         * loader. This is needed to support use within an OSGi container.
         */
        heat = new Heat(getEndpoint(), getClientConnector());
        heat.setLogger(getLogger());
        heat.token(getAccess().getToken().getId());

        /*
         * configure the client object to set any defined proxy and trusted host list
         */
        String proxyHost = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_HOST);
        String proxyPort = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_PORT);
        String trustedHosts = context.getProperties().getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS, "");
        if (proxyHost != null && proxyHost.length() > 0) {
            heat.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_HOST, proxyHost);
            heat.getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_PORT, proxyPort);
        }
        if (trustedHosts != null) {
            heat.getProperties()
                .setProperty(com.woorea.openstack.common.client.Constants.TRUST_HOST_LIST, trustedHosts);
        }
    }

    /**
     * @return The client being wrapped by this connector
     */
    public Heat getClient() {
        return heat;
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
    	heat.token(getAccess().getToken().getId());
    }

}
