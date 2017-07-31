/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OsTypeTest {

    @Test
    public void test() {
        for (OsType ost : OsType.values())
            assertEquals(OsType.valueOf(ost.toString()), ost);
    }

}
