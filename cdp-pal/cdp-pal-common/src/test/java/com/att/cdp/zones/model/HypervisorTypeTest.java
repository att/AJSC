/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HypervisorTypeTest {

    @Test
    public void test() {
        for (HypervisorType hyt : HypervisorType.values())
            assertEquals(HypervisorType.valueOf(hyt.toString()), hyt);
    }

}
