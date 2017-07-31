/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * This class is used to locate and access bean properties in an object graph.
 * <p>
 * Bean properties may be direct access to the fields, or they may be provided through the use of accessor and mutator
 * (getter and setter) methods. Additionally, this class supports the specification of Jackson annotations on the bean
 * to determine what the actual accessor and mutator should be, so that the bean property can vary from the mapping name
 * in the document.
 * </p>
 * 
 * @since Jun 9, 2015
 * @version $Id$
 */
public final class BeanProperty {

    /**
     * This is a factory method used to obtain a bean property that represents the specified property on the specified
     * bean, if a property exists by that name.
     * 
     * @param obj
     *            The bean to be inspected
     * @param name
     *            The name of the property desired
     * @return The BeanProperty that wraps that property, or null if no property exists.
     */
    public static BeanProperty getBeanProperty(Object obj, String name) {

        Class<?> clazz = obj.getClass();
        Field field = ObjectHelper.getJsonPropertyField(clazz, name);
        Method accessor = null;

        if (field == null) {
            field = ObjectHelper.getPropertyField(clazz, name);
            if (field == null) {
                accessor = ObjectHelper.getPropertyAccessor(clazz, name);
            }
        }

        BeanProperty beanProperty = null;
        if (field != null) {
            beanProperty = new BeanProperty(name, obj, field);
        } else if (accessor != null) {
            beanProperty = new BeanProperty(name, obj, accessor);
        }

        return beanProperty;
    }

    /**
     * The accessor method for indirect access, or null
     */
    private Method accessor;

    /**
     * The bean that we are introspecting to support properties
     */
    private Object bean;

    /**
     * The field for direct access, or null
     */
    private Field field;

    /**
     * The name of the property we are managing on the bean
     */
    private String propertyName;

    /**
     * Construct the bean property for the specified bean and property name
     * 
     * @param propertyName
     *            The name of the property
     * @param bean
     *            The bean containing the property
     */
    private BeanProperty(String propertyName, Object bean) {
        this.propertyName = propertyName;
        this.bean = bean;
        bean.getClass();
    }

    /**
     * Construct the bean property for the specified field
     * 
     * @param bean
     *            The bean we are inspecting and supporting
     * @param field
     *            The field that represents this property on the bean
     */
    private BeanProperty(String propertyName, Object bean, Field field) {
        this(propertyName, bean);
        this.field = field;
        this.field.setAccessible(true);
    }

    /**
     * Creates the bean property for the specified accessor (and implied mutator).
     * 
     * @param bean
     *            The bean we are inspecting and supporting
     * @param accessor
     *            The accessor method used to return the property.
     */
    private BeanProperty(String propertyName, Object bean, Method accessor) {
        this(propertyName, bean);
        this.accessor = accessor;
        this.accessor.setAccessible(true);
    }

    /**
     * Obtains and returns the value of the property to the caller
     * 
     * @return The property value
     * @throws IllegalArgumentException
     *             if the method is an instance method and the specified object argument is not an instance of the class
     *             or interface declaring the underlying method (or of a subclass or implementor thereof); if the number
     *             of actual and formal parameters differ; if an unwrapping conversion for primitive arguments fails; or
     *             if, after possible unwrapping, a parameter value cannot be converted to the corresponding formal
     *             parameter type by a method invocation conversion.
     * @throws IllegalAccessException
     *             if this Method object is enforcing Java language access control and the underlying method is
     *             inaccessible.
     * @throws InvocationTargetException
     *             if the underlying method throws an exception.
     */
    public Object get() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (field != null) {
            field.setAccessible(true);
            return field.get(bean);
        }
        return ObjectHelper.callAccessor(bean, accessor);
    }

    /**
     * Sets the property value
     * 
     * @param value
     *            The value to set in the property
     * @throws IllegalArgumentException
     *             if the method is an instance method and the specified object argument is not an instance of the class
     *             or interface declaring the underlying method (or of a subclass or implementor thereof); if the number
     *             of actual and formal parameters differ; if an unwrapping conversion for primitive arguments fails; or
     *             if, after possible unwrapping, a parameter value cannot be converted to the corresponding formal
     *             parameter type by a method invocation conversion.
     * @throws IllegalAccessException
     *             if this Method object is enforcing Java language access control and the underlying method is
     *             inaccessible.
     * @throws InvocationTargetException
     *             if the underlying method throws an exception.
     */
    public void set(Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (field != null) {
            ObjectHelper.setFieldValue(bean, field, value);
        } else {
            Method mutator = ObjectHelper.getPropertyMutator(bean, propertyName, value == null ? null : value);
            mutator.setAccessible(true);
            ObjectHelper.callMutator(bean, mutator, value);
        }
    }

    /**
     * Returns the type of the property. This can be a single value type (scalar) such as String, or it can be a
     * multi-value type such as a collection.
     * 
     * @return The class of the data type of the property.
     */
    public Class<?> getPropertyType() {
        if (field != null) {
            return field.getType();
        }
        return accessor.getReturnType();
    }

    /**
     * @return The name of the property
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return True if the property specified is a map, otherwise false
     */
    public boolean isMap() {
        Class<?> propertyType = getPropertyType();
        return Map.class.isAssignableFrom(propertyType);
    }

    /**
     * @return True if the property specified is a List, otherwise false
     */
    public boolean isList() {
        Class<?> propertyType = getPropertyType();
        return List.class.isAssignableFrom(propertyType);
    }

    /**
     * @return The generic types of the collection (list or map)
     */
    public Class<?>[] getCollectionTypes() {
        Class<?>[] types = null;
        if (field != null) {
            types = ObjectHelper.getCollectionTypes(field);
        } else {
            types = ObjectHelper.getCollectionTypes(accessor);
        }

        return types;
    }
}
