/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.map;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.att.cdp.exceptions.ConversionException;

/**
 * This is a general-purpose object mapper that can copy (or map) the contents of one object to another object of a
 * totally different class.
 * <p>
 * This mapper uses "best effort" to perform the mapping. If a mapping cannot be performed for a specific field, that
 * field is skipped and the remainder of the mappings are performed. It will never throw an exception to the caller. The
 * object returned may be incomplete if the mappings fail. This may be desirable in that any mappings that cannot be
 * performed automatically can then be performed by the caller, thus reducing the effort to handling just the special
 * cases.
 * </p>
 * <p>
 * This mapper operates on two object references provided to it. A source object that we are copying data from, and a
 * destination object that we are copying the data to. The operations are performed on a property by property basis.
 * That is, if a property can be directly accessed as a field, then the field is accessed to obtain or set the value. If
 * there are mutator and accessor methods provided that conform to the JavaBeans specification, then they are used if
 * the field is not accessible.
 * </p>
 * <p>
 * There are two methods provided to perform the mappings. One uses property name correspondence (meaning properties
 * that have the same names in both objects) are copied. This is the equivalent of the Cobol "copyCorresponding" verb,
 * and will automatically determine the properties that are to be copied.
 * </p>
 * <p>
 * The other method is a dictionary-based copy operation. The caller must provide a dictionary of the name mappings
 * where each dictionary entry contains the name of the property in the source, and the corresponding property name in
 * the destination objects
 * </p>
 * 
 * @since Sep 25, 2013
 * @version $Id$
 */

public final class ObjectMapper {

    /**
     * If set to true, then mapping errors do not fail silently. The result is an error message written to stderr, but
     * the mapping does not throw any exceptions and will still perform best effort mapping.
     */
    private static boolean debug = false;

    /**
     * This method is called whenever a mapping is failed because of an exception while trying to locate a specific
     * field on a specific class, either an accessor or mutator
     * 
     * @param clazz
     *            The class of the object we are manipulating
     * @param name
     *            The name of the property
     * @param ex
     *            The exception that was caught
     */
    private static void failMapping(Class<?> clazz, String name, Exception ex) {
        if (debug) {
            System.err.printf("Mapping failed for object of class %s and property %s because of "
                + "exception %s with message \"%s\"\n", clazz.getName(), name, ex.getClass().getName(), ex.getMessage());
        }
    }

    /**
     * This method is called whenever a mapping is failed because of some logical error condition detected while trying
     * to locate a specific field on a specific class of an object, either an accessor or mutator
     * 
     * @param clazz
     *            The class of the object we are manipulating
     * @param name
     *            The name of the property
     * @param message
     *            the reason that the mapping is being failed
     */
    private static void failMapping(Class<?> clazz, String name, String message) {
        if (debug) {
            System.err.printf("Mapping failed for object of class %s and property %s because \"%s\"\n",
                clazz.getName(), name, message);
        }
    }

    /**
     * This method is called whenever a mapping is failed because of an exception while trying to locate a specific
     * field on a specific object, either an accessor or mutator
     * 
     * @param obj
     *            The object we are manipulating
     * @param name
     *            The name of the property
     * @param ex
     *            The exception that was caught
     */
    private static void failMapping(Object obj, String name, Exception ex) {
        failMapping(obj.getClass(), name, ex);
    }

    /**
     * This method is called whenever a mapping is failed because of some logical error condition detected while trying
     * to locate a specific field on a specific object, either an accessor or mutator
     * 
     * @param obj
     *            The object we are manipulating
     * @param name
     *            The name of the property
     * @param message
     *            the reason that the mapping is being failed
     */
    private static void failMapping(Object obj, String name, String message) {
        failMapping(obj.getClass(), name, message);
    }

