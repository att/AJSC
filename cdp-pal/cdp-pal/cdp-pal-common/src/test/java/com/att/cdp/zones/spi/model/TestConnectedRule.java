/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.zones.Context;

/**
 * This test case is designed to test the ConnectedRule class.
 * 
 * @since Feb 27, 2014
 * @version $Id$
 */
public class TestConnectedRule extends AbstractConnectedTests {
    /**
     * Ensure that we can create a ConnectedRule and configure it
     */
    @Test
    public void testCreateConnectedRule() {
        ConnectedRule rule = new ConnectedRule(context);
        Integer num = new Integer(5);
        rule.setId("id");
        rule.setName("name");
        rule.setFromPort(num);
        rule.setToPort(num);
        assertNotNull(rule);
        assertEquals("id", rule.getId());
        assertEquals("name", rule.getName());
        assertEquals(num, rule.getFromPort());
        assertEquals(num, rule.getToPort());
    }
}
