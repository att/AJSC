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

public class TestParameter {

    private Parameter parameter;
    private static final Scalar DESCRIPTION = new Scalar("Test parameter");
    private static final Scalar LABEL = new Scalar("custom");
    private static final Scalar TYPE = new Scalar("string");
    private static final Scalar HIDDEN = new Scalar("false");
    private static final Scalar DEFAULT_VALUE = new Scalar("default");

    private static final List<Constraint> CONSTRAINTS = new ArrayList<Constraint>();

    static {
        CONSTRAINTS.add(new LengthConstraint(new Scalar("1"), new Scalar("4"), new Scalar("Length constraint")));
        CONSTRAINTS.add(new RangeConstraint(new Scalar("1"), new Scalar("9999"), new Scalar("Range constraint")));
    }

    /**
     * Set up a test parameter
     */
    @Before
    public void initialize() {
        parameter = new Parameter();
        parameter.setLabel(LABEL);
        parameter.setType(TYPE);
        parameter.setHidden(HIDDEN);
        parameter.setDefaultValue(DEFAULT_VALUE);
        parameter.setDescription(DESCRIPTION);
        parameter.setConstraints(CONSTRAINTS);
    }

    /**
     * Verify that the test parameter initialized correctly
     */
    @Test
    public void testConstruction() {
        assertEquals(DESCRIPTION, parameter.getDescription());
        assertEquals(LABEL, parameter.getLabel());
        assertEquals(TYPE, parameter.getType());
        assertEquals(HIDDEN, parameter.getHidden());
        assertEquals(DEFAULT_VALUE, parameter.getDefaultValue());
        assertNotNull(parameter.getConstraints());
        assertEquals(CONSTRAINTS, parameter.getConstraints());
    }

    /**
     * Verify that two parameters constructed from the same values are equal
     */
    @Test
    public void testEqual() {
        Parameter other = new Parameter();
        other.setLabel(LABEL);
        other.setType(TYPE);
        other.setHidden(HIDDEN);
        other.setDefaultValue(DEFAULT_VALUE);
        other.setDescription(DESCRIPTION);
        other.setConstraints(CONSTRAINTS);

        assertTrue(other.equals(parameter));
    }

    /**
     * Verify that two parameters constructed from different values are unequal
     */
    @Test
    public void testNotEqual() {
        Parameter other = new Parameter();
        other.setLabel(new Scalar("A different Label"));    // Label is only difference
        other.setType(TYPE);
        other.setHidden(HIDDEN);
        other.setDefaultValue(DEFAULT_VALUE);
        other.setDescription(DESCRIPTION);
        other.setConstraints(CONSTRAINTS);

        assertFalse(other.equals(parameter));

        other = new Parameter();
        other.setLabel(LABEL);
        other.setType(new Scalar("float"));                 // Type is only difference
        other.setHidden(HIDDEN);
        other.setDefaultValue(DEFAULT_VALUE);
        other.setDescription(DESCRIPTION);
        other.setConstraints(CONSTRAINTS);

        assertFalse(other.equals(parameter));

        other = new Parameter();
        other.setLabel(LABEL);
        other.setType(TYPE);
        other.setHidden(new Scalar("true"));                // Hidden is only difference
        other.setDefaultValue(DEFAULT_VALUE);
        other.setDescription(DESCRIPTION);
        other.setConstraints(CONSTRAINTS);

        assertFalse(other.equals(parameter));

        other = new Parameter();
        other.setLabel(LABEL);
        other.setType(TYPE);
        other.setHidden(HIDDEN);
        other.setDefaultValue(new Scalar("123"));           // Default value is only difference
        other.setDescription(DESCRIPTION);
        other.setConstraints(CONSTRAINTS);

        assertFalse(other.equals(parameter));

        other = new Parameter();
        other.setLabel(LABEL);
        other.setType(TYPE);
        other.setHidden(HIDDEN);
        other.setDefaultValue(DEFAULT_VALUE);
        other.setDescription(new Scalar("A different description"));
        other.setConstraints(CONSTRAINTS);

        assertFalse(other.equals(parameter));

        List<Constraint> otherConstraints = new ArrayList<Constraint>();
        otherConstraints.add(new RangeConstraint(new Scalar("100"), new Scalar("200"), new Scalar("Range constraint")));
        other = new Parameter();
        other.setLabel(LABEL);
        other.setType(TYPE);
        other.setHidden(HIDDEN);
        other.setDefaultValue(DEFAULT_VALUE);
        other.setDescription(DESCRIPTION);
        other.setConstraints(otherConstraints);

        assertFalse(other.equals(parameter));
    }

    /**
     * Verify that constraint toString returns the proper string
     */
    @Test
    public void testToString() {
        String string = parameter.toString();
        assertNotNull(string);
        assertTrue(string.contains(Parameter.class.getSimpleName()));
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
        Parameter clone = (Parameter) parameter.clone();

        assertTrue(clone.equals(parameter));
        assertFalse(clone == parameter);

        clone.setDescription(new Scalar("A new description"));
        assertFalse(clone.equals(parameter));
    }

    /**
     * Tests the hashcode method
     * 
     * @throws CloneNotSupportedException
     *             if the clone fails
     */
    @Test
    public void testHashCode() throws CloneNotSupportedException {
        Parameter clone = (Parameter) parameter.clone();

        assertFalse(0 == parameter.hashCode());
        assertFalse(0 == clone.hashCode());

        assertTrue(parameter.hashCode() == clone.hashCode());

        clone.setDescription(new Scalar("Another different description"));
        assertFalse(parameter.hashCode() == clone.hashCode());
    }
}
