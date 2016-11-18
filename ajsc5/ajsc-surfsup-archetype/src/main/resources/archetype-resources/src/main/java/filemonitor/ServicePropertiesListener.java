package ${package}.filemonitor;

import java.io.File;

import com.att.ssf.filemonitor.FileChangedListener;

public class ServicePropertiesListener implements FileChangedListener {

	@Override
	public void update(File file) throws Exception 
	{
		ServicePropertiesMap.refresh(file);
	}
}
