/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="mailto:dh868g@att.com?subject=com.att.cdp.util.TestIPHelper">Dewayne Hafenstein</a>
 * @since Nov 24, 2014
 * @version $Id$
 */

public class TestIPHelper {

    /**
     * Test that IPv4 addresses are recognized correctly
     */
    @Test
    public void testValidIPv4() {
        assertTrue(IPHelper.isValidIpv4("1.2.3.4"));
        assertTrue(IPHelper.isValidIpv4("10.20.30.40"));
        assertTrue(IPHelper.isValidIpv4("100.102.103.104"));
        assertFalse(IPHelper.isValidIpv4("256.1.2.3"));
        assertFalse(IPHelper.isValidIpv4("1"));
        assertFalse(IPHelper.isValidIpv4("1.2"));
        assertFalse(IPHelper.isValidIpv4("1.2.3"));
        assertFalse(IPHelper.isValidIpv4("1.2.3.ABC"));
    }

    /**
     * Test the recognition of routable vs unroutable IPv4 addresses
     */
    @Test
    public void testIsRoutable() {
        assertTrue(IPHelper.isRoutable("2001:db8:85a3::8a2e:370:7334"));
        assertTrue(IPHelper.isRoutable("1.2.3.4"));
        assertFalse(IPHelper.isRoutable("192.168.0.0"));
        assertFalse(IPHelper.isRoutable("10.0.0.0"));
        assertTrue(IPHelper.isRoutable("172.15.255.255"));
        assertFalse(IPHelper.isRoutable("172.16.0.0"));
        assertFalse(IPHelper.isRoutable("172.17.0.0"));
        assertFalse(IPHelper.isRoutable("172.17.0.0"));
        assertFalse(IPHelper.isRoutable("172.18.0.0"));
        assertFalse(IPHelper.isRoutable("172.19.0.0"));
        assertFalse(IPHelper.isRoutable("172.20.0.0"));
        assertFalse(IPHelper.isRoutable("172.21.0.0"));
        assertFalse(IPHelper.isRoutable("172.22.0.0"));
        assertFalse(IPHelper.isRoutable("172.23.0.0"));
        assertFalse(IPHelper.isRoutable("172.24.0.0"));
        assertFalse(IPHelper.isRoutable("172.25.0.0"));
        assertFalse(IPHelper.isRoutable("172.26.0.0"));
        assertFalse(IPHelper.isRoutable("172.27.0.0"));
        assertFalse(IPHelper.isRoutable("172.28.0.0"));
        assertFalse(IPHelper.isRoutable("172.29.0.0"));
        assertFalse(IPHelper.isRoutable("172.30.0.0"));
        assertFalse(IPHelper.isRoutable("172.31.0.0"));
        assertTrue(IPHelper.isRoutable("172.32.0.0"));
        assertFalse(IPHelper.isRoutable("172.31.0.300"));

        assertFalse(IPHelper.isRoutable("172.24.101.169"));
    }

    /**
     * Make sure that we can convert an IPv4 address to the correct byte array representation
     */
    @Test
    public void testConvertIPV4Address() {
        byte[] values = IPHelper.convertIpv4Address("1.2.3.4");
        assertEquals(values[0], (byte) 1);
        assertEquals(values[1], (byte) 2);
        assertEquals(values[2], (byte) 3);
        assertEquals(values[3], (byte) 4);
    }

    /**
     * Test that we can convert an IPv6 address (with shorthand notation) to a byte array of its binary value
     */
    @Test
    public void testConvertIPv6Address() {
        byte[] values = IPHelper.convertIpv6Address("2001:db8:85a3::8a2e:370:7334");

        assertEquals(values[0], (byte) 0x20);
        assertEquals(values[1], (byte) 0x01);
        assertEquals(values[2], (byte) 0x0d);
        assertEquals(values[3], (byte) 0xb8);
        assertEquals(values[4], (byte) 0x85);
        assertEquals(values[5], (byte) 0xa3);
        assertEquals(values[6], (byte) 0x00);
        assertEquals(values[7], (byte) 0x00);
        assertEquals(values[8], (byte) 0x00);
        assertEquals(values[9], (byte) 0x00);
        assertEquals(values[10], (byte) 0x8a);
        assertEquals(values[11], (byte) 0x2e);
        assertEquals(values[12], (byte) 0x03);
        assertEquals(values[13], (byte) 0x70);
        assertEquals(values[14], (byte) 0x73);
        assertEquals(values[15], (byte) 0x34);
    }

