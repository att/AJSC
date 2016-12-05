/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.zones.test.DummyProviderContext;

public class TestVolumeService {

    /**
     * The provider name we are bootstrapping (test provider)
     */
    private static final String ProviderName = "DummyProvider";

    /**
     * The provider context
     */
    private Context context;

    private VolumeService volumeService;

    /**
     * Setup the environment prior to each test
     */
    @Before
    public void setup() {
        try {
            context = ContextFactory.getContext(ProviderName, null);
            assertNotNull(context);
            assertTrue(context instanceof DummyProviderContext);

            volumeService = context.getVolumeService();
            // assertNotNull(volumeService);

        } catch (NoProviderFoundException e) {
            fail("We were supposed to find the TestProvider!");
        }
    }

    /**
     * Close the provider after each test
     */
    @After
    public void teardown() {
        try {
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Verify that when we obtain a volumes , it's not null
     */
    /*
     * @Test public void testGetVolumes() { try { List<Volume> vol = volumeService.getVolumes(); } catch (RimeException
     * e) { e.printStackTrace(); fail("We were supposed to find the volumes!"); } }
     */

}
