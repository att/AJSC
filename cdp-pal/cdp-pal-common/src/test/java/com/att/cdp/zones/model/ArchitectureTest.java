/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArchitectureTest {

    @Test
    public void test() {

        for (Architecture arch : Architecture.values())
            assertEquals(Architecture.valueOf(arch.toString()), arch);
    }

}