    /**
     * Verify that we are expanding IPv6 addresses using the zero-fill shorthand notation
     */
    @Test
    public void testExpandIPV6Address() {
        String result = IPHelper.expandIpv6Address("2001:db8:85a3::8a2e:370:7334");
        assertEquals("2001:db8:85a3:0:0:8a2e:370:7334", result);

        result = IPHelper.expandIpv6Address("::1");
        assertEquals("0:0:0:0:0:0:0:1", result);

        result = IPHelper.expandIpv6Address("1::");
        assertEquals("1:0:0:0:0:0:0:0", result);

        result = IPHelper.expandIpv6Address("::");
        assertEquals("0:0:0:0:0:0:0:0", result);

        assertTrue(IPHelper.expandIpv6Address(null) == null);
        assertTrue(IPHelper.expandIpv6Address("") == null);
    }

    @Test
    public void verifyIsValidIp() {
        String invalidIPV4 = "192.168.1.300";
        assertFalse("Expect ip to be invalid", IPHelper.isValidIp(invalidIPV4));

        assertFalse("Expect ip to be invalid", IPHelper.isValidIp(null));

        assertFalse("Expect ip to be invalid", IPHelper.isValidIp(""));

        String validIPV4 = "192.168.1.255";
        assertTrue("Expect ip to be valid", IPHelper.isValidIp(validIPV4));

        String ipV6Valid = "2001:cdba:0000:0000:0000:0000:3257:9652";
        assertTrue("Expect ip to be valid", IPHelper.isValidIp(ipV6Valid));

        assertTrue(IPHelper.isValidIpv4(null) == false);
        assertTrue(IPHelper.isValidIpv4("") == false);
    }

    @Test
    public void verifyIsInvalidIPV6() {
        assertFalse("Expect ip to be invalid", IPHelper.isValidIpv6(null));
        assertFalse("Expect ip to be invalid", IPHelper.isValidIpv6(""));
    }

    /**
     * Test that the "intToHexString" method works correctly
     */
    @Test
    public void testIntToHexString() {
        assertEquals("0", IPHelper.intToHexString(0));
        assertEquals("1", IPHelper.intToHexString(1));
        assertEquals("2", IPHelper.intToHexString(2));
        assertEquals("3", IPHelper.intToHexString(3));
        assertEquals("4", IPHelper.intToHexString(4));
        assertEquals("5", IPHelper.intToHexString(5));
        assertEquals("6", IPHelper.intToHexString(6));
        assertEquals("7", IPHelper.intToHexString(7));
        assertEquals("8", IPHelper.intToHexString(8));
        assertEquals("9", IPHelper.intToHexString(9));
        assertEquals("A", IPHelper.intToHexString(10));
        assertEquals("B", IPHelper.intToHexString(11));
        assertEquals("C", IPHelper.intToHexString(12));
        assertEquals("D", IPHelper.intToHexString(13));
        assertEquals("E", IPHelper.intToHexString(14));
        assertEquals("F", IPHelper.intToHexString(15));
        assertEquals("10", IPHelper.intToHexString(16));
        assertEquals("20", IPHelper.intToHexString(32));
        assertEquals("30", IPHelper.intToHexString(48));
        assertEquals("40", IPHelper.intToHexString(64));
        assertEquals("50", IPHelper.intToHexString(80));
        assertEquals("60", IPHelper.intToHexString(96));
        assertEquals("70", IPHelper.intToHexString(112));
        assertEquals("80", IPHelper.intToHexString(128));
        assertEquals("90", IPHelper.intToHexString(144));
        assertEquals("A0", IPHelper.intToHexString(160));
        assertEquals("B0", IPHelper.intToHexString(176));
        assertEquals("C0", IPHelper.intToHexString(192));
        assertEquals("D0", IPHelper.intToHexString(208));
        assertEquals("E0", IPHelper.intToHexString(224));
        assertEquals("F0", IPHelper.intToHexString(240));
        assertEquals("FF", IPHelper.intToHexString(255));
        assertEquals("100", IPHelper.intToHexString(256));
    }

