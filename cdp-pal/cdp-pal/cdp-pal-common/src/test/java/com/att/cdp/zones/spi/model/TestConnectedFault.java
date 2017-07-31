/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.zones.Context;

public class TestConnectedFault extends AbstractConnectedTests {

    /**
     * Verify the object is not null
     */
    @Test
    public void testConnectedFault() {
        ConnectedFault fault = new ConnectedFault(context);
        assertNotNull(fault);

    }

}
