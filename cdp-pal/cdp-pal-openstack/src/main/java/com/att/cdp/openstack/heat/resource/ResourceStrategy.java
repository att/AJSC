/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.resource;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.openstack.model.OpenStackStack;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This class is a strategy that uses a chain-of-responsibility pattern to access concrete strategy implementations to
 * handle each resource.
 * <p>
 * This class is used to process OpenStack stack resources, one at a time, by passing each resource through the series
 * of strategy implementation classes. The first one that understands the specific resource type handles it and stops
 * the chain, unwinding the calls back to this class.
 * </p>
 * <p>
 * The specific resource strategies are each implemented as a single class that understands how to map one, and only
 * one, resource type. These strategies are organized as a chain-of-responsibility pattern so that this class need not
 * know how to interpret each resource type. Instead, the job of interpretation is left to each specialized strategy
 * class.
 * </p>
 * 
 * @since Feb 11, 2015
 * @version $Id$
 */

public class ResourceStrategy {

    private List<AbstractResourceStrategy> strategies;

    /**
     * Construct the resource strategy and the concrete strategy implementation objects
     */
    public ResourceStrategy() {
        strategies = new ArrayList<AbstractResourceStrategy>();

        Class<?> me = this.getClass();
        ClassLoader cl = me.getClassLoader();
        String packageName = me.getPackage().getName() + ".strategy";
        String path = packageName.replaceAll("\\.", "/");
        try {
            Enumeration<URL> urls = cl.getResources(path);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                File dir = new File(url.getFile());
                File[] files = dir.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.endsWith(".class")) {
                            return true;
                        }
                        return false;
                    }
                });

                for (File file : files) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    strategies.add((AbstractResourceStrategy) clazz.newInstance());
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException
            | ClassCastException e) {

            ConfigurationFactory.getConfiguration().getApplicationLogger().error(EELFResourceManager.format(e));
        }
    }

    /**
     * This method is used to handle a specific resource definition returned from OpenStack and to map that resource
     * appropriately into the OpenStack stack abstraction model object.
     * 
     * @param stack
     *            The abstraction model object that is the target of the mapping of the resource
     * @param resource
     *            The resource to be "handled"
     * @return The stack model object
     */
    public OpenStackStack handle(OpenStackStack stack, com.woorea.openstack.heat.model.Resource resource) {
        for (AbstractResourceStrategy strategy : strategies) {
            if (strategy.isMapped(resource)) {
                strategy.map(stack, resource);
                return stack;
            }
        }
        return stack;
    };

}
