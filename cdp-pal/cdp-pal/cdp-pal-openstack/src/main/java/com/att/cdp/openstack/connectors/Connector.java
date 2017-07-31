/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.connectors;

import org.slf4j.Logger;

import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.openstack.CommonIdentityService;
import com.att.cdp.openstack.OpenStackContext;
import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.keystone.model.Access;

/**
 * The connector's provide configured access to the various OpenStack client bindings.
 * <p>
 * The configuration of a client to one of hte OpenStack services requires that the IdentityService <code>Access</code>
 * object be obtained and used as the <code>TokenProvider</code> for each client. Since this is common to all connectors
 * for all of the OpenStack services, that has been normalized out to this base class.
 * </p>
 * 
 * @since May 2, 2014
 * @version $Id$
 */

public abstract class Connector {

    /**
     * The access object that manages our access to the OpenStack provider
     */
    private Access access;

    /**
     * The CDP Configuration object
     */
    private Configuration configuration;

    /**
     * The endpoint URL for this connector
     */
    private String endpoint;

    /**
     * The common (version agnostic) identity service for this context
     */
    private CommonIdentityService identity;

    /**
     * The application logger
     */
    private Logger logger;

    /**
     * The reference to the client connector to be used, or null if none was supplied and we use the default service
     * loader mechanism
     */
    private OpenStackClientConnector clientConnector;

    /**
     * Creates the super class from the specified context object. This is then used to obtain the Access object.
     * 
     * @param context
     *            The OpenStackCOntext object to obtain the Access object
     */
    public Connector(OpenStackContext context) {
        /*
         * Allow the specification of a client connector to override the default mechanism of the service
         * loader. This is needed to support use within an OSGi container.
         */
        clientConnector = context.getClientConnector();
        configuration = ConfigurationFactory.getConfiguration();
        logger = configuration.getApplicationLogger();

        identity = (CommonIdentityService) context.getIdentityService();
        access = identity.getAccess();
        access.getToken().getTenant().getId();
    }

    /**
     * JavaBean accessor to obtain the value of endpoint
     * 
     * @return the endpoint value
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint
     *            The value to set the endpoint URL to
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Returns the access object
     * 
     * @return The access object
     */
    public Access getAccess() {
        return access;
    }

    /**
     * @return The logger to be used
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns the client connector to be used
     * 
     * @return The client connector object
     */
    public OpenStackClientConnector getClientConnector() {
        return clientConnector;
    }

    /**
     * Changes the token in the access object to the token currently contained in the identity service. This is used to
     * refresh a token after it expires.
     */
    public void updateToken() {
        access = identity.getAccess();
    }
}
