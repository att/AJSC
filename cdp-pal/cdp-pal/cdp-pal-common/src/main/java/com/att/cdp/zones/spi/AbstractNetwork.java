/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.exceptions.NotSupportedException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.Provider;
import com.att.cdp.zones.model.Port;
import com.att.cdp.zones.model.Subnet;

/**
 * This class is an abstract base class that provides common support for network service implementations.
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */
public abstract class AbstractNetwork extends AbstractService implements NetworkService {

    /**
     * Create the abstract network service implementation for the specified context
     * 
     * @param context
     *            The context that we are providing the services for
     */
    public AbstractNetwork(Context context) {
        super(context);
    }

    /**
     * This method is implemented here until it can be implemented for all providers. This allows the specific providers
     * to be updated incrementally to add this capability. TODO Add this capability to all providers. Currently only
     * implemented in OpenStack.
     * 
     * @see com.att.cdp.zones.NetworkService#createPort(com.att.cdp.zones.model.Subnet, com.att.cdp.zones.model.Port)
     */
    @Override
    public Port createPort(Subnet subnet, Port model) throws ZoneException {
        Context context = getContext();
        Provider provider = context.getProvider();
        throw new NotSupportedException(String.format(
            "Provider %s does not support createPort using a Port object as a model", provider.getName()));
    }

}
