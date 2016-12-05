/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.zones.spi.DefaultProviderMetadata;
import com.att.cdp.zones.test.DummyProviderContext;

/**
 * @since Sep 30, 2013
 * @version $Id$
 */

public class TestProviderMetadata {

    /**
     * The name of the provider we are testing
     */
    private static final String ProviderName = "DummyProvider";

    private Context context;

    /**
     * Setup the environment for the test
     */
    @Before
    public void setup() {
        try {
            context = ContextFactory.getContext(ProviderName, null);
            assertNotNull(context);
            assertTrue(context instanceof DummyProviderContext);
            assertFalse(context.isLoggedIn());

            IdentityService identity = context.getIdentityService();
            assertNotNull(identity);

        } catch (NoProviderFoundException e) {
            fail("We were supposed to find the TestProvider!");
        }
    }

    /**
     * Tear down the environment after each test
     */
    @After
    public void teardown() {
        assertNotNull(context);
        try {
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("We were supposed to succeed!");
        }
    }

    /**
     * Validate that we can retrieve the metadata successfully
     */
    @Test
    public void testMetadataExists() {
        ProviderMetadata md = context.getProviderMetadata();
        assertNotNull(md);
        assertTrue(md instanceof DefaultProviderMetadata);
    }

    /**
     * Test that we retrieve the correct provider name and version information
     */
    @Test
    public void testProviderNameVersion() {
        ProviderMetadata md = context.getProviderMetadata();
        assertNotNull(md);
        assertEquals("DummyProvider", md.getProviderName());
    }

    /**
     * Test that the features are supported correctly
     */
    @Test
    public void testSupportedFeatures() {
        ProviderMetadata md = context.getProviderMetadata();
        assertNotNull(md);

        assertTrue(md.isComputeSupported());
        assertTrue(md.isIdentitySupported());
        assertTrue(md.isImageSupported());
        assertTrue(md.isNetworkSupported());
        assertTrue(md.isObjectSupported());
        assertTrue(md.isVolumeSupported());
    }
}
