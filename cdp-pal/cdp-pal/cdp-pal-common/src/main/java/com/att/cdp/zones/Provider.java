/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.cdp.zones;

import java.util.Properties;

/**
 * All cloud infrastructure implementations using the RIME api must be exposed as a <code>Provider</code> object.
 * <p>
 * The provider is a factory for obtaining contexts to the actual service provider back-end. It is these contexts that
 * are used to perform the various operations. The provider implementation is similar to a JDBC driver in that it
 * directs and coordinates the operation of the provider implementation.
 * </p>
 * <p>
 * Each provider class is used to manage one or more <code>Context</code> objects. Each context object represents one
 * "session" or "connection" to the service provider. A client may request as many contexts as they need, where each
 * context can be used to perform multiple operations. The operations performed on one context are isolated from the
 * operations performed on another context, even if the contexts are from the same provider.
 * </p>
 * <p>
 * The provider implementation class also identifies the component parts that are used to implement the various service
 * abstractions (Compute, Image, Volume, Identity, etc). These identifications are used in the provider metadata map of
 * component versions, and elsewhere. The provider metadata component versions is used to dynamically load the
 * appropriate implementation versions of these services for each context. Since the components are loaded per-context,
 * different contexts may connect to and use the services of different versions of these components, based on the
 * versions of the specific endpoints that they connect to.
 * </p>
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */

public interface Provider {

    /**
     * Returns the publicly known (advertised) name that this provider is called by clients that wish to use its
     * services.
     * 
     * @return The name a client would use to access the service provider
     */
    String getName();

    /**
     * This method allows the caller to request a context and pass an optional <code>Properties</code> object to be used
     * to configure the context.
     * <p>
     * If a properties object is provided, the underlying provider implementation can use that to configure it's
     * connection to the cloud infrastructure. The content of the properties object is defined by the specific service
     * provider. A service provider should ignore properties that it does not understand.
     * </p>
     * 
     * @param properties
     *            An optional properties object used to configure the context, or null if default configuration is
     *            desired.
     * @return The open (usable) context object
     */
    Context openContext(Properties properties);
}
