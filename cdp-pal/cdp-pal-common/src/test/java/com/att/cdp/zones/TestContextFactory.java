/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.zones.test.DummyProviderContext;

/**
 * @since Sep 23, 2013
 * @version $Id$
 */
public class TestContextFactory {

    private static final String ProviderName = "DummyProvider";

    /**
     * This test ensures that we can create multiple contexts from the same provider
     */
    @Test
    public void testMultipleContexts() {
        try {
            Context context1 = ContextFactory.getContext(ProviderName, null);
            assertNotNull(context1);
            assertTrue(context1 instanceof DummyProviderContext);

            Provider provider = context1.getProvider();
            assertNotNull(provider);
            assertEquals(ProviderName, provider.getName());

            Context context2 = ContextFactory.getContext(ProviderName, null);
            assertNotNull(context2);
            assertFalse(context1 == context2);
            assertFalse(context1.equals(context2));
        } catch (NoProviderFoundException e) {
            fail("We were supposed to find the TestProvider!");
        }
    }

    /**
     * This test ensures that if we ask for a provider that does not exist we will get the appropriate exception
     */
    @Test
    public void testNoProviderFound() {
        try {
            ContextFactory.getContext("ABadProviderNameThatWeKnowWontBeFound", null);
            fail("No context was supposed to be found!");
        } catch (NoProviderFoundException e) {
            // no-op
        }
    }

    /**
     * Test that we can list provider types installed
     * 
     * @throws NoProviderFoundException
     */
    @Test
    public void testListProviderTypes() throws NoProviderFoundException {
        List<String> providers = ContextFactory.getProviders();
        assertNotNull(providers);
        assertFalse(providers.contains("ABadProviderNameThatWeKnowWontBeFound"));
        assertTrue(providers.contains(ProviderName));
    }

    /**
     * Validate that the provider is initially not logged in
     */
    @Test
    public void testNotLoggedIn() {
        try {
            Context context = ContextFactory.getContext(ProviderName, null);
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
     * This test ensures that we can load the test provider and that we get a valid context object from it.
     */
    @Test
    public void testTestProvider() {
        try {
            Context context = ContextFactory.getContext(ProviderName, null);
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
     * Tests that we can use a properties object to configure the context.
     */
    @Test
    public void testWithPropertiesConfiguration() {
        String dummyPropertyName = "dummyPropertyName";
        String dummyPropertyValue = "dummyPropertyValue";

        try {
            Properties properties = new Properties();
            properties.setProperty(dummyPropertyName, dummyPropertyValue);

            Context context = ContextFactory.getContext(ProviderName, properties);
            assertNotNull(context);
            assertTrue(context instanceof DummyProviderContext);
            assertNotNull(context.getProperties());
            assertTrue(context.getProperties().containsKey(dummyPropertyName));
            assertEquals(dummyPropertyValue, context.getProperties().getProperty(dummyPropertyName));

        } catch (NoProviderFoundException e) {
            fail("We were supposed to find the TestProvider!");
        }
    }
}
