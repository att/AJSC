/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.util;

import org.junit.Test;
import org.junit.Ignore;

import com.att.cdp.AbstractTestCase;
import com.att.cdp.exceptions.AuthorizationException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ResourceUnavailableException;
import com.att.cdp.exceptions.ZoneException;
import com.woorea.openstack.base.client.OpenStackResponseException;

public class TestExceptionMapper extends AbstractTestCase {

    @Ignore
    @Test(expected = AuthorizationException.class)
    public void testMapException401() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("Authorization", 401));
    }

    @Ignore
    @Test(expected = AuthorizationException.class)
    public void testMapException403() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("Authorization", 403));
    }

    @Ignore
    @Test(expected = AuthorizationException.class)
    public void testMapException405() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("Authorization", 405));
    }

    @Ignore
    @Test(expected = AuthorizationException.class)
    public void testMapException407() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("Authorization", 407));
    }

    @Ignore
    @Test(expected = ResourceNotFoundException.class)
    public void testMapException404() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("ResourceNotFoundException", 404));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException400() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 400));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException406() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 406));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException411() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 411));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException409() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 409));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException410() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 410));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException412() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 412));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException413() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 413));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException414() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 414));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException415() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 415));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException416() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 416));
    }

    @Ignore
    @Test(expected = InvalidRequestException.class)
    public void testMapException417() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("InvalidRequestException", 417));
    }

    @Ignore
    @Test(expected = ResourceUnavailableException.class)
    public void testMapException408() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("ResourceUnavailableException", 408));
    }

    @Ignore
    @Test(expected = ZoneException.class)
    public void testMapException500() throws ZoneException {
        ExceptionMapper.mapException(new OpenStackResponseException("ResourceUnavailableException", 500));
    }
}
