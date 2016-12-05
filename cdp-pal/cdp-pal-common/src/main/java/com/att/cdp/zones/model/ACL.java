/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.att.cdp.zones.Context;

/**
 * @since Oct 23, 2013
 * @version $Id$
 */

public class ACL extends ModelObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The description of this ACL rule
     */
    private String description;

    /**
     * The identification of this ACL
     */
    private String id;

    /**
     * The name of this ACL
     */
    private String name;

    /**
     * A list of the access control rules for this ACL
     */
    private List<Rule> rules = new ArrayList<>();

    /**
     * Default no-argument constructor
     */
    public ACL() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected ACL(Context context) {
        super(context);
    }

    /**
     * Creates an ACL prototype that specifies the name of an actual ACL list to be attached to a server.
     * 
     * @param name
     *            The ACL name
     */
    public ACL(String name) {
        this.name = name;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        ACL other = (ACL) obj;
        return id.equals(other.id);
    }

    /**
     * JavaBean accessor to obtain the value of description
     * 
     * @return the description value
     */
    public String getDescription() {
        return description;
    }

    /**
     * JavaBean accessor to obtain the value of id
     * 
     * @return the id value
     */
    public String getId() {
        return id;
    }

    /**
     * JavaBean accessor to obtain the value of name
     * 
     * @return the name value
     */
    public String getName() {
        return name;
    }

    /**
     * Return the set of all rules defined in this ACL
     * 
     * @return The unmodifiable set of rules
     */
    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Standard JavaBean mutator method to set the value of name
     * 
     * @param name
     *            the value to be set into name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param description
     *            the value for description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param rules
     *            the value for rules
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("ACL: id(%s), name(%s), description(%s), rules(%s)", id, name == null ? "" : name,
            description == null ? "" : description, rules.toString());
    }
}
