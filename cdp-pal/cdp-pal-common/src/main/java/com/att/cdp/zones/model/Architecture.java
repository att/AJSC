/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

/**
 * Standard enumeration of platform architecture types as defined by the "libosinfo project"
 * 
 * @see <a href="http://libosinfo.org/">libosinfo</a>
 * @since Sep 30, 2013
 * @version $Id$
 */

public enum Architecture {

    /**
     * DEC 64-bit RISC
     */
    ALPHA,

    /**
     * ARM Cortex-A7 MPCore
     */
    ARMV71,

    /**
     * Ethernet, Token Ring, AXis - Code Reduced Instruction Set
     */
    CRIS,

    /**
     * Intel sixth-generation x86 (P6 microarchitecture)
     */
    I686,

    /**
     * Itanium
     */
    IA64,

    /**
     * Lattice Micro32
     */
    LM32,

    /**
     * Motorola 68000 series
     */
    M68K,

    /**
     * Xilinx 32-bit FPGA (Big Endian)
     */
    MICROBLAZE,

    /**
     * Xilinx 32-bit FPGA (Little Endian)
     */
    MICROBLAZEEL,

    /**
     * MIPS 32-bit RISC (Big Endian)
     */
    MIPS,

    /**
     * MIPS 64-bit RISC (Big Endian)
     */
    MIPS64,

    /**
     * MIPS 64-bit RISC (Little Endian)
     */
    MIPS64EL,

    /**
     * MIPS 32-bit RISC (Little Endian)
     */
    MIPSEL,

    /**
     * OpenCores RISC
     */
    OPENRISC,

    /**
     * HP Precision Architecture RISC
     */
    PARISC,

    /**
     * HP Precision Architecture 64-bit RISC
     */
    PARISC64,

    /**
     * IBM PowerPC 32-bit
     */
    PPC,

    /**
     * IBM PowerPC 64-bit
     */
    PPC64,

    /**
     * IBM PowerPC (Embedded 32-bit)
     */
    PPCEMB,

    /**
     * IBM Enterprise Systems Architecture/390
     */
    S390,

    /**
     * IBM Enterprise Systems Architecture/390 - Extended Architecture
     */
    S390X,

    /**
     * SuperH SH-4 (Little Endian)
     */
    SH4,

    /**
     * SuperH SH-4 (Big Endian)
     */
    SH4EB,

    /**
     * Scalable Processor Architecture, 32-bit
     */
    SPARC,

    /**
     * Scalable Processor Architecture, 64-bit
     */
    SPARC64,

    /**
     * Microprocessor Research and Development Center RISC Unicore32
     */
    UNICORE32,

    /**
     * 64-bit extension of IA-32
     */
    X86_64,

    /**
     * Tensilica Xtensa configurable microprocessor core
     */
    XTENSA,

    /**
     * Tensilica Xtensa configurable microprocessor core (Big Endian)
     */
    XTENSAEB;
}
