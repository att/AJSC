/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.test.DummyProviderContext;

/**
 *         Hafenstein</a>
 * @since Apr 29, 2015
 * @version $Id$
 */

public abstract class AbstractConnectedTests {
    /**
     *
     */
    @SuppressWarnings("nls")
    protected static final String PROVIDER_NAME = "DummyProvider";

    /**
     * 
     */
    protected ComputeService compService;

    /**
     * 
     */
    protected Context context;

    /**
     * Setup the environment prior to each test
     * 
     * @throws NoProviderFoundException
     *             if the provider cant be found
     */
    @Before
    public void setup() throws NoProviderFoundException {
        context = ContextFactory.getContext(PROVIDER_NAME, null);
        assertNotNull(context);
        assertTrue(context instanceof DummyProviderContext);

        compService = context.getComputeService();
        assertNotNull(compService);
    }

    /**
     * Close the provider after each test
     * 
     * @throws IOException
     *             if the close fails
     */
    @After
    public void teardown() throws IOException {
        context.close();
    }

}
