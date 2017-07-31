/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @since Jun 15, 2015
 * @version $Id$
 */

public class TestProperty {

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VALUE = "value";
    public static final String[] PROPERTY_LIST = {
        "one", "two", "three"
    };
    public static final String[][] PROPERTY_MAP = {
        {
            "one", "1"
        }, {
            "two", "2"
        }, {
            "three", "3"
        }
    };

    private Property property;

    /**
     * Set up a test parameter
     */
    @Before
    public void initialize() {
        property = new Property(PROPERTY_NAME, PROPERTY_VALUE);
    }

    /**
     * Verify that the test parameter initialized correctly
     */
    @Test
    public void testConstruction() {
        assertEquals(PROPERTY_NAME, property.getName());
        assertEquals(PROPERTY_VALUE, property.getValue());
        assertFalse(property.isList());
        assertFalse(property.isMap());
    }

    /**
     * Verify that we can create a property with a value that is a list
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void testListValue() {
        List<String> temp = new ArrayList<String>();
        for (String listItem : PROPERTY_LIST) {
            temp.add(listItem);
        }
        property = new Property(PROPERTY_NAME, temp);

        assertTrue(property.isList());
        assertFalse(property.isMap());
        List values = (List) property.getValue();
        assertNotNull(values);
        assertFalse(values.isEmpty());

        for (String listItem : PROPERTY_LIST) {
            assertTrue(values.contains(listItem));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMapValue() {
        Map<String, String> temp = new HashMap<String, String>();
        for (String[] mapItem : PROPERTY_MAP) {
            temp.put(mapItem[0], mapItem[1]);
        }

        property = new Property(PROPERTY_NAME, temp);

        assertFalse(property.isList());
        assertTrue(property.isMap());
        Map<String, String> values = (Map<String, String>) property.getValue();
        assertNotNull(values);
        assertFalse(values.isEmpty());

        for (String[] mapItem : PROPERTY_MAP) {
            assertTrue(values.containsKey(mapItem[0]));
            assertEquals(mapItem[1], values.get(mapItem[0]));
        }
    }

    /**
     * Verify that two parameters constructed from the same values are equal
     */
    @Test
    public void testEqual() {
        Property other = new Property(PROPERTY_NAME, PROPERTY_VALUE);

        assertTrue(other.equals(property));
    }

    /**
     * Verify that two parameters constructed from different values are unequal
     */
    @Test
    public void testNotEqual() {
        Property other = new Property(PROPERTY_NAME, "A different value");
        assertFalse(other.equals(property));

        other = new Property("Fred", PROPERTY_VALUE);
        assertFalse(other.equals(property));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = property.toString();
        assertNotNull(string);
        assertTrue(string.contains(Property.class.getSimpleName()));
        assertTrue(string.contains(PROPERTY_NAME));
    }

    /**
     * Test that we can clone the constraint
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        Property clone = (Property) property.clone();

        assertTrue(clone.equals(property));
        assertFalse(clone == property);

        clone.setValue("A new value");
        assertFalse(clone.equals(property));
    }

    /**
     * Tests the hashcode method
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testHashCode() throws CloneNotSupportedException {
        Property clone = (Property) property.clone();

        assertFalse(0 == property.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(property.hashCode() == clone.hashCode());

        clone.setValue("Another different value");
        assertFalse(property.hashCode() == clone.hashCode());
    }
}
