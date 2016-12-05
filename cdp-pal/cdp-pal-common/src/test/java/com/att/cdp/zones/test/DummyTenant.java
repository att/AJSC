/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.test;

import com.att.cdp.zones.spi.model.ConnectedTenant;

/**
 * This class represents a dummy tenant implementation useful for testing the API
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */

public class DummyTenant extends ConnectedTenant {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context
     */
    public DummyTenant(DummyProviderContext context) {
        super(context);
        setName("DummyTenant");
    }

}
