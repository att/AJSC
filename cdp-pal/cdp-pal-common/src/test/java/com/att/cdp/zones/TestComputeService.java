/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.TimeoutException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Server.Status;
import com.att.cdp.zones.test.DummyProviderContext;

/**
 * Test the compute service implementation (abstract compute service)
 *
 * @since Nov 24, 2015
 * @version $Id$
 */
public class TestComputeService {

    /**
     * The provider name we are bootstrapping (test provider)
     */
    @SuppressWarnings("nls")
    private static final String ProviderName = "DummyProvider";

    /**
     * The provider context
     */
    private Context context;

    private ComputeService computeService;

    private Server server;

    /**
     * Setup the environment prior to each test
     * 
     * @throws ZoneException
     */
    @SuppressWarnings("nls")
    @Before
    public void setup() throws ZoneException {
        try {
            context = ContextFactory.getContext(ProviderName, null);
            assertNotNull(context);
            assertTrue(context instanceof DummyProviderContext);

            computeService = context.getComputeService();
            assertNotNull(computeService);

            server = computeService.getServer("");
            assertNotNull(server);
            assertEquals("Dummy", server.getName());
            assertTrue(server.isConnected());

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
     * This method tests that we indeed receive the exception for a negative poll interval
     * 
     * @throws ZoneException
     */
    @Test(expected = InvalidRequestException.class)
    public void testWaitForStateChangeNegativeInterval() throws ZoneException {
        computeService.waitForStateChange(-1, 100, server, Server.Status.READY);
    }

    /**
     * This method tests that we indeed receive the exception for a zero poll interval
     * 
     * @throws ZoneException
     */
    @Test(expected = InvalidRequestException.class)
    public void testWaitForStateChangeZeroInterval() throws ZoneException {
        computeService.waitForStateChange(0, 100, server, Server.Status.READY);
    }

    /**
     * This method tests that we indeed receive the exception for a non-zero timeout less than the poll interval
     * 
     * @throws ZoneException
     */
    @Test(expected = InvalidRequestException.class)
    public void testWaitForStateChangeTimeoutLessThanPoll() throws ZoneException {
        computeService.waitForStateChange(30, 10, server, Server.Status.READY);
    }

    /**
     * This method tests that we indeed receive the exception for a null server
     * 
     * @throws ZoneException
     */
    @Test(expected = InvalidRequestException.class)
    public void testWaitForStateChangeNullServer() throws ZoneException {
        computeService.waitForStateChange(30, 30, null, Server.Status.READY);
    }

    /**
     * This method tests that we indeed receive the exception for a null state
     * 
     * @throws ZoneException
     */
    @Test(expected = InvalidRequestException.class)
    public void testWaitForStateChangeNullState() throws ZoneException {
        computeService.waitForStateChange(30, 30, server, (Status[]) null);
    }

    /**
     * This method tests that we indeed receive the exception for a null states
     * 
     * @throws ZoneException
     */
    @Test(expected = InvalidRequestException.class)
    public void testWaitForStateChangeNullStates() throws ZoneException {
        computeService.waitForStateChange(30, 30, server, null, null, null);
    }
}
