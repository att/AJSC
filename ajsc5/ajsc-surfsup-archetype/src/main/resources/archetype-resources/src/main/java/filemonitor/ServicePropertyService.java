package ${package}.filemonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.ssf.filemonitor.FileChangedListener;
import com.att.ssf.filemonitor.FileMonitor;

public class ServicePropertyService {
	private boolean loadOnStartup;
	private ServicePropertiesListener fileChangedListener;
	private ServicePropertiesMap filePropertiesMap;
	private String ssfFileMonitorPollingInterval;
	private String ssfFileMonitorThreadpoolSize;
	private List<File> fileList;
	private static final String FILE_CHANGE_LISTENER_LOC = System
			.getProperty("AJSC_CONF_HOME") + "/etc/appprops";
	private static final String USER_CONFIG_FILE = "service-file-monitor.properties";
	static final Logger logger = LoggerFactory
			.getLogger(ServicePropertyService.class);

	// do not remove the postConstruct annotation, init method will not be
	// called after constructor
	@PostConstruct
	public void init() throws Exception {

		try {
			getFileList(FILE_CHANGE_LISTENER_LOC);

			for (File file : fileList) {
				try {
					FileChangedListener fileChangedListener = this.fileChangedListener;
					Object filePropertiesMap = this.filePropertiesMap;
					Method m = filePropertiesMap.getClass().getMethod(
							"refresh", File.class);
					m.invoke(filePropertiesMap, file);
					FileMonitor fm = FileMonitor.getInstance();
					fm.addFileChangedListener(file, fileChangedListener,
							loadOnStartup);
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
		FileInputStream fis = null;

		if (fileList == null)
			fileList = new ArrayList<File>();

		// get all the files that are ".json" or ".properties", from a directory
		// & it's sub-directories
		File[] fList = directory.listFiles();

		for (File file : fList) {
			// read service property files from the configuration file
			if (file.isFile() && file.getPath().endsWith(USER_CONFIG_FILE)) {
				try {
					fis = new FileInputStream(file);
					Properties prop = new Properties();
					prop.load(fis);

					for (String filePath : prop.stringPropertyNames()) {
						fileList.add(new File(prop.getProperty(filePath)));
					}
				} catch (Exception ioe) {
					logger.error("Error reading the file stream ", ioe);
				} finally {
					fis.close();
				}
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

	public ServicePropertiesListener getFileChangedListener() {
		return fileChangedListener;
	}

	public void setFileChangedListener(
			ServicePropertiesListener fileChangedListener) {
		this.fileChangedListener = fileChangedListener;
	}

	public ServicePropertiesMap getFilePropertiesMap() {
		return filePropertiesMap;
	}

	public void setFilePropertiesMap(ServicePropertiesMap filePropertiesMap) {
		this.filePropertiesMap = filePropertiesMap;
	}
}
