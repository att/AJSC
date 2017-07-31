/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.att.cdp.exceptions.ZoneException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;


public class GenerateKeys {
	
	 private static final Logger LOG = LoggerFactory.getLogger(GenerateKeys.class);
	
	public static void generateKeyPair(com.att.cdp.zones.model.KeyPair kp) throws IOException, JSchException, ZoneException {
		KeyPair kpair;
		kpair = KeyPair.genKeyPair(new JSch(), KeyPair.RSA);	
	
	//	String publicKeyFilename = PUBLIC_KEY;
	//	String privateKeyFilename = PRIVATE_KEY;
		
		OutputStream os = new ByteArrayOutputStream();
		kpair.writePrivateKey(os);
		String str="";
		try {
			str = reformatSSHKey(os.toString());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		kp.setPrivateKey(str);
		
		os = new ByteArrayOutputStream();
		kpair.writePublicKey(os, "");		
		kp.setPublicKey(os.toString());
		
		kpair.getFingerPrint();		
		kpair.dispose();
		
	  
	}
	
	public static String reformatSSHKey(String privateKey) throws Exception {

        if (StringUtils.isEmpty(privateKey)) {
            throw new Exception(privateKey);
        }

        String begin = "-----BEGIN RSA PRIVATE KEY-----";
        String end = "-----END RSA PRIVATE KEY-----";
        StringBuffer buffer = new StringBuffer(StringHelper.stripCRLF(privateKey));

        int position = buffer.indexOf(begin);
        if (position != -1) {
            buffer.delete(position, begin.length());
        }
        position = buffer.indexOf(end);
        if (position != -1) {
            buffer.delete(position, position + end.length());
        }
        List<String> segments = segmentData(buffer.toString(), 64, "\r\n");
        buffer.setLength(0);
        buffer.append(begin + "\r\n");
        for (String segment : segments) {
            buffer.append(segment);
        }
        buffer.append(end + "\r\n");
        return buffer.toString();
    }

	public static List<String> segmentData(String data, int maxLen, String terminator) {
		StringBuffer buffer = new StringBuffer(StringHelper.stripCRLF(data));
		ArrayList<String> list = new ArrayList<String>();
		while (buffer.length() > 0) {
        int length = buffer.length() > maxLen ? maxLen : buffer.length();

        String segment = buffer.substring(0, length);
        if (terminator != null) {
            segment += terminator;
        }
        list.add(segment);
        buffer.delete(0, length);
    }

    return list;
}
	
}
