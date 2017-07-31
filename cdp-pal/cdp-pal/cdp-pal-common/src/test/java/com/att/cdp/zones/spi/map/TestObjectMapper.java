/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.map;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * This test case tests the ObjectMapper facility.
 * 
 * @since Sep 25, 2013
 * @version $Id$
 */
public class TestObjectMapper {

    /**
     * This is a much more complex object that we want to map data from. It uses only accessors and mutators to get and
     * set the properties.
     * 
     * @since Sep 25, 2013
     * @version $Id$
     */
    @SuppressWarnings("javadoc")
    public class ComplexFrom extends SimpleFrom {
        private String string = "a string";
        private ArrayList<String> strings = new ArrayList<String>();

        public ComplexFrom() {
            strings.add("String 1");
            strings.add("String 2");
            strings.add("String 3");
        }

        public String getString() {
            return string;
        }

        public ArrayList<String> getStrings() {
            return strings;
        }
    }

    /**
     * This class is the counter part to the complexFrom class.
     * 
     * @since Sep 25, 2013
     * @version $Id$
     */
    @SuppressWarnings("javadoc")
    public class ComplexTo extends SimpleTo {
        private String string;
        private ArrayList<String> strings;

        public String getString() {
            return string;
        }

        public ArrayList<String> getStrings() {
            return strings;
        }

        public void setString(String toString) {
            this.string = toString;
        }

        public void setStrings(ArrayList<String> strings) {
            this.strings = strings;
        }
    }

    /**
     * This class represents a simple "data object" type of object that may need to be mapped. There are many different
     * types of data fields to be mapped.
     * 
     * @since Sep 25, 2013
     * @version $Id$
     */
    @SuppressWarnings("javadoc")
    public class SimpleFrom {
        public char[] charArray = { 'a', 'b', 'c', 'd' };
        public char charValue = 'a';
        public double[] doubleArray = { 5.1, 5.2, 5.3 };
        public double doubleValue = 5.4321;
        public float[] floatArray = { 4.1f, 4.2f, 4.3f };
        public float floatValue = 4.123f;
        public int[] intArray = { 2, 22, 222 };
        public int intValue = 2;
        public long[] longArray = { 3, 33, 333 };
        public long longValue = 3;
        private String parentString = "from string";
        public short[] shortArray = { 1, 11, 111 };

        public short shortValue = 1;

        public String getParentString() {
            return parentString;
        }
    }

    /**
     * This class is the counterpart to the "SimpleFrom" class and allows us to test copying fields directly because
     * they are accessible and also allows us to test the support for primitives and arrays of primitives.
     * 
     * @since Sep 25, 2013
     * @version $Id$
     */
    @SuppressWarnings("javadoc")
    public class SimpleTo {
        public char[] charArray;
        public char charValue;
        public double[] doubleArray;
        public double doubleValue;
        public float[] floatArray;
        public float floatValue;
        public int[] intArray;
        public int intValue;
        public long[] longArray;
        public long longValue;
        private String parentString;
        public short[] shortArray;

        public short shortValue;

        public String getParentString() {
            return parentString;
        }

        public void setParentString(String value) {
            parentString = value;
        }
    }

    /**
     * This test is used to see if the copy corresponding functionality is working correctly in the simple cases
     */
    @Test
    public void testComplexCorrespondingMapping() {
        ComplexFrom from = new ComplexFrom();
        ComplexTo to = new ComplexTo();

        ObjectMapper.setDebug(true);
        ObjectMapper.map(from, to);

        assertEquals(from.charValue, to.charValue);
        assertEquals(from.shortValue, to.shortValue);
        assertEquals(from.intValue, to.intValue);
        assertEquals(from.longValue, to.longValue);
        assertEquals(from.floatValue, to.floatValue, 0.1);
        assertEquals(from.doubleValue, to.doubleValue, 0.1);
        assertEquals(from.charArray, to.charArray);
        assertEquals(from.shortArray, to.shortArray);
        assertEquals(from.intArray, to.intArray);
        assertEquals(from.longArray, to.longArray);
        assertEquals(from.floatArray, to.floatArray);
        assertEquals(from.doubleArray, to.doubleArray);

        assertEquals(from.getString(), to.getString());
        assertEquals(from.getStrings(), to.getStrings());
        assertEquals(from.getParentString(), to.getParentString());
    }

