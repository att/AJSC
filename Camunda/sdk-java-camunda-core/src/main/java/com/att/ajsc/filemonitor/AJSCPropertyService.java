/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AJSCPropertyService {
	private boolean loadOnStartup;
	//private AJSCPropertiesListener fileChangedListener;
	@Autowired
	private AJSCPropertiesMap filePropertiesMap;
	private String ssfFileMonitorPollingInterval;
	private String ssfFileMonitorThreadpoolSize;
	private List<File> fileList;
	
	private static final String FILE_CHANGE_LISTENER_LOC = System
			.getProperty("com.att.ajsc.app.prop.path");
	static final Logger logger = LoggerFactory
			.getLogger(AJSCPropertyService.class);

	// do not remove the postConstruct annotation, init method will not be
	// called after constructor
	@PostConstruct
	public void init() throws Exception {

		try {
			if(FILE_CHANGE_LISTENER_LOC!=null ) {
			getFileList(FILE_CHANGE_LISTENER_LOC);
			
			for (File file : fileList) {
				try {
					Object filePropertiesMap = this.filePropertiesMap;
					Method m = filePropertiesMap.getClass().getMethod(
							"refresh", File.class);
					m.invoke(filePropertiesMap, file);
				} catch (Exception ioe) {
					logger.error("Error in the file monitor block", ioe);
				}
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
		// we do NOT want "template" files to be added as these have swm node variables that will be replaced
		// and 
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.getName().startsWith("template")){
				logger.info(file.getName() + " will NOT be file monitored - template files are NOT loaded due to SWM node variable replacement");
			}
			if (file.isFile() && (file.getPath()
							.endsWith(".properties"))) {
				fileList.add(file);
			} else if (file.isDirectory()) {
				getFileList(file.getPath());
			}
		}

	}

	public void setLoadOnStartup(boolean loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public void setSsfFileMonitorPollingInterval(
			String ssfFileMonitorPollingInterval) {
		this.ssfFileMonitorPollingInterval = ssfFileMonitorPollingInterval;
	}

	public void setSsfFileMonitorThreadpoolSize(
			String ssfFileMonitorThreadpoolSize) {
		this.ssfFileMonitorThreadpoolSize = ssfFileMonitorThreadpoolSize;
	}

	public boolean getLoadOnStartup() {
		return loadOnStartup;
	}

	public String getSsfFileMonitorPollingInterval() {
		return ssfFileMonitorPollingInterval;
	}

	public String getSsfFileMonitorThreadpoolSize() {
		return ssfFileMonitorThreadpoolSize;
	}

	public AJSCPropertiesMap getFilePropertiesMap() {
		return filePropertiesMap;
	}

	public void setFilePropertiesMap(AJSCPropertiesMap filePropertiesMap) {
		this.filePropertiesMap = filePropertiesMap;
	}
}
