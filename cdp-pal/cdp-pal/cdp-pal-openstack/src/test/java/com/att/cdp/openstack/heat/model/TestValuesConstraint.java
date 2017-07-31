/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @since Jun 15, 2015
 * @version $Id$
 */

public class TestValuesConstraint {

    private ValuesConstraint constraint;
    private static final List<Scalar> values = new ArrayList<Scalar>();
    private static final Scalar description = new Scalar("Test value constraint");

    static {
        values.add(new Scalar("One"));
        values.add(new Scalar("two"));
        values.add(new Scalar("three"));
        values.add(new Scalar("four"));
        values.add(new Scalar("five"));
    }

    /**
     * Set up a test value constraint
     */
    @Before
    public void initialize() {
        constraint = new ValuesConstraint();
        constraint.setValues(values);
        constraint.setDescription(description);
    }

    /**
     * Verify that the test constraint initialized correctly
     */
    @Test
    public void testConstruction() {
        assertEquals(description, constraint.getDescription());
        assertEquals(values, constraint.getValues());
    }

    /**
     * Verify that two constraints constructed from the same values are equal
     */
    @Test
    public void testEqual() {
        ValuesConstraint other = new ValuesConstraint();
        other.setDescription(description);
        other.setValues(values);

        assertTrue(other.equals(constraint));
    }

    /**
     * Verify that two constraints constructed from different values are unequal
     */
    @Test
    public void testNotEqual() {
        ValuesConstraint other = new ValuesConstraint();
        other.setDescription(new Scalar("An inequal constraint"));
        other.setValues(null);

        assertFalse(other.equals(constraint));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = constraint.toString();
        assertNotNull(string);
        assertTrue(string.contains(ValuesConstraint.class.getSimpleName()));

        for (Scalar value : values) {
            assertTrue(string.contains(value));
        }
    }

    /**
     * Test that we can clone the constraint
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        ValuesConstraint clone = (ValuesConstraint) constraint.clone();

        assertTrue(clone.equals(constraint));
        assertFalse(clone == constraint);

        clone.setDescription(new Scalar("A new description"));
        assertFalse(clone.equals(constraint));
    }

    /**
     * Tests the hashcode method
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testHashCode() throws CloneNotSupportedException {
        ValuesConstraint clone = (ValuesConstraint) constraint.clone();

        assertFalse(0 == constraint.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(constraint.hashCode() == clone.hashCode());

        clone.setDescription(new Scalar("Another different description"));
        assertFalse(constraint.hashCode() == clone.hashCode());
    }
}
