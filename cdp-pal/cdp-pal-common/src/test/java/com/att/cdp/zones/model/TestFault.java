/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestFault {

    /**
     * Verify the Fault object is not null
     */
    @Test
    public void testFault() {
        Fault obj = new Fault();
        assertNotNull(obj);
    }

}
