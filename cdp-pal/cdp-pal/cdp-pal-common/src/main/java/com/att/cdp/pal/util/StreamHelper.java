/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A general helper class for stream manipulation.
 * 
 * @author <a href="mailto:dh868g@att.com?subject=com.att.scld.gw.util.StreamHelper">Dewayne Hafenstein</a>
 * @author $LastChangedBy: dh868g $
 * @since Oct 24, 2011
 * @version $Id: StreamHelper.java 54083 2011-11-02 20:09:57Z dh868g $
 */
public final class StreamHelper {

    /**
     * The amount of memory that we will reserve to read files.
     */
    protected static final int BUFFER_SEGMENT_LENGTH = 4096;

    /**
     * Default constructor so no one can instantiate us.
     */
    private StreamHelper() {
        /* empty */
    }

    /**
     * This method copies the contents of an input stream to an output stream. The streams are not closed after the
     * method completes.
     * 
     * @param input
     *            The input stream to read
     * @param output
     *            The output stream to write
     * @return The total number of bytes transfered between the streams
     * @throws IOException
     *             If anything fails.
     */
    public static int copyStreams(final InputStream input, final OutputStream output) throws IOException {
        int amtRead = 0;
        int totalLength = 0;
        byte[] segment = new byte[BUFFER_SEGMENT_LENGTH];
        amtRead = input.read(segment, 0, segment.length);
        while (amtRead != -1) {
            totalLength += amtRead;
            output.write(segment, 0, amtRead);
            amtRead = input.read(segment, 0, segment.length);
        }

        return totalLength;
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

        byte[] buffer = readAndBuffer(inputStream);
        return new String(buffer, "UTF-8");

        // StringBuilder builder = new StringBuilder();
        // byte[] buffer = new byte[4096];
        // int amtRead = inputStream.read(buffer, 0, buffer.length);
        // while (amtRead != -1) {
        // builder.append(new String(buffer, 0, amtRead, "UTF-8"));
        // amtRead = inputStream.read(buffer, 0, buffer.length);
        // }
        //
        // return builder.toString();
    }

    /**
     * This method reads the content of the indicated input stream and buffers the content in memory as a single
     * contiguous byte array.
     * <p>
     * In order to create a contiguous buffer without allocating an arbitrarily large amount of memory, this routine
     * reads up to 4K segments of the incoming file and adds them to an array list (in the order they were read). This
     * allows the byte array segments to be reassembled into a contiguous buffer once the total size has been
     * determined.
     * </p>
     * 
     * @param input
     *            The stream contents to be read
     * @return The byte array containing the contents.
     * @throws IOException
     *             if there are any errors
     */
    public static byte[] readAndBuffer(final InputStream input) throws IOException {
        ArrayList<byte[]> segments = new ArrayList<byte[]>();
        int totalLength = 0;
        int amtRead = 0;
        byte[] buffer = new byte[BUFFER_SEGMENT_LENGTH];
        amtRead = input.read(buffer, 0, BUFFER_SEGMENT_LENGTH);
        while (amtRead != -1) {
            byte[] segment = new byte[amtRead];
            System.arraycopy(buffer, 0, segment, 0, amtRead);
            segments.add(segment);
            totalLength += amtRead;
            amtRead = input.read(buffer, 0, BUFFER_SEGMENT_LENGTH);
        }

        /*
         * We need one contiguous buffer
         */
        buffer = null;
        int offset = 0;
        buffer = new byte[totalLength];
        for (int i = 0; i < segments.size(); i++) {
            byte[] segment = segments.get(i);
            System.arraycopy(segment, 0, buffer, offset, segment.length);
            offset += segment.length;
        }

        return buffer;
    }
}