    /**
     * This method returns all fields that are defined in the object (as well as its parents) and adds them to the
     * supplied map
     * 
     * @param clazz
     *            The Class (and its hierarchy) to be examined
     * @param map
     *            The map of field names to the declaring classes for all fields that can be either directly accessed or
     *            where an accessor or mutator is provided.
     * @return The map of accessible properties.
     */
    private static Map<String, Class<?>> findAllProperties(Class<?> clazz, Map<String, Class<?>> map) {
        if ("java.lang.Object".equals(clazz.getName())) {
            return map;
        }

        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();

        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> type = field.getType();
            if (Modifier.isPublic(field.getModifiers())) {
                map.put(fieldName, type);
            } else {
                String accessorName1 = "get" + firstUpper(fieldName);
                String accessorName2 = "is" + firstUpper(fieldName);
                String mutatorName = "set" + firstUpper(fieldName);
                for (Method method : methods) {
                    if (Modifier.isPublic(method.getModifiers())) {
                        String methodName = method.getName();
                        if (methodName.equals(accessorName1) || methodName.equals(accessorName2)
                            || methodName.equals(mutatorName)) {
                            map.put(fieldName, type);
                            break;
                        }
                    }
                }
            }
        }

        // Now, repeat for our super class
        return findAllProperties(clazz.getSuperclass(), map);
    }

    /**
     * This method returns all fields that are defined in the object (as well as its parents)
     * 
     * @param obj
     *            The Object (and its hierarchy) to be examined
     * @return The map of accessible properties.
     */
    private static Map<String, Class<?>> findAllProperties(Object obj) {
        HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
        if (obj == null) {
            return map;
        }
        Class<?> clazz = obj.getClass();
        return findAllProperties(clazz, map);
    }

    /**
     * Finds and returns the Field of the given class, or any of it's super classes, that has the specified name.
     * 
     * @param clazz
     *            The class (and hierarchy) to search
     * @param name
     *            The name of the field to find
     * @return The field, or null if not found
     */
    private static Field findField(Class<?> clazz, String name) {
        if ("java.lang.Object".equals(clazz.getName())) {
            return null;
        }
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return findField(clazz.getSuperclass(), name);
        } catch (SecurityException e) {
            failMapping(clazz, name, e);
        }

        failMapping(clazz, name, "Field not found");
        return null;
    }

    /**
     * Finds and returns a method of the given name within the class, or any of it's super classes.
     * 
     * @param clazz
     *            The class (and hierarchy) to search
     * @param name
     *            The name of the method to find
     * @param arguments
     *            The class array of the argument list to match to the method signature. If no method matches exactly,
     *            then the argument list is examined to see if the arguments can be converted to a type that does match.
     *            The first method that is found that can accept all of the arguments (with possible conversion) is
     *            returned.
     * @return The method that was desired
     */
    private static Method findMethod(Class<?> clazz, String name, Class<?>[] arguments) {
        if ("java.lang.Object".equals(clazz.getName())) {
            return null;
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == arguments.length) {
                    for (int index = 0; index < params.length; index++) {
                        if (!Converter.isLegal(arguments[index], params[index])) {
                            break;
                        }
                    }

                    return method;
                }
            }
        }
        return findMethod(clazz.getSuperclass(), name, arguments);

    }

    /**
     * Returns the name with the first (or only) character converted to upper case
     * 
     * @param name
     *            The name to be converted
     * @return The name, with the first character upper case
     */
    private static String firstUpper(String name) {
        if (name == null) {
            return null;
        }

        if (name.length() == 1) {
            return name.toUpperCase();
        }

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Get the value from the specified object and return it
     * 
     * @param source
     *            The source object
     * @param name
     *            The name of the property to return
     * @return The value of the property, expressed as an Object (type conversion may be needed)
     */
    private static Object getValue(Object source, String name) {
        Class<?> clazz = source.getClass();
        Field field = findField(clazz, name);
        if (field != null && Modifier.isPublic(field.getModifiers())) {
            try {
                return field.get(source);
            } catch (IllegalArgumentException e) {
                failMapping(source, name, e);
            } catch (IllegalAccessException e) {
                failMapping(source, name, e);
            }
        }

        // Either the field wasn't found, or it was not accessible. See if we can find an accessor method
        String methodName = "get" + firstUpper(name);
        Method method = findMethod(clazz, methodName, new Class[] {});
        if (method == null) {
            methodName = "is" + firstUpper(name);
            method = findMethod(clazz, methodName, new Class[] {});
        }

        if (method != null && Modifier.isPublic(method.getModifiers())) {
            try {
                return method.invoke(source, new Object[] {});
            } catch (IllegalAccessException e) {
                failMapping(source, name, e);
            } catch (IllegalArgumentException e) {
                failMapping(source, name, e);
            } catch (InvocationTargetException e) {
                failMapping(source, name, e);
            }
        }

        failMapping(source, name, "accessor field/method not found");
        return null;
    }

    /**
     * This mapping method is the equivalent to a "copyCorresponding" operation, in that it automatically copies all
     * properties that have the same name (and are of compatible types) from the source object to the destination
     * object.
     * 
     * @param source
     *            The source object
     * @param dest
     *            The destination object
     * @return The destination object after mapping is completed
     */
    public static Object map(Object source, Object dest) {
        HashMap<String, String> dictionary = new HashMap<String, String>();
        Map<String, Class<?>> sourceFields = findAllProperties(source);
        Map<String, Class<?>> destFields = findAllProperties(dest);

        for (Map.Entry<String, Class<?>> entry : sourceFields.entrySet()) {
            String fieldName = entry.getKey();
            if (destFields.containsKey(fieldName)) {

                Class<?> sourceClass = entry.getValue();
                Class<?> destClass = destFields.get(fieldName);
                if (destClass.isAssignableFrom(sourceClass)) {
                    dictionary.put(fieldName, fieldName);
                }
            }
        }

        if (!dictionary.isEmpty()) {
            return map(source, dest, dictionary);
        }

        return dest;
    }

    /**
     * This method maps the contents of the <code>source</code> object to the <code>dest</code> object using the field
     * (or accessor/mutator) names specified in the dictionary.
     * 
     * @param source
     *            The object to map the contents from
     * @param dest
     *            The object to map the contents to
     * @param dictionary
     *            The name mapping dictionary. The key of the dictionary is the field name in the "from" object. The
     *            value of the dictionary is the "to" field name
     * @return The mapped object
     */
    public static Object map(Object source, Object dest, Map<String, String> dictionary) {

        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            mapFields(source, dest, entry.getKey(), entry.getValue());
        }

        return dest;
    }

    /**
     * Maps a specific field from the "source" object to the "destination" object.
     * 
     * @param source
     *            The source object
     * @param dest
     *            The destination object
     * @param fromField
     *            The field name of the source
     * @param toField
     *            The field name of the target
     */
    private static void mapFields(Object source, Object dest, String fromField, String toField) {
        Object value = getValue(source, fromField);
        setValue(dest, toField, value);
    }

    /**
     * Sets the static debug flag true to cause error messages to be generated if a mapping fails for any reason.
     * 
     * @param flag
     *            True causes an error message to be written to stderr in the event of a failed mapping. False causes
     *            the mapping to fail silently.
     */
    public static void setDebug(boolean flag) {
        debug = flag;
    }

    /**
     * Set the value to the specified object using the specified property from the value (expressed as a String).
     * 
     * @param dest
     *            The destination object
     * @param name
     *            The name of the property
     * @param value
     *            The value to set into the property (type conversion may be needed).
     */
    private static void setValue(Object dest, String name, Object value) {
        Class<?> clazz = dest.getClass();
        Field field = findField(clazz, name);
        if (field != null && Modifier.isPublic(field.getModifiers())) {
            try {
                field.set(dest, value);
                return;
            } catch (IllegalArgumentException e) {
                failMapping(dest, name, e);
            } catch (IllegalAccessException e) {
                failMapping(dest, name, e);
            }
        }

        // Either the field wasn't found, or it was not accessible. See if we can find a mutator method that can accept
        // an object that is compatible with the value class type
        String methodName = "set" + firstUpper(name);
        Class<?>[] argList =
            new Class<?>[] { value == null ? (field == null ? Object.class : field.getType()) : value.getClass() };
        Method method = findMethod(clazz, methodName, argList);
        if (method != null) {
            Class<?> argClass = method.getParameterTypes()[0];
            if (Modifier.isPublic(method.getModifiers())) {
                try {
                    method.invoke(dest, new Object[] { Converter.convert(argClass, value) });
                    return;
                } catch (IllegalAccessException e) {
                    failMapping(dest, name, e);
                } catch (IllegalArgumentException e) {
                    failMapping(dest, name, e);
                } catch (InvocationTargetException e) {
                    failMapping(dest, name, e);
                } catch (ConversionException e) {
                    failMapping(dest, name, e);
                }
            }
        }

        failMapping(dest, name, "mutator field/method not found");
    }

    /**
     * Private default constructor prevents instantiation
     */
    private ObjectMapper() {
    }
}
