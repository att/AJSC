/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.att.cdp.exceptions.ConversionException;

/**
 * @since Sep 26, 2013
 * @version $Id$
 */
// @checkstyle:off
public class TestConverter {

    private Byte byteObject = Byte.valueOf((byte) 8);
    private byte bytePrimitive = 1;
    private Character charObject = Character.valueOf('9');
    private char charPrimitive = '2';
    private double doublePrimitive = 7.0d;
    private float floatPrimitive = 6.0f;
    private int intPrimitive = 4;
    private long longPrimitive = 5l;
    private short shortPrimitive = 3;

    /**
     * Tests that we can convert an object of various types to a byte (for supported types) and that we throw an
     * exception when it is unsupported.
     */
    @Test
    public void testToByteConversion() {
        Byte result = null;
        try {
            result = (Byte) Converter.convert(Byte.TYPE, bytePrimitive);
            assertEquals(Byte.valueOf((byte) 1), result);

            result = (Byte) Converter.convert(Byte.TYPE, byteObject);
            assertEquals(Byte.valueOf((byte) 8), result);

            result = (Byte) Converter.convert(Byte.TYPE, charPrimitive);
            assertEquals(Byte.valueOf((byte) 2), result);

            result = (Byte) Converter.convert(Byte.TYPE, charObject);
            assertEquals(Byte.valueOf((byte) 9), result);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            Converter.convert(Byte.TYPE, shortPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Byte.TYPE, intPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Byte.TYPE, longPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Byte.TYPE, floatPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Byte.TYPE, doublePrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }
    }

    /**
     * Tests that we can convert an object of various types to a char (for supported types) and that we throw an
     * exception when it is unsupported.
     */
    @Test
    public void testToCharConversion() {
        Character result = null;
        try {
            result = (Character) Converter.convert(Character.TYPE, bytePrimitive);
            assertEquals(Character.valueOf('1'), result);

            result = (Character) Converter.convert(Character.TYPE, charPrimitive);
            assertEquals(Character.valueOf('2'), result);

            result = (Character) Converter.convert(Character.TYPE, shortPrimitive);
            assertEquals(Character.valueOf('3'), result);

            result = (Character) Converter.convert(Character.TYPE, intPrimitive);
            assertEquals(Character.valueOf('4'), result);

            result = (Character) Converter.convert(Character.TYPE, longPrimitive);
            assertEquals(Character.valueOf('5'), result);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            Converter.convert(Character.TYPE, floatPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Character.TYPE, doublePrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }
    }

    /**
     * Tests that we can convert an object of various types to a float (for supported types) and that we throw an
     * exception when it is unsupported.
     */
    @Test
    public void testToFloatConversion() {
        Float result = null;
        try {
            result = (Float) Converter.convert(Float.TYPE, bytePrimitive);
            assertEquals(1.0, result.floatValue(), .1);

            result = (Float) Converter.convert(Float.TYPE, charPrimitive);
            assertEquals(2.0, result.floatValue(), .1);

            result = (Float) Converter.convert(Float.TYPE, shortPrimitive);
            assertEquals(3.0, result.floatValue(), .1);

            result = (Float) Converter.convert(Float.TYPE, intPrimitive);
            assertEquals(4.0, result.floatValue(), .1);

            result = (Float) Converter.convert(Float.TYPE, longPrimitive);
            assertEquals(5.0, result.floatValue(), .1);

            result = (Float) Converter.convert(Float.TYPE, floatPrimitive);
            assertEquals(6.0, result.floatValue(), .1);

            result = (Float) Converter.convert(Float.TYPE, doublePrimitive);
            assertEquals(7.0, result.floatValue(), .1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests that we can convert an object of various types to a integer (for supported types) and that we throw an
     * exception when it is unsupported.
     */
    @Test
    public void testToIntegerConversion() {
        Integer result = null;
        try {
            result = (Integer) Converter.convert(Integer.TYPE, bytePrimitive);
            assertEquals(Integer.valueOf(1), result);

            result = (Integer) Converter.convert(Integer.TYPE, charPrimitive);
            assertEquals(Integer.valueOf(2), result);

            result = (Integer) Converter.convert(Integer.TYPE, shortPrimitive);
            assertEquals(Integer.valueOf(3), result);

            result = (Integer) Converter.convert(Integer.TYPE, intPrimitive);
            assertEquals(Integer.valueOf(4), result);

            result = (Integer) Converter.convert(Integer.TYPE, longPrimitive);
            assertEquals(Integer.valueOf(5), result);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            Converter.convert(Integer.TYPE, floatPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Integer.TYPE, doublePrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }
    }

    /**
     * Tests that we can convert an object of various types to a long (for supported types) and that we throw an
     * exception when it is unsupported.
     */
    @Test
    public void testToLongConversion() {
        Long result = null;
        try {
            result = (Long) Converter.convert(Long.TYPE, bytePrimitive);
            assertEquals(Long.valueOf(1), result);

            result = (Long) Converter.convert(Long.TYPE, charPrimitive);
            assertEquals(Long.valueOf(2), result);

            result = (Long) Converter.convert(Long.TYPE, shortPrimitive);
            assertEquals(Long.valueOf(3), result);

            result = (Long) Converter.convert(Long.TYPE, intPrimitive);
            assertEquals(Long.valueOf(4), result);

            result = (Long) Converter.convert(Long.TYPE, longPrimitive);
            assertEquals(Long.valueOf(5), result);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            Converter.convert(Long.TYPE, floatPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Long.TYPE, doublePrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }
    }

    /**
     * Tests that we can convert an object of various types to a short (for supported types) and that we throw an
     * exception when it is unsupported.
     */
    @Test
    public void testToShortConversion() {
        Short result = null;
        try {
            result = (Short) Converter.convert(Short.TYPE, bytePrimitive);
            assertEquals(Short.valueOf((short) 1), result);

            result = (Short) Converter.convert(Short.TYPE, charPrimitive);
            assertEquals(Short.valueOf((short) 2), result);

            result = (Short) Converter.convert(Short.TYPE, shortPrimitive);
            assertEquals(Short.valueOf((short) 3), result);

            result = (Short) Converter.convert(Short.TYPE, intPrimitive);
            assertEquals(Short.valueOf((short) 4), result);

            result = (Short) Converter.convert(Short.TYPE, longPrimitive);
            assertEquals(Short.valueOf((short) 5), result);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            Converter.convert(Short.TYPE, floatPrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }

        try {
            Converter.convert(Short.TYPE, doublePrimitive);
            fail("Supposed to be unsupported!");
        } catch (ConversionException e) {
            // ignore
        }
    }
}
