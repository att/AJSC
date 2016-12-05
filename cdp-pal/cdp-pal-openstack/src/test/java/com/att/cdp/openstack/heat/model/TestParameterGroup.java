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
 * @since Jun 15, 2015
 * @version $Id$
 */

public class TestParameterGroup {

    private ParameterGroup group;
    private static final Scalar DESCRIPTION = new Scalar("Test parameter");
    private static final Scalar LABEL = new Scalar("custom");
    private static final Scalar TYPE = new Scalar("string");
    private static final Scalar HIDDEN = new Scalar("false");
    private static final Scalar DEFAULT_VALUE = new Scalar("default");

    private static final List<Scalar> parameters = new ArrayList<Scalar>();

    static {
        parameters.add(new Scalar("one"));
        parameters.add(new Scalar("two"));
        parameters.add(new Scalar("three"));
    }

    /**
     * Set up a test parameter
     */
    @Before
    public void initialize() {
        group = new ParameterGroup();
        group.setLabel(LABEL);
        group.setDescription(DESCRIPTION);
        group.setParameters(parameters);
    }

    /**
     * Verify that the test parameter initialized correctly
     */
    @Test
    public void testConstruction() {
        assertEquals(DESCRIPTION, group.getDescription());
        assertEquals(LABEL, group.getLabel());
        assertNotNull(group.getParameters());
        assertEquals(parameters, group.getParameters());
    }

    /**
     * Verify that two parameters constructed from the same values are equal
     */
    @Test
    public void testEqual() {
        ParameterGroup other = new ParameterGroup();
        other.setLabel(LABEL);
        other.setDescription(DESCRIPTION);
        other.setParameters(parameters);

        assertTrue(other.equals(group));
    }

    /**
     * Verify that two parameters constructed from different values are unequal
     */
    @Test
    public void testNotEqual() {
        ParameterGroup other = new ParameterGroup();
        other.setLabel(new Scalar("A different Label"));    // Label is only difference
        other.setDescription(DESCRIPTION);
        other.setParameters(parameters);

        assertFalse(other.equals(group));

        other = new ParameterGroup();
        other.setLabel(LABEL);
        other.setDescription(new Scalar("A different description"));
        other.setParameters(parameters);

        assertFalse(other.equals(group));

        List<Scalar> otherParameters = new ArrayList<Scalar>();
        otherParameters.add(new Scalar("ten"));
        otherParameters.add(new Scalar("eleven"));
        otherParameters.add(new Scalar("twelve"));
        other = new ParameterGroup();
        other.setLabel(LABEL);
        other.setDescription(DESCRIPTION);
        other.setParameters(otherParameters);

        assertFalse(other.equals(group));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = group.toString();
        assertNotNull(string);
        assertTrue(string.contains(ParameterGroup.class.getSimpleName()));
        assertTrue(string.contains(LABEL));
    }

    /**
     * Test that we can clone the constraint
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        ParameterGroup clone = (ParameterGroup) group.clone();

        assertTrue(clone.equals(group));
        assertFalse(clone == group);

        clone.setDescription(new Scalar("A new description"));
        assertFalse(clone.equals(group));
    }

    /**
     * Tests the hashcode method
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testHashCode() throws CloneNotSupportedException {
        ParameterGroup clone = (ParameterGroup) group.clone();

        assertFalse(0 == group.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(group.hashCode() == clone.hashCode());

        clone.setDescription(new Scalar("Another different description"));
        assertFalse(group.hashCode() == clone.hashCode());
    }
}