    /**
     * Test the helper method "toHex" to make sure it converts strings correctly
     */
    @Test
    public void testHexStringToInt() {
        assertEquals(0, IPHelper.hexStringToInt("0"));
        assertEquals(0, IPHelper.hexStringToInt("00"));
        assertEquals(0, IPHelper.hexStringToInt("000"));
        assertEquals(0, IPHelper.hexStringToInt("0000"));
        assertEquals(1, IPHelper.hexStringToInt("1"));
        assertEquals(1, IPHelper.hexStringToInt("01"));
        assertEquals(1, IPHelper.hexStringToInt("001"));
        assertEquals(1, IPHelper.hexStringToInt("0001"));
        assertEquals(2, IPHelper.hexStringToInt("2"));
        assertEquals(3, IPHelper.hexStringToInt("3"));
        assertEquals(4, IPHelper.hexStringToInt("4"));
        assertEquals(5, IPHelper.hexStringToInt("5"));
        assertEquals(6, IPHelper.hexStringToInt("6"));
        assertEquals(7, IPHelper.hexStringToInt("7"));
        assertEquals(8, IPHelper.hexStringToInt("8"));
        assertEquals(9, IPHelper.hexStringToInt("9"));
        assertEquals(10, IPHelper.hexStringToInt("A"));
        assertEquals(11, IPHelper.hexStringToInt("B"));
        assertEquals(12, IPHelper.hexStringToInt("C"));
        assertEquals(13, IPHelper.hexStringToInt("D"));
        assertEquals(14, IPHelper.hexStringToInt("e"));
        assertEquals(15, IPHelper.hexStringToInt("f"));
        assertEquals(16, IPHelper.hexStringToInt("10"));
        assertEquals(32, IPHelper.hexStringToInt("20"));
        assertEquals(48, IPHelper.hexStringToInt("30"));
        assertEquals(64, IPHelper.hexStringToInt("40"));
        assertEquals(80, IPHelper.hexStringToInt("50"));
        assertEquals(96, IPHelper.hexStringToInt("60"));
        assertEquals(112, IPHelper.hexStringToInt("70"));
        assertEquals(128, IPHelper.hexStringToInt("80"));
        assertEquals(144, IPHelper.hexStringToInt("90"));
        assertEquals(160, IPHelper.hexStringToInt("A0"));
        assertEquals(176, IPHelper.hexStringToInt("B0"));
        assertEquals(192, IPHelper.hexStringToInt("C0"));
        assertEquals(208, IPHelper.hexStringToInt("D0"));
        assertEquals(224, IPHelper.hexStringToInt("E0"));
        assertEquals(240, IPHelper.hexStringToInt("F0"));
        assertEquals(255, IPHelper.hexStringToInt("FF"));
        assertEquals(256, IPHelper.hexStringToInt("100"));
        assertEquals(512, IPHelper.hexStringToInt("200"));
        assertEquals(768, IPHelper.hexStringToInt("300"));
        assertEquals(1024, IPHelper.hexStringToInt("400"));
        assertEquals(1280, IPHelper.hexStringToInt("500"));
        assertEquals(1536, IPHelper.hexStringToInt("600"));
        assertEquals(1792, IPHelper.hexStringToInt("700"));
        assertEquals(2048, IPHelper.hexStringToInt("800"));
        assertEquals(2304, IPHelper.hexStringToInt("900"));
        assertEquals(2560, IPHelper.hexStringToInt("a00"));
        assertEquals(2816, IPHelper.hexStringToInt("b00"));
        assertEquals(3072, IPHelper.hexStringToInt("c00"));
        assertEquals(3328, IPHelper.hexStringToInt("d00"));
        assertEquals(3584, IPHelper.hexStringToInt("e00"));
        assertEquals(3840, IPHelper.hexStringToInt("f00"));
        assertEquals(4096, IPHelper.hexStringToInt("1000"));
        assertEquals(8192, IPHelper.hexStringToInt("2000"));
        assertEquals(12288, IPHelper.hexStringToInt("3000"));
        assertEquals(16384, IPHelper.hexStringToInt("4000"));
        assertEquals(20480, IPHelper.hexStringToInt("5000"));
        assertEquals(24576, IPHelper.hexStringToInt("6000"));
        assertEquals(28672, IPHelper.hexStringToInt("7000"));
        assertEquals(32768, IPHelper.hexStringToInt("8000"));
        assertEquals(36864, IPHelper.hexStringToInt("9000"));
        assertEquals(40960, IPHelper.hexStringToInt("a000"));
        assertEquals(45056, IPHelper.hexStringToInt("b000"));
        assertEquals(49152, IPHelper.hexStringToInt("c000"));
        assertEquals(53248, IPHelper.hexStringToInt("d000"));
        assertEquals(57344, IPHelper.hexStringToInt("e000"));
        assertEquals(61440, IPHelper.hexStringToInt("f000"));
        assertEquals(65536, IPHelper.hexStringToInt("10000"));
    }

