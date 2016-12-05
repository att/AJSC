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

public class TestLengthConstraint {

    private LengthConstraint constraint;
    private static final Scalar description = new Scalar("Test length constraint");
    private static final Scalar MINIMUM = new Scalar("1");
    private static final Scalar MAXIMUM = new Scalar("4");

    /**
     * Set up a test value constraint
     */
    @Before
    public void initialize() {
        constraint = new LengthConstraint();
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
        LengthConstraint other = new LengthConstraint();
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
        LengthConstraint other = new LengthConstraint();
        other.setDescription(new Scalar("An inequal constraint"));
        other.setMin(MINIMUM);
        other.setMax(MAXIMUM);

        assertFalse(other.equals(constraint));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = constraint.toString();
        assertNotNull(string);
        assertTrue(string.contains(LengthConstraint.class.getSimpleName()));
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
        LengthConstraint clone = (LengthConstraint) constraint.clone();

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
        LengthConstraint clone = (LengthConstraint) constraint.clone();

        assertFalse(0 == constraint.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(constraint.hashCode() == clone.hashCode());

        clone.setDescription(new Scalar("Another different description"));
        assertFalse(constraint.hashCode() == clone.hashCode());
    }
}
