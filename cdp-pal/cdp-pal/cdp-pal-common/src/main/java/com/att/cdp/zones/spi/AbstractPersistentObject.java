/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.ObjectService;

/**
 * @since May 2, 2014
 * @version $Id$
 */

public class AbstractPersistentObject extends AbstractService implements ObjectService {

    /**
     * @param context
     *            The context that we are connected to
     */
    public AbstractPersistentObject(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return "";
    }

}