    /**
     * This test is more complex in that it does everything using the accessor and mutator methods, and it needs to walk
     * up the class hierarchy to perform the copy of the parent values.
     */
    @Test
    public void testComplexMapping1() {
        ComplexFrom from = new ComplexFrom();
        ComplexTo to = new ComplexTo();

        HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put("string", "string");
        dictionary.put("strings", "strings");
        dictionary.put("parentString", "parentString");

        ObjectMapper.setDebug(true);
        ObjectMapper.map(from, to, dictionary);

        assertEquals(from.getString(), to.getString());
        assertEquals(from.getStrings(), to.getStrings());
        assertEquals(from.getParentString(), to.getParentString());
    }

    /**
     * This test is used to see if the copy corresponding functionality is working correctly in the simple cases
     */
    @Test
    public void testSimpleCorrespondingMapping() {
        SimpleFrom from = new SimpleFrom();
        SimpleTo to = new SimpleTo();

        ObjectMapper.setDebug(true);
        ObjectMapper.map(from, to);

        assertEquals(from.charValue, to.charValue);
        assertEquals(from.shortValue, to.shortValue);
        assertEquals(from.intValue, to.intValue);
        assertEquals(from.longValue, to.longValue);
        assertEquals(from.floatValue, to.floatValue, 0.1);
        assertEquals(from.doubleValue, to.doubleValue, 0.1);
        assertEquals(from.charArray, to.charArray);
        assertEquals(from.shortArray, to.shortArray);
        assertEquals(from.intArray, to.intArray);
        assertEquals(from.longArray, to.longArray);
        assertEquals(from.floatArray, to.floatArray);
        assertEquals(from.doubleArray, to.doubleArray);
    }

    /**
     * This test is a simple mapping test for all of the primitive and arrays of primitives fields of compatible types.
     * Note, we are not doing any testing of type conversion in this test.
     */
    @Test
    public void testSimpleMapping() {

        SimpleFrom from = new SimpleFrom();
        SimpleTo to = new SimpleTo();
        HashMap<String, String> dictionary = new HashMap<String, String>();
        dictionary.put("charValue", "charValue");
        dictionary.put("shortValue", "shortValue");
        dictionary.put("intValue", "intValue");
        dictionary.put("longValue", "longValue");
        dictionary.put("floatValue", "floatValue");
        dictionary.put("doubleValue", "doubleValue");
        dictionary.put("charArray", "charArray");
        dictionary.put("shortArray", "shortArray");
        dictionary.put("intArray", "intArray");
        dictionary.put("longArray", "longArray");
        dictionary.put("floatArray", "floatArray");
        dictionary.put("doubleArray", "doubleArray");

        ObjectMapper.setDebug(true);
        ObjectMapper.map(from, to, dictionary);

        assertEquals(from.charValue, to.charValue);
        assertEquals(from.shortValue, to.shortValue);
        assertEquals(from.intValue, to.intValue);
        assertEquals(from.longValue, to.longValue);
        assertEquals(from.floatValue, to.floatValue, 0.1);
        assertEquals(from.doubleValue, to.doubleValue, 0.1);
        assertEquals(from.charArray, to.charArray);
        assertEquals(from.shortArray, to.shortArray);
        assertEquals(from.intArray, to.intArray);
        assertEquals(from.longArray, to.longArray);
        assertEquals(from.floatArray, to.floatArray);
        assertEquals(from.doubleArray, to.doubleArray);
    }
}
