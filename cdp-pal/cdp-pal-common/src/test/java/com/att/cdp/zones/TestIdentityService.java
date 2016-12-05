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
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.AuthenticationException;
import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.test.DummyProviderContext;

/**
 * This test is used to verify that the identity service (dummy implementation) is working correctly and is used to
 * check the abstract base classes, default behaviors, and general API flow.
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */
public class TestIdentityService {

    /**
     * The credential we will use for a valid login
     */
    private static final String credential = "TestUser";

    /**
     * The principal that we will use for a valid login
     */
    private static final String principal = "TestUser";

    /**
     * The provider name we are bootstrapping (test provider)
     */
    private static final String ProviderName = "DummyProvider";

    /**
     * The provider context
     */
    private Context context;

    /**
     * The provider identity service for the context
     */
    private IdentityService identity;

    /**
     * Verify that if we pass an invalid principal/credential the request fails. The DummyIdentityService blindly
     * accepts any request where the user and password are the same.
     */
    @Test
    public void failedLoginAttempt() {
        try {
            identity.authenticate(principal, "badCredentials");
            fail("supposed to get AuthenticationException");
        } catch (AuthenticationException e) {
            // success
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            fail("supposed to get AuthenticationException");
        } catch (ZoneException e) {
            e.printStackTrace();
            fail("supposed to get AuthenticationException");
        }
    }

    /**
     * Verify that if we pass a valid principal/credential the request succeeds. The DummyIdentityService blindly
     * accepts any request where the user and password are the same.
     */
    @Test
    public void goodLoginAttempt() {
        try {
            identity.authenticate(principal, credential);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            fail("supposed to succeed");
        } catch (ZoneException e) {
            e.printStackTrace();
            fail("supposed to succeed");
        }
    }

    /**
     * Setup the environment prior to each test
     */
    @Before
    public void setup() {
        try {
            context = ContextFactory.getContext(ProviderName, null);
            assertNotNull(context);
            assertTrue(context instanceof DummyProviderContext);

            identity = context.getIdentityService();
            assertNotNull(identity);
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
     * Verify that if we pass a bad credential the request fails
     */
    @Test
    public void testInvalidCredential() {
        try {
            identity.authenticate(principal, null);
            fail("supposed to get IllegalArgumentException");
        } catch (ZoneException e) {
            e.printStackTrace();
            fail("supposed to get IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }

        try {
            identity.authenticate(principal, "  ");
            fail("supposed to get IllegalArgumentException");
        } catch (ZoneException e) {
            e.printStackTrace();
            fail("supposed to get IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * Verify that if we pass a bad principal the request fails
     */
    @Test
    public void testInvalidPrincipal() {
        try {
            identity.authenticate(null, credential);
            fail("supposed to get IllegalArgumentException");
        } catch (ZoneException e) {
            e.printStackTrace();
            fail("supposed to get IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }

        try {
            identity.authenticate("   ", credential);
            fail("supposed to get IllegalArgumentException");
        } catch (ZoneException e) {
            e.printStackTrace();
            fail("supposed to get IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * Test the ability to retrieve the set of roles for the current user
     */
    @Test
    public void testRoles() {
        try {
            identity.authenticate(principal, credential);
            List<String> roles = identity.getRoles();
            assertNotNull(roles);
            assertFalse(roles.isEmpty());
            assertEquals(2, roles.size());
            assertTrue(roles.contains("DummyRole1"));
            assertTrue(roles.contains("DummyRole2"));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            fail("supposed to succeed");
        } catch (ZoneException e) {
            e.printStackTrace();
            fail("supposed to succeed");
        }
    }
}
