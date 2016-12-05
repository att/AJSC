/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.HashMap;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedKeyPair;

/**
 * @since May 17, 2014
 * @version $Id$
 */

public class OpenStackKeyPair extends ConnectedKeyPair {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context that we are servicing
     * @param pair
     *            The OpenStack keypair object from the api
     */
    @SuppressWarnings("nls")
    public OpenStackKeyPair(Context context, com.woorea.openstack.nova.model.KeyPair pair) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();

        dictionary.put("name", "name");
        dictionary.put("userId", "userId");
        dictionary.put("publicKey", "publicKey");
        dictionary.put("privateKey", "privateKey");
        dictionary.put("fingerprint", "fingerprint");
        ObjectMapper.map(pair, this, dictionary);
    }

}
