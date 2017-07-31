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

public class TestPatternConstraint {

    private PatternConstraint constraint;
    private static final Scalar description = new Scalar("Test range constraint");
    private static final Scalar PATTERN = new Scalar(".+");

    /**
     * Set up a test value constraint
     */
    @Before
    public void initialize() {
        constraint = new PatternConstraint();
        constraint.setPattern(PATTERN);
        constraint.setDescription(description);
    }

    /**
     * Verify that the test constraint initialized correctly
     */
    @Test
    public void testConstruction() {
        assertEquals(description, constraint.getDescription());
        assertEquals(PATTERN, constraint.getPattern());
    }

    /**
     * Verify that two constraints constructed from the same values are equal
     */
    @Test
    public void testEqual() {
        PatternConstraint other = new PatternConstraint();
        other.setDescription(description);
        other.setPattern(PATTERN);

        assertTrue(other.equals(constraint));
    }

    /**
     * Verify that two constraints constructed from different values are unequal
     */
    @Test
    public void testNotEqual() {
        PatternConstraint other = new PatternConstraint();
        other.setDescription(new Scalar("An inequal constraint"));
        other.setPattern(new Scalar("[abc]{1,4}"));

        assertFalse(other.equals(constraint));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = constraint.toString();
        assertNotNull(string);
        assertTrue(string.contains(PatternConstraint.class.getSimpleName()));
        assertTrue(string.contains(PATTERN));
    }

    /**
     * Test that we can clone the constraint
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        PatternConstraint clone = (PatternConstraint) constraint.clone();

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
        PatternConstraint clone = (PatternConstraint) constraint.clone();

        assertFalse(0 == constraint.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(constraint.hashCode() == clone.hashCode());

        clone.setDescription(new Scalar("Another different description"));
        assertFalse(constraint.hashCode() == clone.hashCode());
    }
}
