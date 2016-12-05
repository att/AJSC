/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.StackService;

/**
 * An abstract base class for all service implementations in any concrete providers.
 * 
 * @since Jan 16, 2015
 * @version $Id$
 */

public abstract class AbstractStack extends AbstractService implements StackService {

    /**
     * @param context
     *            The context that we are connected to
     */
    public AbstractStack(Context context) {
        super(context);
    }
}
