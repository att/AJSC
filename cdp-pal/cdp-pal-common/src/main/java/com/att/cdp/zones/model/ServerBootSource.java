/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

/**
 * @since Oct 16, 2015
 * @version $Id$
 */

public enum ServerBootSource {

    /**
     * The boot source cannot be determined
     */
    UNKNOWN,

    /**
     * The server has been booted from a bootable volume.
     */
    VOLUME,

    /**
     * The server was booted from an image
     */
    IMAGE,

    /**
     * The server was booted from a snapshot
     */
    SNAPSHOT,

    /**
     * Preboot Execution Environment, otherwise known as a "pixie" boot, is an image transfered to a machine via network
     * connection, also called a "network boot".
     */
    PXE;
}
