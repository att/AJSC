/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

/**
 * @since Jun 11, 2015
 * @version $Id$
 */

public enum ConstraintType {
    /**
     * A Length constraint
     */
    LENGTH("length"),

    /**
     * A range constraint
     */
    RANGE("range"),

    /**
     * An allowed values list constraint
     */
    ALLOWED_VALUES("allowed_values"),

    /**
     * An allowed pattern constraint
     */
    ALLOWED_PATTERN("allowed_pattern"),

    /**
     * A custom constraint
     */
    CUSTOM_CONSTRAINT("custom_constraint");

    /**
     * The template constraint type
     */
    private String type;

    /**
     * Construct the customized enumeration
     * 
     * @param type
     *            The heat template constraint type
     */
    private ConstraintType(String type) {
        this.type = type;
    }

    /**
     * @return The actual internal Heat template type name of a constraint
     */
    public String getType() {
        return type;
    }

    /**
     * Determines if the supplied name is an enumerated name or a heat template constraint type name of one of the
     * enumerated values.
     * 
     * @param value
     *            The value to be checked
     * @return True if the name is equal to one of the defined enumerations, either as the enumeration name or the heat
     *         template constraint type
     */
    public static boolean isOneOf(String value) {
        for (ConstraintType ct : ConstraintType.values()) {
            if (ct.getType().equals(value) || ct.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
