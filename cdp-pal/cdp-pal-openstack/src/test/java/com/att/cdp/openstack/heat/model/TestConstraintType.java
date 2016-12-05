/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @since Jun 15, 2015
 * @version $Id$
 */

public class TestConstraintType {

    /**
     * Test the constraint type enumeration to recognize the correct constraint types
     */
    @Test
    public void testIsOneOf() {
        assertTrue(ConstraintType.isOneOf("length"));
        assertTrue(ConstraintType.isOneOf("range"));
        assertTrue(ConstraintType.isOneOf("allowed_values"));
        assertTrue(ConstraintType.isOneOf("allowed_pattern"));
        assertTrue(ConstraintType.isOneOf("custom_constraint"));

        assertFalse(ConstraintType.isOneOf("test"));
        assertFalse(ConstraintType.isOneOf("false"));
        assertFalse(ConstraintType.isOneOf("dummy"));
    }

    /**
     * Test that we can retrieve the proper type code from the enumeration
     */
    @Test
    public void testGetType() {
        assertEquals("length", ConstraintType.LENGTH.getType());
        assertEquals("range", ConstraintType.RANGE.getType());
        assertEquals("allowed_values", ConstraintType.ALLOWED_VALUES.getType());
        assertEquals("allowed_pattern", ConstraintType.ALLOWED_PATTERN.getType());
        assertEquals("custom_constraint", ConstraintType.CUSTOM_CONSTRAINT.getType());
    }

    /**
     * Test that we can get the value of a constraint type and assign that to the correct enumerated value.
     */
    public void testSetType() {
        assertEquals(ConstraintType.LENGTH, ConstraintType.valueOf("length"));
        assertEquals(ConstraintType.RANGE, ConstraintType.valueOf("range"));
        assertEquals(ConstraintType.ALLOWED_VALUES, ConstraintType.valueOf("allowed_values"));
        assertEquals(ConstraintType.ALLOWED_PATTERN, ConstraintType.valueOf("allowed_pattern"));
        assertEquals(ConstraintType.CUSTOM_CONSTRAINT, ConstraintType.valueOf("custom_constraint"));
    }
}
