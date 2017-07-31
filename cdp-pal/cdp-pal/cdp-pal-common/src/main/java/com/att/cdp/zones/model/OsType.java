/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

/**
 * Standard enumeration of O/S distributiuon types as defined by the "libosinfo project"
 * 
 * @see <a href="http://libosinfo.org/">libosinfo</a>
 * @since Sep 30, 2013
 * @version $Id$
 */

public enum OsType {
    /**
     * Arch Linux
     */
    ARCH,

    /**
     * Community Enterprise Operating System
     */
    CENTOS,

    /**
     * Debian
     */
    DEBIAN,

    /**
     * Fedora
     */
    FEDORA,

    /**
     * FreeBSD
     */
    FREEBSD,

    /**
     * Gentoo Linux
     */
    GENTOO,

    /**
     * Mandrake linux (MandrakeSoft)
     */
    MANDRAKE,

    /**
     * Mandriva Linux
     */
    MANDRIVA,

    /**
     * Mandriva Enterprise Server
     */
    MES,

    /**
     * Microsoft Disc Operating System
     */
    MSDOS,

    /**
     * NetBSD
     */
    NETBSD,

    /**
     * Novell NetWare
     */
    NETWARE,

    /**
     * OpenBSD
     */
    OPENBSD,

    /**
     * OpenSolaris
     */
    OPENSOLARIS,

    /**
     * openSUSE
     */
    OPENSUSE,

    /**
     * Red Hat Enterprise Linux
     */
    RHEL,

    /**
     * SUSE Linux Enterprise Desktop
     */
    SLED,

    /**
     * Ubuntu
     */
    UBUNTU,

    /**
     * Microsoft Windows
     */
    WINDOWS,

    /**
     * Default
     */
    DEFAULT;
}
