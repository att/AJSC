/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

/**
 * This enumeration formalizes the specification of the disk image format, rather than a String that can vary from one
 * provider to another. The implementation of the RIME api must map the provider-specific format code to one of these
 * values.
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */

public enum Format {
    /**
     * This indicates what is stored in Glance is an Amazon kernel image
     */
    AKI,

    /**
     * This indicates what is stored in Glance is an Amazon machine image
     */
    AMI,

    /**
     * This indicates what is stored in Glance is an Amazon ramdisk image
     */
    ARI,

    /**
     * An archive format for the data contents of an optical disc (e.g. CDROM)
     */
    ISO,

    /**
     * A disk format supported by the QEMU emulator that can expand dynamically and supports Copy on Write
     */
    QCOW2,

    /**
     * This is an unstructured disk image format
     */

    RAW,

    /**
     * A disk format supported by VirtualBox virtual machine monitor and the QEMU emulator
     */
    VDI,

    /**
     * This is the VHD disk format, a common disk format used by virtual machine monitors from VMWare, Xen, Microsoft,
     * VirtualBox, and others
     */
    VHD,

    /**
     * Another common disk format supported by many common virtual machine monitors
     */
    VMDK;
}
