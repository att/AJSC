/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.test.DummyProviderContext;

/**
 * @since Mar 4, 2015
 * @version $Id$
 */
public class TestConnectedTenant extends AbstractConnectedTests {
    /**
     * Verify the object is not null
     */
    @Test
    public void testConnectedTenant() {
        ConnectedTenant tenant = new ConnectedTenant(context);
        assertNotNull(tenant);
    }
}
