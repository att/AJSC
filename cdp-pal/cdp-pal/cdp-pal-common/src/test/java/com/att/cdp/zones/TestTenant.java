/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.test.DummyProviderContext;

/**
 * This class tests the tenant object to make sure that it works correctly for the dummy provider.
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */
public class TestTenant {

    private static final String ProviderName = "DummyProvider";

    private Context context;

    /**
     * Set up the context for the tests
     */
    @Before
    public void setup() {
        try {
            context = ContextFactory.getContext(ProviderName, null);
            assertNotNull(context);
            assertTrue(context instanceof DummyProviderContext);

            Provider provider = context.getProvider();
            assertNotNull(provider);
            assertEquals(ProviderName, provider.getName());
        } catch (NoProviderFoundException e) {
            fail("We were supposed to find the TestProvider!");
        }
    }

    /**
     * Close the context after the tests
     */
    @After
    public void tearDown() {
        assertNotNull(context);
        try {
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected result!");
        }
    }

    /**
     * tests that once a context exists and a login has occurred, that the tenant object can be obtained
     */
    @SuppressWarnings("unused")
    @Test
    public void testConnectedTenant() {
        Tenant tenant;
        try {
            tenant = context.getTenant();
            assertNotNull(tenant);
            // Context ctx = tenant.getContext();
        } catch (ZoneException e1) {
            e1.printStackTrace();
            fail("Should have succeeded!");
        }

    }
}
