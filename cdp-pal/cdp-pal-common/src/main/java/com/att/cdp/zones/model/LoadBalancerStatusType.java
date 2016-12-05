/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model; 

/**
 * @since May 19, 2015
 * @version $Id$
 */
public enum LoadBalancerStatusType {

    /**
     * 
     */
	ACTIVE,

    /**
	 * 
	 */
    BUILD,

    /**
	 * 
	 */
    DOWN,

    /**
	 * 
	 */
    ERROR,

    /**
	 * 
	 */
    PENDING_CREATE,

    /**
	 * 
	 */
    PENDING_DELETE,

    /**
	 * 
	 */
    PENDING_UPDATE;
}
