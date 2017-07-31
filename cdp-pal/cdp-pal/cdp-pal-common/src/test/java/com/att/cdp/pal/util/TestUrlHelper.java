/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.junit.Test;

/**
 * @since Sep 28, 2016
 * @version $Id$
 */

public class TestUrlHelper {

    /**
     * Test that we can parse a URL string
     */
    @Test
    public void testParseValidURL() {
        UrlHelper helper = new UrlHelper();

        assertTrue(helper.parse("http://www.google.com:80/some/path?name=value&name2=value2"));
        assertEquals("http", helper.getProtocol());
        assertTrue(helper.isProtocolKnown());
        assertEquals("www.google.com", helper.getHost());
        assertEquals(80, helper.getPort());
        assertTrue(helper.isPortValid());
        assertTrue(helper.isPortWellKnown());
        String[] path = helper.getPath();
        assertNotNull(path);
        assertEquals(2, path.length);
        assertEquals("some", path[0]);
        assertEquals("path", path[1]);
        Map query = helper.getQuery();
        assertNotNull(query);
        assertTrue(query.containsKey("name"));
        assertEquals("value", query.get("name"));
        assertTrue(query.containsKey("name2"));
        assertEquals("value2", query.get("name2"));
        assertEquals("http://www.google.com:80", helper.hostOnly());
        String urlString = helper.asString();
        assertNotNull(urlString);
        assertEquals(58, urlString.length());

        assertTrue(helper.parse("http://www.google.com/"));
        assertTrue(helper.parse("http://1.2.3.4"));
        assertTrue(helper.parse("http://1.2.3.4:1234"));
    }

    /**
     * Test that we can parse a url string, manipulate it, and then obtain it as a URL object
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testAsURL() throws MalformedURLException {
        URL url = new UrlHelper("http://www.google.com:8080/path?name=value").asURL();

        assertNotNull(url);
        assertEquals("http", url.getProtocol());
        assertEquals("www.google.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("/path", url.getPath());
        assertEquals("name=value", url.getQuery());
    }
}
