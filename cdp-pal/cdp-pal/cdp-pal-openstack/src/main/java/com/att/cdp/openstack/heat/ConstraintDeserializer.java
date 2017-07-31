/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat;

import java.io.IOException;

import com.att.cdp.openstack.heat.model.Constraint;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 
 * @since Jun 26, 2015
 * @version $Id$
 */

public class ConstraintDeserializer extends JsonDeserializer<Constraint> {

    /**
     * @see org.codehaus.jackson.map.JsonDeserializer#deserialize(org.codehaus.jackson.JsonParser,
     *      org.codehaus.jackson.map.DeserializationContext)
     */
    @Override
    public Constraint deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
        JsonProcessingException {

        return null;
    }

}
