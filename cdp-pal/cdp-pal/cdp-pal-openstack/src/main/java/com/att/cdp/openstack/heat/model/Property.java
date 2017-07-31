/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.att.cdp.pal.util.ObjectHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class represents a property of some specific type.
 * <p>
 * Properties are a collection of name-value tuples, where the name is always a string, but the value can be one of
 * several types of objects. The value can also be a simple string, which is the trivial case (name-value are
 * string-string). Additionally, the value may be a complex construction of lists and/or mapped values, the sum of which
 * supplies the value of the property. An example of this might be a property where the value is an intrisic function.
 * </p>
 * 
 * @since Jan 29, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property extends ModelObject implements Cloneable {

    /**
     * The name of the property
     */
    private String name;

    /**
     * The value of the property
     */
    private Object value;

    /**
     * Default constructor to create an empty property object
     */
    public Property() {

    }

    /**
     * Constructor to create the property base class and initialize the property name
     * 
     * @param name
     *            The name of the property
     * @param value
     *            the value of the property
     */
    public Property(String name, Object value) {
        setName(name);
        setValue(value);
    }

    /**
     * @return True if the value of the property is a list of objects
     */
    public boolean isList() {
        return value instanceof List;
    }

    /**
     * @return True if the value is a map of values
     */
    public boolean isMap() {
        return value instanceof Map;
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
     * @return the value of value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     *            the value for value
     */
    public void setValue(Object value) {
        this.value = value;
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode()) + (value == null ? 0 : value.hashCode());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s [%s] = [%s]", getClass().getSimpleName(), name, value);
    }

    /**
     * @see com.att.cdp.openstack.heat.model.ModelObject#clone()
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Property clone = (Property) super.clone();
        if (value instanceof List) {
            List newList = new ArrayList();
            newList.addAll((List) value);
            clone.value = newList;
        } else if (value instanceof Map) {
            Map newMap = new HashMap();
            newMap.putAll((Map) value);
            clone.value = newMap;
        }
        return clone;
    }
}
