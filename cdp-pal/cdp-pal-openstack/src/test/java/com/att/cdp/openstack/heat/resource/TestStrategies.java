/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

/**
 * @since Feb 11, 2015
 * @version $Id$
 */

public class TestStrategies {

    /**
     * This test is used to verify that the resource strategy support for processing OpenStack stacks created from heat
     * templates works correctly.
     * 
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("nls")
    @Test
    public void testStrategyLoad() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
        IllegalAccessException {
        ResourceStrategy strategy = new ResourceStrategy();

        Class<ResourceStrategy> rsClass = ResourceStrategy.class;
        Field rsList = rsClass.getDeclaredField("strategies");
        assertNotNull(rsList);
        rsList.setAccessible(true);
        List<AbstractResourceStrategy> strategies = (List<AbstractResourceStrategy>) rsList.get(strategy);

        assertNotNull(strategies);
        assertFalse(strategies.isEmpty());
        
    }
}
