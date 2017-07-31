/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * A Utility class to perform operations around stream
 * 
 * @author vs053a
 */
public final class StreamUtility {

    /**
     * Default private constructor prevents instantiation
     */
    private StreamUtility() {
        // no-op
    }

    /**
     * This method reads an input stream to completion, buffering the contents, and returns it as a string
     * 
     * @param inputStream
     *            The input stream to be read
     * @return Input stream converted to string
     * @throws IOException
     *             If the stream can't be read
     */
    @SuppressWarnings("nls")
    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[4096];
        int amtRead = inputStream.read(buffer, 0, buffer.length);
        while (amtRead != -1) {
            builder.append(new String(buffer, 0, amtRead, "UTF-8"));
            amtRead = inputStream.read(buffer, 0, buffer.length);
        }

        return builder.toString();
    }
}
