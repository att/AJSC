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

public class TestCustomConstraint {

    private CustomConstraint constraint;
    private static final Scalar description = new Scalar("Test custom constraint");
    private static final Scalar NAME = new Scalar("custom");

    /**
     * Set up a test value constraint
     */
    @Before
    public void initialize() {
        constraint = new CustomConstraint();
        constraint.setName(NAME);
        constraint.setDescription(description);
    }

    /**
     * Verify that the test constraint initialized correctly
     */
    @Test
    public void testConstruction() {
        assertEquals(description, constraint.getDescription());
        assertEquals(NAME, constraint.getName());
    }

    /**
     * Verify that two constraints constructed from the same values are equal
     */
    @Test
    public void testEqual() {
        CustomConstraint other = new CustomConstraint();
        other.setDescription(description);
        other.setName(NAME);

        assertTrue(other.equals(constraint));
    }

    /**
     * Verify that two constraints constructed from different values are unequal
     */
    @Test
    public void testNotEqual() {
        CustomConstraint other = new CustomConstraint();
        other.setDescription(new Scalar("An inequal constraint"));
        other.setName(new Scalar("other"));

        assertFalse(other.equals(constraint));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = constraint.toString();
        assertNotNull(string);
        assertTrue(string.contains(CustomConstraint.class.getSimpleName()));
        assertTrue(string.contains(NAME));
    }

    /**
     * Test that we can clone the constraint
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        CustomConstraint clone = (CustomConstraint) constraint.clone();

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
        CustomConstraint clone = (CustomConstraint) constraint.clone();

        assertFalse(0 == constraint.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(constraint.hashCode() == clone.hashCode());

        clone.setDescription(new Scalar("Another different description"));
        assertFalse(constraint.hashCode() == clone.hashCode());
    }
}
