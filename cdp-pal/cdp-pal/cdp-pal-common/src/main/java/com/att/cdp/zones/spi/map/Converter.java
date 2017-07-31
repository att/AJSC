/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.map;

import com.att.cdp.exceptions.ConversionException;

/**
 * This class provides simple data conversion routines to convert from common data types to other common data type. It
 * is intended to be used as part of the object mapping support. If the types passed to it are not one of the primitive,
 * primitive wrapper, or String classes, then the routine checks to see if the types are assignable and if they are,
 * simply assigns the values. Otherwise, the converter throws an exception.
 * 
 * @since Sep 26, 2013
 * @version $Id$
 */

public final class Converter {

    /**
     * The radix to use when converting to decimal
     */
    public static final int RADIX_DECIMAL = 10;

    /**
     * The maximum unsigned value that can be assigned to a byte
     */
    public static final short MAX_BYTE = 255;

    /**
     * The minimum unsigned value that can be assigned to a byte
     */
    public static final short MIN_BYTE = 0;

    /**
     * The maximum value a byte can have and still represent a single digit
     */
    public static final short MAX_DIGIT = 10;

    /**
     * Illegal conversion
     */
    public static final int ILLEGAL = -1;

    /**
     * Primitive byte
     */
    public static final int P_BYTE = 0;

    /**
     * Primitive char
     */
    public static final int P_CHAR = 1;

    /**
     * Primitive short
     */
    public static final int P_SHORT = 2;

    /**
     * Primitive int
     */
    public static final int P_INT = 3;

    /**
     * Primitive long
     */
    public static final int P_LONG = 4;

    /**
     * Primitive float
     */
    public static final int P_FLOAT = 5;

    /**
     * Primitive double
     */
    public static final int P_DOUBLE = 6;

    /**
     * Class Byte
     */
    public static final int C_BYTE = 7;

    /**
     * Class Character
     */
    public static final int C_CHARACTER = 8;

    /**
     * Class Short
     */
    public static final int C_SHORT = 9;

    /**
     * Class Integer
     */
    public static final int C_INTEGER = 10;

    /**
     * Class Long
     */
    public static final int C_LONG = 11;

    /**
     * Class Float
     */
    public static final int C_FLOAT = 12;

    /**
     * Class Double
     */
    public static final int C_DOUBLE = 13;

    /**
     * Class String
     */
    public static final int C_STRING = 14;

