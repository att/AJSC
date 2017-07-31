/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
  
package com.att.cdp.zones.test;

import java.util.List;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.ACL;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Template;

/**
 *
 * @since Nov 24, 2015
 * @version $Id$
 */

public class DummyServer extends Server {

    /**
     * 
     */
    public DummyServer() {
    }

    /**
     * @param context
     */
    @SuppressWarnings("nls")
    public DummyServer(Context context) {
        super(context);
        setName("Dummy");
    }

    /**
     * @param name
     */
    public DummyServer(String name) {
        super(name);
    }

    /**
     * @param name
     * @param image
     * @param template
     */
    public DummyServer(String name, Image image, Template template) {
        super(name, image, template);
    }

    /**
     * @param name
     * @param image
     * @param template
     * @param accessControl
     */
    public DummyServer(String name, Image image, Template template, List<ACL> accessControl) {
        super(name, image, template, accessControl);
    }

    /**
     * @param name
     * @param imageId
     * @param templateId
     */
    public DummyServer(String name, String imageId, String templateId) {
        super(name, imageId, templateId);
    }

    /**
     * @param name
     * @param image
     * @param template
     * @param aclName
     */
    public DummyServer(String name, String image, String template, String aclName) {
        super(name, image, template, aclName);
    }

}