    /**
     * Test the ability to convert a ip address and prefix length to a CIDR for both IPv4 and IPv6.
     */
    @Test
    public void testToCIDR() {
        assertEquals("192.168.1.0/24", IPHelper.toCIDR("192.168.1.123", 24));
        assertEquals("192.168.1.0/25", IPHelper.toCIDR("192.168.1.123", 25));
        assertEquals("192.168.1.64/26", IPHelper.toCIDR("192.168.1.123", 26));
        assertEquals("192.168.1.96/27", IPHelper.toCIDR("192.168.1.123", 27));
        assertEquals("192.168.1.112/28", IPHelper.toCIDR("192.168.1.123", 28));
        assertEquals("192.168.1.120/29", IPHelper.toCIDR("192.168.1.123", 29));
        assertEquals("192.168.1.120/30", IPHelper.toCIDR("192.168.1.123", 30));

        assertEquals("2001:CDBA::/32", IPHelper.toCIDR("2001:cdba::3257:9652", 32));
        assertEquals("2001:CDBA::/33", IPHelper.toCIDR("2001:cdba::3257:9652", 33));
        assertEquals("2001:CDBA::/34", IPHelper.toCIDR("2001:cdba::3257:9652", 34));
        assertEquals("2001:CDBA::/35", IPHelper.toCIDR("2001:cdba::3257:9652", 35));
        assertEquals("2001:CDBA::/36", IPHelper.toCIDR("2001:cdba::3257:9652", 36));
        assertEquals("2001:CDBA:1::/48", IPHelper.toCIDR("2001:cdba:0001::3257:9652", 48));
    }

