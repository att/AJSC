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

public class TestRangeConstraint {

    private RangeConstraint constraint;
    private static final Scalar description = new Scalar("Test range constraint");
    private static final Scalar MINIMUM = new Scalar("1");
    private static final Scalar MAXIMUM = new Scalar("100");

    /**
     * Set up a test value constraint
     */
    @Before
    public void initialize() {
        constraint = new RangeConstraint();
        constraint.setMin(MINIMUM);
        constraint.setMax(MAXIMUM);
        constraint.setDescription(description);
    }

    /**
     * Verify that the test constraint initialized correctly
     */
    @Test
    public void testConstruction() {
        assertEquals(description, constraint.getDescription());
        assertEquals(MINIMUM, constraint.getMin());
        assertEquals(MAXIMUM, constraint.getMax());
    }

    /**
     * Verify that two constraints constructed from the same values are equal
     */
    @Test
    public void testEqual() {
        RangeConstraint other = new RangeConstraint();
        other.setDescription(description);
        other.setMin(MINIMUM);
        other.setMax(MAXIMUM);

        assertTrue(other.equals(constraint));
    }

    /**
     * Verify that two constraints constructed from different values are unequal
     */
    @Test
    public void testNotEqual() {
        RangeConstraint other = new RangeConstraint();
        other.setDescription(new Scalar("An inequal constraint"));
        other.setMin(MINIMUM);
        other.setMax(new Scalar("99"));

        assertFalse(other.equals(constraint));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = constraint.toString();
        assertNotNull(string);
        assertTrue(string.contains(RangeConstraint.class.getSimpleName()));
        assertTrue(string.contains(MINIMUM));
        assertTrue(string.contains(MAXIMUM));
    }

    /**
     * Test that we can clone the constraint
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        RangeConstraint clone = (RangeConstraint) constraint.clone();

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
        RangeConstraint clone = (RangeConstraint) constraint.clone();

        assertFalse(0 == constraint.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(constraint.hashCode() == clone.hashCode());

        clone.setDescription(new Scalar("Another different description"));
        assertFalse(constraint.hashCode() == clone.hashCode());
    }
}
