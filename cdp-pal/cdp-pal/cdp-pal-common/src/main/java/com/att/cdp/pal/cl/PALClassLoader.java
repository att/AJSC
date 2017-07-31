/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.cl;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.att.cdp.pal.util.FileHelper;

/**
 * This class loader is used to provide isolation of classes from each other when they are loaded as part of a provider.
 * <p>
 * Each provider loaded has its own instance of this class loader and is used to load all classes referenced in that
 * provider, unless the references were loaded by the system class loader (global references or shared references).
 * </p>
 * <p>
 * This class loader simply extends the {@link URLClassLoader} and overrides the
 * {@link URLClassLoader#loadClass(String)} method. This method is called to request the loading of a class, and is
 * passed the "binary name" of the class (the name we refer to in java code, fully qualified with the package, such as
 * "java.lang.String"). This method performs a child-first class load strategy by delegating to the overridden
 * {@link #findClass(String)} method. This method will search the URLs of the class path set on this class loader for
 * the class. If the class is found, then it is returned to the caller. If not, then the parent of this class loader is
 * called on it's {@link #loadClass(String)} method to attempt to load the class.
 * </p>
 * <p>
 * Methods are provided that allow the caller that creates this class loader to manipulate the class path on the loader
 * to set it to whatever is needed.
 * </p>
 * 
 */

public class PALClassLoader extends URLClassLoader {

    /**
     * Construct the class loader as a child of a specified parent class loader using child-first loader strategy.
     * 
     * @param parent
     *            The parent class loader of this class loader
     */
    public PALClassLoader(final ClassLoader parent) {
        this(new URL[] {}, parent);
    }

    /**
     * Construct the class loader as a child of a specified parent class loader using child-first loader strategy.
     * 
     * @param path
     *            An array of URL's that are used to set the initial class path search list for this class loader
     * @param parent
     *            The parent class loader of this class loader
     */
    public PALClassLoader(URL[] path, final ClassLoader parent) {
        super(path, parent);
    }

    /**
     * This method adds a set of URLs to the end of the current class path where the collection of URLs to be added is
     * in an array of URL objects. If a path element already exists, it is not added again.
     * 
     * @param urls
     *            The ordered collections (as an array) of urls to be added to the end of the current class path
     */
    public void addURLs(final URL[] urls) {
        if (urls != null && urls.length > 0) {
            for (URL url : urls) {
                super.addURL(url);
            }
        }
    }

    /**
     * This method adds a specific URL to the end of the current class path. If it already exists, it is not added
     * again.
     * 
     * @param url
     *            The URL specifying a directory or an archive (zip or jar) that is to be added to the current class
     *            path
     */
    public void addURL(final URL url) {
        if (url != null) {
            super.addURL(url);
        }
    }

    /**
     * Load the requested class
     * <p>
     * This method performs child-first load policy. This means that we will attempt to locate and load the class FIRST,
     * before calling our parent class loader. This is usually done only to isolate classes that may have the same fully
     * qualified name, but may be a different version, from the same class(es) that may be loaded by the parent. The
     * default policy of class loaders is parent-first, which is designed to minimize the number of classes that get
     * loaded, but prevents duplicate classes with different versions or code sources from being loaded.
     * </p>
     * 
     * @param name
     *            The name of the class to be found.
     * @return the class desired
     * @throws ClassNotFoundException
     *             if the class cannot be found.
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {

        /*
         * First, check our class cache to see if we already loaded it
         */
        Class<?> clazz = super.findLoadedClass(name);

        /*
         * If we haven't loaded it already, then try to load it ourselves.
         */
        if (clazz == null) {
            try {
                clazz = findClass(name);
            } catch (ClassNotFoundException e) {
                /*
                 * If we fail to find the class, call our parent and request him to load it.
                 */
                clazz = super.getParent().loadClass(name);
            }
        }

        return clazz;
    }
}