    /**
     * This static constant 2-dimensional array is the encoding of the following table using the type codes for the
     * various class and primitive types. The type of object being converted from and to is first looked up in the type
     * index table. The resulting index is then used to index into this table, where the from type is the major (outer)
     * index, and the destination type is the minor (inner) index. If the intersection is the destination type (or at
     * least NOT -1), then the conversion is legal. If the intersection is -1, then the conversion is not legal.
     */
    //@formatter:off 
    private static final int[][] LEGAL_CONVERSIONS = {

        // From type P_BYTE (0) 
        {      P_BYTE,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
               C_BYTE, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type P_CHAR (1) 
        {      P_BYTE,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
               C_BYTE, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type P_SHORT (2) 
        {     ILLEGAL,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type P_INT (3) 
        {     ILLEGAL,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type P_LONG (4) 
        {     ILLEGAL,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type P_FLOAT (5) 
        {     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type P_DOUBLE (6) 
        {     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_BYTE (7) 
        {      P_BYTE,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
               C_BYTE, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_CHARACTER (8) 
        {      P_BYTE,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
               C_BYTE, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_SHORT (9) 
        {     ILLEGAL,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_INTEGER (10) 
        {     ILLEGAL,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_LONG (11) 
        {     ILLEGAL,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_FLOAT (12) 
        {     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_DOUBLE (13) 
        {     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     ILLEGAL,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

        // From type C_STRING (14) 
        {     ILLEGAL,      P_CHAR,     P_SHORT,       P_INT,      P_LONG,     P_FLOAT,    P_DOUBLE, 
              ILLEGAL, C_CHARACTER,     C_SHORT,   C_INTEGER,      C_LONG,     C_FLOAT,    C_DOUBLE,    C_STRING}, 

    };

    //
    //  Source       Destination Types --->
    //   Types                                     C  
    //    |                                        h  
    //    |                                        a       I 
    //    |                                d       r       n           D   S
    //    V                s           f   o       a   S   t       F   o   t
    //             b   c   h       l   l   u   B   c   h   e   L   l   u   r
    //             y   h   o   i   o   o   b   y   t   o   g   o   o   b   i
    //             t   a   r   n   n   a   l   t   e   r   e   n   a   l   n 
    //             e   r   t   t   g   t   e   e   r   t   r   g   t   e   g
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //      byte | x | x | x | x | x | x | x | x | x | x | x | x | x | x | x | - 0
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //      char | x | x | x | x | x | x | x | x | x | x | x | x | x | x | x | - 1
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //     short |   | x | x | x | x | x | x |   | x | x | x | x | x | x | x | - 2
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //       int |   | x | x | x | x | x | x |   | x | x | x | x | x | x | x | - 3
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //      long |   | x | x | x | x | x | x |   | x | x | x | x | x | x | x | - 4
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //     float |   |   |   |   |   | x | x |   |   |   |   |   | x | x | x | - 5
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //    double |   |   |   |   |   | x | x |   |   |   |   |   | x | x | x | - 6
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //      Byte | x | x | x | x | x | x | x | x | x | x | x | x | x | x | x | - 7
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    // Character | x | x | x | x | x | x | x | x | x | x | x | x | x | x | x | - 8
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //     Short |   | x | x | x | x | x | x |   | x | x | x | x | x | x | x | - 9 
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //   Integer |   | x | x | x | x | x | x |   | x | x | x | x | x | x | x | - 10
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //      Long |   | x | x | x | x | x | x |   | x | x | x | x | x | x | x | - 11
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //     Float |   |   |   |   |   | x | x |   |   |   |   |   | x | x | x | - 12
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //    Double |   |   |   |   |   | x | x |   |   |   |   |   | x | x | x | - 13
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //    String |   | x | x | x | x | x | x |   | x | x | x | x | x | x | x | - 14 
    //           +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+ 
    //             0   1   2   3   4   5   6   7   8   9   10  11  12  13  14
    //
    //@formatter:on 

    private static final String[] TYPE_INDEX = {
        "byte", "char", "short", "int", "long", "float", "double", "java.lang.Byte", "java.lang.Character",
        "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double",
        "java.lang.String"
    };

    /**
     * This method converts the input to the destination type and returns a value of the destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input byte primitive
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, byte input) throws ConversionException {
        return convert(dest, Byte.valueOf(input));
    }

    /**
     * This method converts the input to the destination type and returns a value of the destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input char primitive
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, char input) throws ConversionException {
        return convert(dest, Character.valueOf(input));
    }

    /**
     * This method converts the input to the destination type and returns a value of the destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input double primitive
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, double input) throws ConversionException {
        return convert(dest, Double.valueOf(input));
    }

    /**
     * This method converts the input to the destination type and returns a value of the destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input float primitive
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, float input) throws ConversionException {
        return convert(dest, Float.valueOf(input));
    }

    /**
     * This method converts the input to the destination type and returns a value of the destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input int primitive
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, int input) throws ConversionException {
        return convert(dest, Integer.valueOf(input));
    }

    /**
     * This method converts the input to the destination type and returns a value of the destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input long primitive
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, long input) throws ConversionException {
        return convert(dest, Long.valueOf(input));
    }

    /**
     * This method converts the input object from the source type to the destination type and returns a value of the
     * destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input object to be converted. The class of this object is determined through reflection.
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, Object input) throws ConversionException {

        Object result = null;
        Class<?> source = input == null ? java.lang.String.class : input.getClass();
        if (!isLegal(source, dest)) {
            throw new ConversionException("Unsupported conversion from " + source.getName() + " to " + dest.getName());
        }
        int sourceIndex = lookupTypeIndex(source);
        int destIndex = lookupTypeIndex(dest);

        switch (destIndex) {
            case P_BYTE:
            case C_BYTE:
                result = convertToBytes(sourceIndex, input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = convertToChar(sourceIndex, input);
                break;
            case P_SHORT:
            case C_SHORT:
                result = convertToShort(sourceIndex, input);
                break;
            case P_INT:
            case C_INTEGER:
                result = convertToInteger(sourceIndex, input);
                break;
            case P_LONG:
            case C_LONG:
                result = convertToLong(sourceIndex, input);
                break;
            case P_FLOAT:
            case C_FLOAT:
                result = convertToFloat(sourceIndex, input);
                break;
            case P_DOUBLE:
            case C_DOUBLE:
                result = convertToDouble(sourceIndex, input);
                break;

            case C_STRING:
                result = convertToString(sourceIndex, input);
                break;

            default:
                if (dest.isAssignableFrom(source)) {
                    result = input;
                }
        }

        return result;
    }

    /**
     * This method converts the input to the destination type and returns a value of the destination type.
     * 
     * @param dest
     *            The destination type to convert to. This is the class of the required destination data type.
     * @param input
     *            The input short primitive
     * @return The converted value expressed as the destination type
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    public static Object convert(Class<?> dest, short input) throws ConversionException {
        return convert(dest, Short.valueOf(input));
    }

    /**
     * Converts a supported object type to a Byte
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a Byte
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Byte convertToBytes(int sourceIndex, Object input) throws ConversionException {
        Byte result = null;
        switch (sourceIndex) {
            case P_BYTE:
            case C_BYTE:
                result = toByte((Byte) input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = toByte((Character) input);
                break;
            case C_STRING:
                result = toByte(input);
        }
        return result;
    }

    /**
     * Converts a supported data type to a Character
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a Character
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Character convertToChar(int sourceIndex, Object input) throws ConversionException {
        Character result = null;
        switch (sourceIndex) {
            case P_BYTE:
            case C_BYTE:
                result = toChar((Byte) input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = toChar((Character) input);
                break;
            case P_SHORT:
            case C_SHORT:
                result = toChar((Short) input);
                break;
            case P_INT:
            case C_INTEGER:
                result = toChar((Integer) input);
                break;
            case P_LONG:
            case C_LONG:
                result = toChar((Long) input);
                break;
            case C_STRING:
                result = toChar(input);
        }
        return result;
    }

    /**
     * Converts a supported data type to a Double
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a Double
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Double convertToDouble(int sourceIndex, Object input) throws ConversionException {
        Double result = null;
        switch (sourceIndex) {
            case P_BYTE:
            case C_BYTE:
                result = toDouble((Byte) input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = toDouble((Character) input);
                break;
            case P_SHORT:
            case C_SHORT:
                result = toDouble((Short) input);
                break;
            case P_INT:
            case C_INTEGER:
                result = toDouble((Integer) input);
                break;
            case P_LONG:
            case C_LONG:
                result = toDouble((Long) input);
                break;
            case P_FLOAT:
            case C_FLOAT:
                result = toDouble((Float) input);
                break;
            case P_DOUBLE:
            case C_DOUBLE:
                result = toDouble((Double) input);
                break;
            case C_STRING:
                result = toDouble(input);
        }
        return result;
    }

    /**
     * Converts a supported data type to a Float
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a Float
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float convertToFloat(int sourceIndex, Object input) throws ConversionException {
        Float result = null;
        switch (sourceIndex) {
            case P_BYTE:
            case C_BYTE:
                result = toFloat((Byte) input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = toFloat((Character) input);
                break;
            case P_SHORT:
            case C_SHORT:
                result = toFloat((Short) input);
                break;
            case P_INT:
            case C_INTEGER:
                result = toFloat((Integer) input);
                break;
            case P_LONG:
            case C_LONG:
                result = toFloat((Long) input);
                break;
            case P_FLOAT:
            case C_FLOAT:
                result = toFloat((Float) input);
                break;
            case P_DOUBLE:
            case C_DOUBLE:
                result = toFloat((Double) input);
                break;
            case C_STRING:
                result = toFloat(input);
        }
        return result;
    }

    /**
     * Converts a supported data type to a Integer
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a Integer
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Integer convertToInteger(int sourceIndex, Object input) throws ConversionException {
        Integer result = null;
        switch (sourceIndex) {
            case P_BYTE:
            case C_BYTE:
                result = toInteger((Byte) input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = toInteger((Character) input);
                break;
            case P_SHORT:
            case C_SHORT:
                result = toInteger((Short) input);
                break;
            case P_INT:
            case C_INTEGER:
                result = toInteger((Integer) input);
                break;
            case P_LONG:
            case C_LONG:
                result = toInteger((Long) input);
                break;
            case C_STRING:
                result = toInteger(input);
        }
        return result;
    }

    /**
     * Converts a supported data type to a Long
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a Long
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Long convertToLong(int sourceIndex, Object input) throws ConversionException {
        Long result = null;
        switch (sourceIndex) {
            case P_BYTE:
            case C_BYTE:
                result = toLong((Byte) input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = toLong((Character) input);
                break;
            case P_SHORT:
            case C_SHORT:
                result = toLong((Short) input);
                break;
            case P_INT:
            case C_INTEGER:
                result = toLong((Integer) input);
                break;
            case P_LONG:
            case C_LONG:
                result = toLong((Long) input);
                break;
            case C_STRING:
                result = toLong(input);
        }
        return result;
    }

    /**
     * Converts a supported data type to a Short
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a Short
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Short convertToShort(int sourceIndex, Object input) throws ConversionException {
        Short result = null;
        switch (sourceIndex) {
            case P_BYTE:
            case C_BYTE:
                result = toShort((Byte) input);
                break;
            case P_CHAR:
            case C_CHARACTER:
                result = toShort((Character) input);
                break;
            case P_SHORT:
            case C_SHORT:
                result = toShort((Short) input);
                break;
            case P_INT:
            case C_INTEGER:
                result = toShort((Integer) input);
                break;
            case P_LONG:
            case C_LONG:
                result = toShort((Long) input);
                break;
            case C_STRING:
                result = toShort(input);
        }
        return result;
    }

    /**
     * Converts a supported data type to a String
     * 
     * @param sourceIndex
     *            The index position of the source type in the conversion matrix
     * @param input
     *            The input object to be converted to a String
     * @return The converted value
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static String convertToString(int sourceIndex, Object value) {
        String result = null;
        if (value != null) {
            result = value.toString();
        }

        return result;
    }

    /**
     * Returns an indication if the source type can be converted to the destination type
     * 
     * @param source
     *            The type of source object
     * @param dest
     *            The type of destination object
     * @return True if the types can be converted, false otherwise
     */
    public static boolean isLegal(Class<?> source, Class<?> dest) {
        int sourceIndex = lookupTypeIndex(source);
        int destIndex = lookupTypeIndex(dest);

        if (sourceIndex == -1 || destIndex == -1) {
            if (dest.isAssignableFrom(source)) {
                return true;
            }
            return false;
        }

        if (LEGAL_CONVERSIONS[sourceIndex][destIndex] == -1) {
            return false;
        }

        return true;
    }

    /**
     * Lookup the index of the data type if it is a supported class. If it is not supported, return -1.
     * 
     * @param clazz
     *            The class to lookup
     * @return The type index assigned to this class type, or -1 if not supported
     */
    private static int lookupTypeIndex(Class<?> clazz) {
        for (int index = 0; index < TYPE_INDEX.length; index++) {
            if (TYPE_INDEX[index].equals(clazz.getName())) {
                return index;
            }
        }

        return ILLEGAL;
    }

    /**
     * Trivial case of returning a byte from a byte
     * 
     * @param value
     *            The byte value (either a primitive boxed into a Byte or a Byte object reference)
     * @return The value as a Byte object (which can be unboxed)
     */
    private static Byte toByte(Byte value) {
        return value;
    }

    /**
     * Convert a character to a byte. Note that this works only as long as the character is single-byte encoded.
     * 
     * @param value
     *            The character to be converted
     * @return The equivalent value as a byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Byte toByte(Character value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (Character.isDigit(value.charValue())) {
            return Byte.valueOf(value.toString());
        }

        return Byte.valueOf((byte) value.charValue());
    }

    /**
     * Convert an object to a byte (by intermediate conversion to a String)
     * 
     * @param value
     *            The value to be converted
     * @return The byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Byte toByte(Object value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Byte.valueOf(value.toString().getBytes()[0]);
    }

    /**
     * @param value
     *            the Byte value to be converted
     * @return The value expressed as a Character
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Character toChar(Byte value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.byteValue() > MAX_BYTE || value.byteValue() < MIN_BYTE) {
            throw new ConversionException("Byte " + value + " cannot be converted to a character!");
        }
        if (value.byteValue() < MAX_DIGIT) {
            return Character.valueOf(Character.forDigit(value.byteValue(), RADIX_DECIMAL));
        }
        return Character.valueOf((char) value.byteValue());
    }

    /**
     * @param input
     *            The input to be "converted"
     * @return The Character value
     */
    private static Character toChar(Character input) {
        return input;
    }

    /**
     * @param value
     *            The Integer value to be converted
     * @return The value expressed as a Character
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Character toChar(Integer value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.intValue() > MAX_BYTE || value.intValue() < MIN_BYTE) {
            throw new ConversionException("Integer " + value + " cannot be converted to a character!");
        }
        if (value.byteValue() < MAX_DIGIT) {
            return Character.valueOf(Character.forDigit(value.byteValue(), RADIX_DECIMAL));
        }
        return Character.valueOf((char) value.byteValue());
    }

    /**
     * @param value
     *            The Long value to be converted
     * @return The value expressed as a Character
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Character toChar(Long value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.longValue() > MAX_BYTE || value.longValue() < MIN_BYTE) {
            throw new ConversionException("Long " + value + " cannot be converted to a character!");
        }
        if (value.byteValue() < MAX_DIGIT) {
            return Character.valueOf(Character.forDigit(value.byteValue(), RADIX_DECIMAL));
        }
        return Character.valueOf((char) value.byteValue());
    }

    /**
     * Convert an object to a character (by intermediate conversion to a String)
     * 
     * @param value
     *            The value to be converted
     * @return The byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Character toChar(Object value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Character.valueOf(value.toString().charAt(0));
    }

    /**
     * @param value
     *            The Short value to be converted
     * @return The value expressed as a Character
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Character toChar(Short value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.shortValue() > MAX_BYTE || value.shortValue() < MIN_BYTE) {
            throw new ConversionException("Short " + value + " cannot be converted to a character!");
        }
        if (value.byteValue() < MAX_DIGIT) {
            return Character.valueOf(Character.forDigit(value.byteValue(), RADIX_DECIMAL));
        }
        return Character.valueOf((char) value.byteValue());
    }

    /**
     * @param value
     *            The Byte value to be converted
     * @return The value expressed as a Double
     */
    private static Double toDouble(Byte value) {
        if (value == null) {
            return null;
        }

        return Double.valueOf(value.doubleValue());
    }

    /**
     * @param value
     *            The Character value to be converted
     * @return The value expressed as a Double
     */
    private static Double toDouble(Character value) {
        if (value == null) {
            return null;
        }

        if (Character.isDigit(value.charValue())) {
            return Double.valueOf(Character.getNumericValue(value.charValue()));
        }
        return Double.valueOf(value.charValue());
    }

    /**
     * @param value
     *            The Double value to be "converted"
     * @return The double value
     */
    private static Double toDouble(Double value) {
        return value;
    }

    /**
     * @param value
     *            The Float value to be converted
     * @return The value expressed as a Float
     */
    private static Double toDouble(Float value) {
        if (value == null) {
            return null;
        }

        return Double.valueOf(value.doubleValue());
    }

    /**
     * @param value
     *            The integer value to be converted
     * @return The value expressed as a Double
     */
    private static Double toDouble(Integer value) {
        if (value == null) {
            return null;
        }

        return Double.valueOf(value.doubleValue());
    }

    /**
     * @param value
     *            Value of the Long to be converted
     * @return The value expressed as a Double
     */
    private static Double toDouble(Long value) {
        if (value == null) {
            return null;
        }

        return Double.valueOf(value.doubleValue());
    }

    /**
     * Convert an object to a double (by intermediate conversion to a String)
     * 
     * @param value
     *            The value to be converted
     * @return The byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Double toDouble(Object value) throws ConversionException {
        if (value == null) {
            return null;
        }

        try {
            return Double.valueOf(value.toString());
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }

    /**
     * @param value
     *            The Short value to be converted
     * @return The value expressed as a Double
     */
    private static Double toDouble(Short value) {
        if (value == null) {
            return null;
        }

        return Double.valueOf(value.doubleValue());
    }

    /**
     * @param value
     *            The byte value (either a primitive boxed into a Byte or a Byte object reference)
     * @return The value as an Float object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float toFloat(Byte value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Float.valueOf(value.shortValue());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Float object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float toFloat(Character value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (Character.isDigit(value.charValue())) {
            return Float.valueOf(Character.getNumericValue(value.charValue()));
        }
        return Float.valueOf(value.charValue());
    }

    /**
     * @param value
     *            The Double value to be converted
     * @return The value expressed as a Float
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float toFloat(Double value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.longValue() > Float.MAX_VALUE || value.longValue() < Float.MIN_VALUE) {
            throw new ConversionException("Double " + value + " cannot be converted to a float!");
        }

        return Float.valueOf(value.floatValue());
    }

    /**
     * @param value
     *            The Float value to be "converted"
     * @return The value expressed as a Float
     */
    private static Float toFloat(Float value) {
        return value;
    }

    /**
     * @param value
     *            The integer value
     * @return The value as a Float object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float toFloat(Integer value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.longValue() > Integer.MAX_VALUE || value.longValue() < Integer.MIN_VALUE) {
            throw new ConversionException("Long " + value + " cannot be converted to a float!");
        }

        return Float.valueOf(value.longValue());
    }

    /**
     * @param value
     *            The long value
     * @return The value as a Long object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float toFloat(Long value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.longValue() > Float.MAX_VALUE || value.longValue() < Float.MIN_VALUE) {
            throw new ConversionException("Long " + value + " cannot be converted to a float!");
        }

        return Float.valueOf(value.floatValue());
    }

    /**
     * Convert an object to a integer (by intermediate conversion to a String)
     * 
     * @param value
     *            The value to be converted
     * @return The byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float toFloat(Object value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Float.valueOf(value.toString());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Float object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Float toFloat(Short value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Float.valueOf(value.longValue());
    }

    /**
     * @param value
     *            The byte value (either a primitive boxed into a Byte or a Byte object reference)
     * @return The value as an Integer object (which can be unboxed)
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Integer toInteger(Byte value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Integer.valueOf(value.shortValue());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Integer object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Integer toInteger(Character value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (Character.isDigit(value.charValue())) {
            return Integer.valueOf(Character.getNumericValue(value.charValue()));
        }
        return Integer.valueOf(value.charValue());
    }

    /**
     * @param value
     *            The integer value
     * @return The value as a Integer object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Integer toInteger(Integer value) throws ConversionException {
        return value;
    }

    /**
     * @param value
     *            The long value
     * @return The value as a Integer object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Integer toInteger(Long value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.longValue() > Integer.MAX_VALUE || value.longValue() < Integer.MIN_VALUE) {
            throw new ConversionException("Long " + value + " cannot be converted to a integer!");
        }

        return Integer.valueOf(value.intValue());
    }

    /**
     * Convert an object to a integer (by intermediate conversion to a String)
     * 
     * @param value
     *            The value to be converted
     * @return The byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Integer toInteger(Object value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Integer.valueOf(value.toString());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Integer object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Integer toInteger(Short value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Integer.valueOf(value.intValue());
    }

    /**
     * @param value
     *            The byte value (either a primitive boxed into a Byte or a Byte object reference)
     * @return The value as an Long object (which can be unboxed)
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Long toLong(Byte value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Long.valueOf(value.shortValue());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Integer object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Long toLong(Character value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (Character.isDigit(value.charValue())) {
            return Long.valueOf(Character.getNumericValue(value.charValue()));
        }
        return Long.valueOf(value.charValue());
    }

    /**
     * @param value
     *            The integer value
     * @return The value as a Long object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Long toLong(Integer value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (value.longValue() > Integer.MAX_VALUE || value.longValue() < Integer.MIN_VALUE) {
            throw new ConversionException("Long " + value + " cannot be converted to a integer!");
        }

        return Long.valueOf(value.longValue());
    }

    /**
     * @param value
     *            The long value
     * @return The value as a Long object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Long toLong(Long value) throws ConversionException {
        return value;
    }

    /**
     * Convert an object to a long (by intermediate conversion to a String)
     * 
     * @param value
     *            The value to be converted
     * @return The byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Long toLong(Object value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Long.valueOf(value.toString());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Long object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Long toLong(Short value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Long.valueOf(value.longValue());
    }

    /**
     * @param value
     *            The byte value (either a primitive boxed into a Byte or a Byte object reference)
     * @return The value as a Short object (which can be unboxed)
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Short toShort(Byte value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Short.valueOf(value.shortValue());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Short object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Short toShort(Character value) throws ConversionException {
        if (value == null) {
            return null;
        }

        if (Character.isDigit(value.charValue())) {
            return Short.valueOf((short) Character.getNumericValue(value.charValue()));
        }
        return Short.valueOf((short) value.charValue());
    }

    /**
     * @param value
     *            The integer value
     * @return The value as a Short object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Short toShort(Integer value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Short.valueOf(value.shortValue());
    }

    /**
     * @param value
     *            The long value
     * @return The value as a Short object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Short toShort(Long value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Short.valueOf(value.shortValue());
    }

    /**
     * Convert an object to a short (by intermediate conversion to a String)
     * 
     * @param value
     *            The value to be converted
     * @return The byte
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Short toShort(Object value) throws ConversionException {
        if (value == null) {
            return null;
        }

        return Short.valueOf(value.toString());
    }

    /**
     * @param value
     *            The character value
     * @return The value as a Short object
     * @throws ConversionException
     *             If the conversion cannot be performed for some reason.
     */
    private static Short toShort(Short value) throws ConversionException {
        return value;
    }

    /**
     * Private default constructor prevents instantiation
     */
    private Converter() {
    }
}
