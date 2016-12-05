/**
 * Copyright (C) 2015, AT&T Inc. All rights reserved. Proprietary materials, property of AT&T. For internal use only,
 * not for disclosure to parties outside of AT&T or its affiliates.
 */

package com.att.cdp.openstack.heat;

import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.type.JavaType;

/**
 * @since Jun 26, 2015
 * @version $Id$
 */

public class ConstraintResolver implements TypeIdResolver {

    /**
     * Base class for type token classes used both to contain information and as keys for deserializers.
     */
    private JavaType baseType;

    /**
     * Method that will be called once before any type resolution calls; used to initialize instance with configuration.
     * This is necessary since instances may be created via reflection, without ability to call specific constructor to
     * pass in configuration settings.
     * 
     * @param baseType
     *            Base type for which this id resolver instance is used
     * @see org.codehaus.jackson.map.jsontype.TypeIdResolver#init(org.codehaus.jackson.type.JavaType)
     */
    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    /**
     * @see org.codehaus.jackson.map.jsontype.TypeIdResolver#idFromValue(java.lang.Object)
     */
    @Override
    public String idFromValue(Object value) {
        return idFromValueAndType(value, value.getClass());
    }

    /**
     * Alternative method used for determining type from combination of value and type, using suggested type (that
     * serializer provides) and possibly value of that type. Most common implementation will use suggested type as is.
     * 
     * @see org.codehaus.jackson.map.jsontype.TypeIdResolver#idFromValueAndType(java.lang.Object, java.lang.Class)
     */
    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method called to resolve type from given type identifier.
     * 
     * @see org.codehaus.jackson.map.jsontype.TypeIdResolver#typeFromId(java.lang.String)
     */
    @Override
    public JavaType typeFromId(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Accessor for mechanism that this resolver uses for determining type id from type. Mostly informational; not
     * required to be called or used.
     * 
     * @see org.codehaus.jackson.map.jsontype.TypeIdResolver#getMechanism()
     */
    @Override
    public Id getMechanism() {
        return Id.CUSTOM;
    }

}
