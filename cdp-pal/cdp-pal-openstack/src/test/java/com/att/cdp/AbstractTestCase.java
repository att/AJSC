/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.pal.util.ResourceHelper;
import com.att.cdp.pal.util.StreamUtility;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ContextFactory;
/**
 * This is a helper class which can be used as the base class of any test case and which loads a configuration
 * properties object for testing.
 *
 * @since Jan 20, 2015
 * @version $Id$
 */

public class AbstractTestCase {
	
    private Properties testConfig;
    private Map<String, String> templates;

    /**
     * (Re)Initializes the test environment with the properties object used to configure the tests
     * 
     * @throws IOException
     *             If the resources can't be read
     */
    @Before
    public void loadProperties() throws IOException {
        testConfig = new Properties();
        ClassLoader cl = getClass().getClassLoader();
        
        String integrationtest = System.getProperty("mock", "com/att/cdp/test.properties");
     	try (InputStream stream = cl.getResourceAsStream(integrationtest)) {
            if (stream != null) {
                testConfig.load(stream);
            }
        }

        if (testConfig.containsKey("http.proxyHost")) {
            System.setProperty("http.proxyHost", testConfig.getProperty("http.proxyHost"));
            System.setProperty("http.proxyPort", testConfig.getProperty("http.proxyPort"));
        }

        templates = new HashMap<String, String>();
        URL[] urls = ResourceHelper.findResources(this.getClass(), "com/att/cdp/templates/.+\\.yml");
        if (urls != null) {
            for (URL url : urls) {
                try (InputStream stream = url.openStream()) {
                    String path = url.getPath();
                    Pattern pattern = Pattern.compile("([^\\/]+)$");
                    Matcher matcher = pattern.matcher(path);
                    if (matcher.find()) {
                        String name = matcher.group(1);
                        String content = StreamUtility.getStringFromInputStream(stream);
                        templates.put(name, content);
                    }
                }
            }
        }
    }

    /**
     * Obtains the provider identity url, tenant name, region (if any), userid, and password from the test case
     * properties and uses them to connect to the specified provider. Returns the connected context object.
     * 
     * @return The context object
     * @throws ZoneException
     *             If anything fails
     */
    public Context connect() throws ZoneException {
        Properties properties = new Properties();
        properties.put(ContextFactory.PROPERTY_IDENTITY_URL, testConfig.getProperty("provider.url"));
        properties.put(ContextFactory.PROPERTY_TENANT, testConfig.getProperty("provider.tenant"));
        if (testConfig.getProperty("provider.region") != null) {
            properties.put(ContextFactory.PROPERTY_REGION, testConfig.getProperty("provider.region"));
        }

        OpenStackContext context =
            (OpenStackContext) ContextFactory.getContext(testConfig.getProperty("provider.name"), properties);

        context.login(testConfig.getProperty("provider.user"), testConfig.getProperty("provider.password"));
        return context;
    }

    /**
     * Get the properties used to configure the test
     * 
     * @return The test properties
     */
    protected Properties getProperties() {
        return testConfig;
    }

    /**
     * A helper method to allow test cases that need an active, actual logged in connection to a provider
     * 
     * @return The context
     * @throws ZoneException
     *             If the login fails
     */
    protected OpenStackContext login() throws ZoneException {
        OpenStackContext context = (OpenStackContext) ContextFactory.getContext("OpenStackProvider", getProperties());

        assertFalse(context.isLoggedIn());
        context.login(getProperties().getProperty(ContextFactory.PROPERTY_USERID),
            getProperties().getProperty(ContextFactory.PROPERTY_PASSWORD));

        assertTrue(context.isLoggedIn());
        return context;
    }

    /**
     * A helper method to allow a test case to disconnect from the provider
     * 
     * @param context
     *            The context to be logged out of
     */
    protected void logout(OpenStackContext context) {
        context.logout();
        assertFalse(context.isLoggedIn());
    }
}