    /**
     * Convert an IP address to the equivalent byte array
     */
    @Test
    public void testToByteArray() {
        /*
         * Test IPv4 ip address (dotted decimal notation)
         */
        byte[] array = IPHelper.toByteArray("192.168.1.255");
        assertEquals((byte) 192, array[0]);
        assertEquals((byte) 168, array[1]);
        assertEquals((byte) 1, array[2]);
        assertEquals((byte) 255, array[3]);

        /*
         * Test IPv6 (16-bit hexadecimal tokens separated by colons without the shorthand notation)
         */
        array = IPHelper.toByteArray("2001:cdba:0000:0000:0000:0000:3257:9652");
        assertEquals((byte) 32, array[0]);
        assertEquals((byte) 1, array[1]);
        assertEquals((byte) 205, array[2]);
        assertEquals((byte) 186, array[3]);
        assertEquals((byte) 0, array[4]);
        assertEquals((byte) 0, array[5]);
        assertEquals((byte) 0, array[6]);
        assertEquals((byte) 0, array[7]);
        assertEquals((byte) 0, array[8]);
        assertEquals((byte) 0, array[9]);
        assertEquals((byte) 0, array[10]);
        assertEquals((byte) 0, array[11]);
        assertEquals((byte) 50, array[12]);
        assertEquals((byte) 87, array[13]);
        assertEquals((byte) 150, array[14]);
        assertEquals((byte) 82, array[15]);

        /*
         * Test IPv6 (16-bit hexadecimal tokens separated by colons without the shorthand notation with truncated
         * tokens)
         */
        array = IPHelper.toByteArray("1:02:003:0004:0105:0106:0207:0308");
        assertEquals((byte) 0, array[0]);
        assertEquals((byte) 1, array[1]);
        assertEquals((byte) 0, array[2]);
        assertEquals((byte) 2, array[3]);
        assertEquals((byte) 0, array[4]);
        assertEquals((byte) 3, array[5]);
        assertEquals((byte) 0, array[6]);
        assertEquals((byte) 4, array[7]);
        assertEquals((byte) 1, array[8]);
        assertEquals((byte) 5, array[9]);
        assertEquals((byte) 1, array[10]);
        assertEquals((byte) 6, array[11]);
        assertEquals((byte) 2, array[12]);
        assertEquals((byte) 7, array[13]);
        assertEquals((byte) 3, array[14]);
        assertEquals((byte) 8, array[15]);

        /*
         * Test IPv6 (16-bit hexadecimal tokens separated by colons with the shorthand notation AND truncated tokens)
         */
        array = IPHelper.toByteArray("2001:db8:85a3::8a2e:370:7334");
        assertEquals((byte) 32, array[0]);
        assertEquals((byte) 1, array[1]);
        assertEquals((byte) 13, array[2]);
        assertEquals((byte) 184, array[3]);
        assertEquals((byte) 133, array[4]);
        assertEquals((byte) 163, array[5]);
        assertEquals((byte) 0, array[6]);
        assertEquals((byte) 0, array[7]);
        assertEquals((byte) 0, array[8]);
        assertEquals((byte) 0, array[9]);
        assertEquals((byte) 138, array[10]);
        assertEquals((byte) 46, array[11]);
        assertEquals((byte) 3, array[12]);
        assertEquals((byte) 112, array[13]);
        assertEquals((byte) 115, array[14]);
        assertEquals((byte) 52, array[15]);
    }

    @Test
    public void testNormalizeIPv6Address() {
        assertEquals("2001:DBA8::", IPHelper.normalizeIpv6("2001:dba8:0000:0000:0000:0000:0000:0000"));
        assertEquals("2001:DBA8::", IPHelper.normalizeIpv6("2001:dba8::"));
        assertEquals("2001:DBA8:1:2::", IPHelper.normalizeIpv6("2001:dba8:0001:0002:0000:0000:0000:0000"));
    }

    @Test
    public void testIsValidCIDR() {
        assertTrue(IPHelper.isValidCIDR("1.2.3.4/31"));
        assertTrue(IPHelper.isValidCIDR("255.0.0.0/1"));
        assertTrue(IPHelper.isValidCIDR("172.16.1.0/24"));
        assertFalse(IPHelper.isValidCIDR("172.16.1.0/0"));
        assertFalse(IPHelper.isValidCIDR("172.16.1.0/32"));
    }

    @Test
    public void testIsCIDRRoutable() {
        assertFalse(IPHelper.isCIDRRoutable("10.192.0.0/24"));
        assertTrue(IPHelper.isCIDRRoutable("172.15.255.255/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.16.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.17.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.18.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.19.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.20.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.21.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.22.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.23.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.24.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.25.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.26.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.27.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.28.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.29.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.30.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.31.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("172.31.255.255/24"));
        assertTrue(IPHelper.isCIDRRoutable("172.32.0.0/24"));
        assertFalse(IPHelper.isCIDRRoutable("192.168.0.0/24"));
    }
    
    @Test
    public void testGetAddressesInIPv4Subnet() {
    	List<String> ips = IPHelper.getAddressesInIPv4Subnet("135.40.234.0/24");
    	assertEquals(254, ips.size());
    	
    	List<String> blackList = new ArrayList<String>();
    	blackList.add("135.40.234.3");
    	blackList.add(null);
    	blackList.add("badIP");
    	List<String> ips2 = IPHelper.getAddressesInIPv4Subnet("135.40.234.0/24", blackList);
    	assertEquals(253, ips2.size());
    	assertFalse(ips2.contains("135.40.234.3"));
    }
}
