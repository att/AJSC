/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.filemonitor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AJSCPropertyService {
	private AJSCPropertiesMap filePropertiesMap;
	private List<File> fileList;
	private static final String FILE_CHANGE_LISTENER_LOC = System.getProperty("AJSC_CONF_HOME") + "/etc/appprops";
	static final Logger logger = LoggerFactory.getLogger(AJSCPropertyService.class);

	// do not remove the postConstruct annotation, init method will not be
	// called after constructor
	@PostConstruct
	public void init() throws Exception {

		try {
			getFileList(FILE_CHANGE_LISTENER_LOC);

			for (File file : fileList) {
				try {
					Object filePropertiesMap = this.filePropertiesMap;
					Method m = filePropertiesMap.getClass().getMethod("refresh", File.class);
					m.invoke(filePropertiesMap, file);
				} catch (Exception ioe) {
					logger.error("Error in the file monitor block", ioe);
				}
			}
		} catch (Exception ex) {
			logger.error("Error creating property map ", ex);
		}

	}

	private void getFileList(String dirName) throws IOException {
		File directory = new File(dirName);

		if (fileList == null)
			fileList = new ArrayList<File>();

		// get all the files that are ".json" or ".properties", from a directory
		// & it's sub-directories
		// we do NOT want "template" files to be added as these have swm node
		// variables that will be replaced
		// and
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.getName().startsWith("template")) {
				logger.info(file.getName()
						+ " will NOT be file monitored - template files are NOT loaded due to SWM node variable replacement");
			}
			if (file.isFile() && !file.getName().startsWith("template")
					&& (file.getPath().endsWith(".json") || file.getPath().endsWith(".properties"))) {
				fileList.add(file);
			} else if (file.isDirectory()) {
				getFileList(file.getPath());
			}
		}

	}

	public AJSCPropertiesMap getFilePropertiesMap() {
		return filePropertiesMap;
	}

	public void setFilePropertiesMap(AJSCPropertiesMap filePropertiesMap) {
		this.filePropertiesMap = filePropertiesMap;
	}
}
