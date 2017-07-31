/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class contains methods to assist in the manipulation or support of classes and objects via reflection
 * 
 * @since Jun 19, 2015
 * @version $Id$
 */

public final class ObjectHelper {

    /**
     * Private default constructor prevents instantiation
     */
    private ObjectHelper() {

    }

    /**
     * Checks to see if a JsonIgnoreProperty annotation is present on the specified class and the supplied property is
     * ignored (either explicitly or implicitly if ignoreUnknown=true).
     * 
     * @param cls
     *            The class to be checked
     * @param property
     *            The name of the property to be checked
     * @return True if the named property is ignored, or if the blanket specification ignoreUnknown=true is set.
     */
    // public static boolean isJsonIgnoreUnknownProperty(Class<?> cls, String property) {
    // boolean result = false;
    // JsonIgnoreProperties annotation = getAnnotation(cls, JsonIgnoreProperties.class);
    // if (annotation != null) {
    // String[] values = annotation.value();
    // if (values == null || values.length == 0) {
    // result = annotation.ignoreUnknown();
    // } else {
    // for (String value : values) {
    // if (value.equals(property)) {
    // result = true;
    // break;
    // }
    // }
    // }
    // }
    //
    // return result;
    // }

    /**
     * This method scans the class and super-class hierarchy (up to, but not including java.lang.Object) for the
     * specified annotation class. If found, the annotation is returned. If not found, null is returned.
     * 
     * @param <T>
     *            The type of annotation desired
     * @param clazz
     *            The class to be scanned, including its hierarchy
     * @param annotationClass
     *            The class of the annotation desired.
     * @return The annotation if found, null if not found.
     */
    private static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        if (clazz.getName().equals(Object.class.getName())) {
            return null;
        }
        T annotation = clazz.getAnnotation((Class<T>) annotationClass);
        if (annotation == null) {
            annotation = getAnnotation(clazz.getSuperclass(), annotationClass);
        }

