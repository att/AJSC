/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v1;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.exceptions.ContextClosedException;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.connectors.NovaConnector;
import com.att.cdp.openstack.model.OpenStackImage;
import com.att.cdp.openstack.util.ExceptionMapper;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.spi.AbstractImage;
import com.att.cdp.zones.spi.RequestState;
import com.woorea.openstack.base.client.OpenStackBaseException;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.nova.model.Images;

/**
 * @since Sep 25, 2013
 * @version $Id$
 */

public class OpenStackImageService extends AbstractImage {

    private NovaConnector nova;

    /**
     * @param context
     *            The context object for our connection to the OpenStack provider
     */
    public OpenStackImageService(Context context) {
        super(context);
    }

    /**
     * This is a helper method used to construct the Glance service object and setup the environment to access the
     * OpenStack image service (Glance).
     * 
     * @throws NotLoggedInException
     *             If the user is not logged in
     * @throws ContextClosedException
     *             If the user attempts an operation after the context is closed
     */
    private void connect() throws NotLoggedInException, ContextClosedException {
        checkLogin();
        checkOpen();
        Context context = getContext();
        OpenStackContext osContext = (OpenStackContext) context;
        // connector = osContext.getGlanceConnector();
        nova = osContext.getNovaConnector();
        ((OpenStackContext) context).refreshIfStale(nova);
    }

    /**
     * @see com.att.cdp.zones.ImageService#getImage(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public Image getImage(String id) throws ZoneException {
        checkArg(id, "id");
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.IMAGE, id);
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

        try {
            return new OpenStackImage(context, nova.getClient().images().show(id).execute());
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }
        return null; // just for the compiler
    }

    /**
     * This method is used to list all of the images that are available within the context.
     * 
     * @return The list of images or an empty list if there are none available.
     * @throws ZoneException
     *             If the service fails.
     * @see com.att.cdp.zones.ImageService#listImages()
     */
    @Override
    public List<Image> listImages() throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

        ArrayList<Image> list = new ArrayList<>();

        try {
            for (com.woorea.openstack.nova.model.Image osImage : nova.getClient().images().list(true).execute()) {
                list.add(new OpenStackImage(context, osImage));
            }
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }
        return list;
    }

    /**
     * This method is used to list all of the images where the name of the image matches the supplied pattern.
     * 
     * @param pattern
     *            The pattern (regular expression) that is compared to the image name to select it for the returned
     *            list.
     * @return The list of images that match the specified name pattern
     * @throws ZoneException
     *             If anything fails
     * @see com.att.cdp.zones.ImageService#listImages(java.lang.String)
     */
    @Override
    public List<Image> listImages(String pattern) throws ZoneException {
        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.IMAGE, pattern);
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

        ArrayList<Image> list = new ArrayList<>();
        try {
            for (com.woorea.openstack.nova.model.Image osImage : nova.getClient().images().list(true).execute()) {
                if (pattern != null) {
                    if (osImage.getName().matches(pattern)) {
                        list.add(new OpenStackImage(context, osImage));
                    }
                } else {
                    list.add(new OpenStackImage(context, osImage));
                }
            }
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return list;
    }

    /**
     * This method is used to get the image where the name of the image exactly matches the supplied name.
     * 
     * @param name
     *            The name that is compared to the image name to select it for the returned image.
     * @return The image that match the specified name
     * @throws ZoneException
     *             If anything fails
     * @see com.att.cdp.zones.ImageService#getImageByName(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public Image getImageByName(String name) throws ZoneException {
        checkArg(name, "name");

        connect();
        Context context = getContext();

        trackRequest();
        RequestState.put(RequestState.IMAGE, name);
        RequestState.put(RequestState.SERVICE, "Compute");
        RequestState.put(RequestState.SERVICE_URL, nova.getEndpoint());

        Image image = null;
        try {
            com.woorea.openstack.nova.api.ImagesResource.List openStackRequest = nova.getClient().images().list(true);
            openStackRequest.queryParam("name", name);
            Images images = openStackRequest.execute();
            if (images.getList() != null && images.getList().size() == 1) {
                image = new OpenStackImage(context, images.getList().get(0));
            }
        } catch (OpenStackBaseException ex) {
            ExceptionMapper.mapException(ex);
        }

        return image;
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return nova.getEndpoint();
    }
}
