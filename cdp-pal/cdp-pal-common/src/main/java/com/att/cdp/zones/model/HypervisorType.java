/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

/**
 * @since Sep 30, 2013
 * @version $Id$
 */

public enum HypervisorType {
    /**
     * Server virtualization with Microsoft's Hyper-V, use to run Windows, Linux, and FreeBSD virtual machines.
     */
    HYPERV,

    /**
     * Kernel-based Virtual Machine
     */
    KVM,

    /**
     * Linux Containers (through libvirt), use to run Linux-based virtual machines.
     */
    LXC,

    /**
     * Server virtualization with IBM PowerVM, use to run AIX, IBM i and Linux environments on IBM POWER technology.
     */
    POWERVM,

    /**
     * Quick EMUlator, generally only used for development purposes.
     */
    QEMU,

    /**
     * User Mode Linux, generally only used for development purposes.
     */
    UML,

    /**
     * 4.1 update 1 and newer, runs VMWare-based Linux and Windows images through a connection with a vCenter server or
     * directly with an ESXi host.
     */
    VMWARE,

    /**
     * XenServer, Xen Cloud Platform (XCP), use to run Linux or Windows virtual machines.
     */
    XEN;
}