        return annotation;
    }

    /**
     * This method checks to see if two objects are equal and returns true if they are, and false if they are not.
     * <p>
     * Equality as defined by this method means that the two objects must both either be null (null == null), or they
     * must be both non-null and of the same type. If they are both non-null, additionally all fields of the various
     * types, as well as all superclass fields (recursively up to java.lang.Object), must either be null or must be
     * equal. If any of these conditions are not true, the objects are assumed to be unequal. If all of these conditions
     * are true, the objects are equal.
     * </p>
     * 
     * @param o1
     *            The first object to be compared
     * @param o2
     *            The second object to be compared
     * @return True if they are equal, false if not
     */
    public static boolean equals(Object o1, Object o2) {
        boolean result = false;

        if (o1 == null && o2 == null) {
            result = true;
        } else if (o1 != null && o2 != null) {
            Class<?> o1Class = o1.getClass();
            Class<?> o2Class = o2.getClass();
            if (o1Class.equals(o2Class)) {
                result = true;  // assume true until we find otherwise
                while (result && !o1Class.equals(Object.class)) {
                    Field[] fields = o1Class.getDeclaredFields();
                    for (Field field : fields) {
                        result = result && checkEqualField(field.getName(), o1, o2);
                        if (!result) {
                            break;
                        }
                    }
                    o1Class = o1Class.getSuperclass();
                }
            }
        }

        return result;
    }

    /**
     * This method is called to check if two fields are equal on two objects of the same class. This can be used to test
     * for equality in an "equals" method where checking for null or non-null conditions must be accounted for.
     * 
     * @param name
     *            The name of the field
     * @param obj1
     *            The first object instance
     * @param obj2
     *            The second object instance
     * @return True if both objects are non-null and the field either exists in both objects and are equal, or both
     *         fields are null. False under any other condition.
     */
    public static boolean checkEqualField(String name, Object obj1, Object obj2) {
        boolean result = false;
        if (obj1 != null && obj2 != null) {
            try {
                Field field = getPropertyField(obj1.getClass(), name); // obj1.getClass().getDeclaredField(name);
                if (field != null) {
                    field.setAccessible(true);
                    try {
                        Object value1 = field.get(obj1);
                        Object value2 = field.get(obj2);
                        if (value1 != null && value2 != null) {
                            result = value1.equals(value2);
                        } else if (value1 == null && value2 == null) {
                            result = true;
                        }
                    } finally {
                        field.setAccessible(false);
                    }
                }
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (obj1 == null && obj2 == null) {
            result = true;
        }
        return result;
    }

    /**
     * This method returns an array of the generic types defined for the specified collection field. If the field is not
     * a collection, the return array is empty.
     * <p>
     * This method returns the generic types associated with a type-safe collection. For example, a field defined as
     * <code>Map&lt;String, Object&gt;</code> will return an array of <code>[java.lang.String, java.lang.Object]</code>.
     * </p>
     * 
     * @param field
     *            The field to be examined
     * @return An array of types (classes) that define the declared generic types for the collection. If the collection
     *         is a list, the array will contain one element with the class of the objects in the list. If the
     *         collection is a map, the array will contain two elements, the first being the type of object stored as
     *         the key, and the second the type of object stored as the value.
     */
    @SuppressWarnings("rawtypes")
    public static Class[] getCollectionTypes(Field field) {
        Class[] result = new Class[] {};
        if (field != null) {
            if (isMap(field) || isList(field)) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                if (genericType != null) {
                    Type[] types = genericType.getActualTypeArguments();
                    result = new Class[types.length];
                    for (int index = 0; index < types.length; index++) {
                        Type type = types[index];
                        result[index] = (Class) type;
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method returns an array of the generic types defined as the return type of the specified accessor method. If
     * the return type is not a collection, the returned array is empty.
     * <p>
     * This method returns the generic types associated with a type-safe collection. For example, a method defined as
     * returning <code>Map&lt;String, Object&gt;</code> will return an array of
     * <code>[java.lang.String, java.lang.Object]</code>.
     * </p>
     * 
     * @param accessor
     *            The accessor method to be examined
     * @return An array of types (classes) that define the declared generic types for the collection. If the collection
     *         is a list, the array will contain one element with the class of the objects in the list. If the
     *         collection is a map, the array will contain two elements, the first being the type of object stored as
     *         the key, and the second the type of object stored as the value.
     */
    @SuppressWarnings("rawtypes")
    public static Class[] getCollectionTypes(Method accessor) {
        Class[] result = new Class[] {};
        if (accessor != null) {
            if (isMap(accessor) || isList(accessor)) {
                ParameterizedType genericType = (ParameterizedType) accessor.getGenericReturnType();
                if (genericType != null) {
                    Type[] types = genericType.getActualTypeArguments();
                    result = new Class[types.length];
                    for (int index = 0; index < types.length; index++) {
                        Type type = types[index];
                        result[index] = (Class) type;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param field
     *            The field to be checked for the data type
     * @return True if the field is a map, false otherwise
     */
    public static boolean isMap(Field field) {
        if (field == null) {
            return false;
        }
        return Map.class.isAssignableFrom(field.getType());
    }

    /**
     * @param accessor
     *            The accessor method that is used to access the property
     * @return True if the return data from the accessor is a map, false otherwise
     */
    public static boolean isMap(Method accessor) {
        if (accessor == null || accessor.getReturnType().equals(Void.class)) {
            return false;
        }
        return Map.class.isAssignableFrom(accessor.getReturnType());
    }

    /**
     * @param field
     *            The field to be checked for the data type
     * @return True if the field is a list, false otherwise
     */
    public static boolean isList(Field field) {
        if (field == null) {
            return false;
        }
        return List.class.isAssignableFrom(field.getType());
    }

    /**
     * @param accessor
     *            The accessor method that is used to access the property
     * @return True if the return data from the accessor is a list, false otherwise
     */
    public static boolean isList(Method accessor) {
        if (accessor == null || accessor.getReturnType().equals(Void.class)) {
            return false;
        }
        return List.class.isAssignableFrom(accessor.getReturnType());
    }

    /**
     * This method calls the accessor to get a value of a property.
     * 
     * @param bean
     *            The object that contains the property to be accessed.
     * @param method
     *            The accessor method to be called
     * @return the value of the property
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
    public static Object callAccessor(Object bean, Method method) throws IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {

        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length == 0) {
            Object[] args = new Object[] {};
            return method.invoke(bean, args);
        } else {
            throw new IllegalArgumentException(String.format("Illegal accessor method %s, it takes one or more "
                + "arguments!", method.getName()));
        }
    }

    /**
     * This method is used to call a mutator (setter) method on a bean to change the value of a property.
     * 
     * @param bean
     *            the Bean that contains the property to be changed.
     * @param mutator
     *            The mutator method used to change the value
     * @param value
     *            The new value to set
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
    public static void callMutator(Object bean, Method mutator, Object value) throws IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {
        Class<?>[] parameters = mutator.getParameterTypes();
        if (parameters.length == 1) {
            Class<?> targetType = parameters[0];
            Class<?> sourceType = value.getClass();

            if (targetType.isAssignableFrom(sourceType)) {
                mutator.setAccessible(true);
                Object[] args = new Object[] {
                    value
                };
                mutator.invoke(bean, args);
            } else {
                throw new IllegalArgumentException(String.format("Attempt to set property by invoking method %s(%s) "
                    + "using a value of type %s, the value cannot be assigned to the parameter type.",
                    mutator.getName(), targetType.getName(), sourceType.getName()));
            }
        } else {
            throw new IllegalArgumentException(String.format("Illegal mutator method %s, it takes more than one "
                + "argument!", mutator.getName()));
        }
    }

    /**
     * This method is used to set the value of a field of a model object to the specified value.
     * 
     * @param bean
     *            The bean object to be manipulated
     * @param field
     *            The field definition in the model object to be set
     * @param value
     *            The value to be stored in that field.
     * @throws IllegalArgumentException
     *             if the method is an instance method and the specified object argument is not an instance of the class
     *             or interface declaring the underlying method (or of a subclass or implementor thereof); if the number
     *             of actual and formal parameters differ; if an unwrapping conversion for primitive arguments fails; or
     *             if, after possible unwrapping, a parameter value cannot be converted to the corresponding formal
     *             parameter type by a method invocation conversion.
     * @throws IllegalAccessException
     *             if this Method object is enforcing Java language access control and the underlying method is
     *             inaccessible.
     */
    public static void setFieldValue(Object bean, Field field, Object value) throws IllegalArgumentException,
        IllegalAccessException {
        Class<?> targetType = field.getType();
        Class<?> sourceType = value.getClass();

        if (targetType.isAssignableFrom(sourceType)) {
            field.setAccessible(true);
            field.set(bean, value);
        } else {
            throw new IllegalArgumentException(String.format("Attempt to set property %s of type %s from a "
                + "value of type %s, types are not assignable.", field.getName(), targetType.getName(),
                sourceType.getName()));
        }
    }

    /**
     * Searches the specified class for the specified annotation and returns the annotation if it exists, or null if not
     * found. This method will search the class hierarchy up to java.lang.Object (exclusive) looking for the annotation.
     * 
     * @param clazz
     *            The class to be searched
     * @param annotationClass
     *            The annotation to be found
     * @return The annotation object if found, null if not found
     */
    public static Annotation getClassAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Annotation annotation = null;
        if (clazz != null && annotationClass != null) {
            Class<?> cls = clazz;
            while (!Object.class.equals(cls)) {
                annotation = cls.getAnnotation(annotationClass);
                if (annotation != null) {
                    break;
                }
                cls = cls.getSuperclass();
            }
        }
        return annotation;
    }

    /**
     * Searches a class hierarchy for any fields that are annotated with the JsonProperty annotation that specifies the
     * provided property name.
     * 
     * @param clazz
     *            The leaf class of the hierarchy to be searched
     * @param name
     *            The name of the property desired
     * @return The field annotated with this property if it exists, or null if no field was found
     */
    // public static Field getJsonPropertyField(Class<?> clazz, String name) {
    // Field[] fields = clazz.getDeclaredFields();
    // Field propertyField = null;
    //
    // for (Field field : fields) {
    // JsonProperty annotation = field.getAnnotation(JsonProperty.class);
    // if (annotation != null && annotation.value().equals(name)) {
    // propertyField = field;
    // break;
    // }
    // }
    //
    // if (propertyField == null) {
    // Class<?> sc = clazz.getSuperclass();
    // if (!Object.class.getName().equals(sc.getName())) {
    // propertyField = getJsonPropertyField(sc, name);
    // }
    // }
    //
    // return propertyField;
    // }

    /**
     * This method returns the field associated with the JSon property of the specified name
     * 
     * @param obj
     *            The object on which we are looking for an annotation
     * @param name
     *            The property name we are looking for
     * @return The field associated with that property, or null if the json property annotation does not exist
     */
    public static Field getJsonPropertyField(Object obj, String name) {
        Class<?> clazz = obj.getClass();
        return getJsonPropertyField(clazz, name);
    }

    /**
     * Returns the accessor method (getter)
     * 
     * @param clazz
     *            The class we are searching
     * @param name
     *            The name of the property. the accessor method name will be "get" + name, where the first letter of
     *            name will be upper case.
     * @return The method
     */
    public static Method getPropertyAccessor(Class<?> clazz, String name) {
        return _getPropertyAccessor(clazz, getAccessorMethodName(name));
    }

    /**
     * This method returns the method that corresponds to the property but supplies the ability to access (get) the
     * property value.
     * 
     * @param obj
     *            The object to be searched
     * @param name
     *            The name of the property. the accessor method name will be "get" + name, where the first letter of
     *            name will be upper case.
     * @return The method to call to access the property value, or null if none exists.
     */
    public static Method getPropertyAccessor(Object obj, String name) {
        Class<?> clazz = obj.getClass();
        return _getPropertyAccessor(clazz, getAccessorMethodName(name));
    }

    /**
     * Returns the accessor method (getter)
     * 
     * @param clazz
     *            The class we are searching
     * @param methodName
     *            The accessor (getter) name we are looking for
     * @return The method
     */
    private static Method _getPropertyAccessor(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        Method accessor = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == 0) {
                accessor = method;
                break;
            }
        }
        if (accessor == null) {
            Class<?> sc = clazz.getSuperclass();
            if (!Object.class.getName().equals(sc.getName())) {
                accessor = _getPropertyAccessor(sc, methodName);
            }
        }

        return accessor;
    }

    /**
     * Returns the name of the method to access the property
     * 
     * @param name
     *            The name of the property. the accessor method name will be "get" + name, where the first letter of
     *            name will be upper case.
     * @return The accessor method name
     */
    public static String getAccessorMethodName(String name) {
        StringBuffer buffer = new StringBuffer(name);
        buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
        buffer.insert(0, "get");
        return buffer.toString();
    }

    /**
     * Returns the name of the method to mutate (change) the property
     * 
     * @param name
     *            The name of the property. The mutator method name will be "set" + name, where the first letter of name
     *            will be upper case.
     * @return The mutator method name
     */
    public static String getMutatorMethodName(String name) {
        StringBuffer buffer = new StringBuffer(name);
        buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
        buffer.insert(0, "set");
        return buffer.toString();
    }

    /**
     * This method searches the class hierarchy for a field of the specified name
     * 
     * @param clazz
     *            The class to be searched, as well as its super class(es)
     * @param name
     *            The name of the field
     * @return The field if found, or null if not
     */
    public static Field getPropertyField(Class<?> clazz, String name) {
        Field result = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                result = field;
                break;
            }
        }
        if (result == null) {
            Class<?> sc = clazz.getSuperclass();
            if (Object.class.getName().equals(sc.getName())) {
                result = null;
            } else {
                result = getPropertyField(sc, name);
            }
        }

        return result;
    }

    /**
     * Returns the field that matches the provided property name, if found
     * 
     * @param obj
     *            The object to be searched
     * @param name
     *            The property name to be found
     * @return The field that corresponds to that property, or null if no field exists
     */
    public static Field getPropertyField(Object obj, String name) {
        Class<?> clazz = obj.getClass();
        return getPropertyField(clazz, name);
    }

    /**
     * Returns the mutator method (setter)
     * 
     * @param clazz
     *            The class to be searched for the mutator
     * @param name
     *            The name of the property. the accessor method name will be "get" + name, where the first letter of
     *            name will be upper case.
     * @param targetType
     *            The type of object to pass to the mutator. If null, uses the first mutator that it finds that accepts
     *            a single argument.
     * @return the mutator method if one is found, or null
     */
    public static Method getPropertyMutator(Class<?> clazz, String name, Class<?> targetType) {
        return _getPropertyMutator(clazz, getMutatorMethodName(name), targetType);
    }

    /**
     * This method returns the method that corresponds to the property but supplies the ability to access (get) the
     * property value.
     * 
     * @param obj
     *            The object to be searched
     * @param name
     *            The name of the property. the accessor method name will be "get" + name, where the first letter of
     *            name will be upper case.
     * @param value
     *            The object that contains the value to be set. This is needed so that we can locate the appropriate
     *            mutator method that takes an assignable type as its only argument.
     * @return The method to call to access the property value, or null if none exists.
     */
    public static Method getPropertyMutator(Object obj, String name, Object value) {
        Class<?> clazz = obj.getClass();
        return _getPropertyMutator(clazz, getMutatorMethodName(name), value.getClass());
    }

    /**
     * Returns the mutator method (setter)
     * 
     * @param clazz
     *            The class to be searched for the mutator
     * @param methodName
     *            The method name to look for
     * @param targetType
     *            The type of object to pass to the mutator. If null, uses the first mutator that it finds that accepts
     *            a single argument.
     * @return the mutator method if one is found, or null
     */
    private static Method _getPropertyMutator(Class<?> clazz, String methodName, Class<?> targetType) {
        Method[] methods = clazz.getDeclaredMethods();
        Method mutator = null;
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (method.getName().equals(methodName) && parameterTypes.length == 1) {
                if (targetType == null || parameterTypes[0].isAssignableFrom(targetType)) {
                    mutator = method;
                    break;
                }
            }
        }

        if (mutator == null) {
            Class<?> sc = clazz.getSuperclass();
            if (!Object.class.getName().equals(sc.getName())) {
                mutator = _getPropertyMutator(sc, methodName, targetType);
            }
        }

        return mutator;
    }

    /**
     * Checks to see if a JsonIgnoreProperty annotation is present on the specified class and the supplied property is
     * ignored (either explicitly or implicitly if ignoreUnknown=true).
     * 
     * @param cls
     *            The class to be checked
     * @param property
     *            The name of the property to be checked
     * @return True if the named property is ignored, or if the blanket specification ignoreUnknown=true is set.
     */
    public static boolean isJsonIgnoreUnknownProperty(Class<?> cls, String property) {
        boolean result = false;
        JsonIgnoreProperties annotation = getAnnotation(cls, JsonIgnoreProperties.class);
        if (annotation != null) {
            String[] values = annotation.value();
            if (values == null || values.length == 0) {
                result = annotation.ignoreUnknown();
            } else {
                for (String value : values) {
                    if (value.equals(property)) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

}
