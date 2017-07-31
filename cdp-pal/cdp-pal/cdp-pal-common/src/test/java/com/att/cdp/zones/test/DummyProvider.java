/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.test;

import java.util.Properties;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.AbstractProvider;

/**
 * This is a test provider.
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */

public class DummyProvider extends AbstractProvider {

    /**
     * @see com.att.cdp.zones.Provider#openContext(java.util.Properties)
     */
    @Override
    public Context openContext(Properties properties) {
        return new DummyProviderContext(this, getDefaults(), properties);
    }
}
