/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.openejb.config.Deployment;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

import ajsc.exceptions.CSIRESTException;
import ajsc.exceptions.RESTExceptionUtil;
import ajsc.exceptions.RefresheableRESTErrorMap;
import ajsc.exceptions.RestError;

public class RESTExceptionUtilAndRefresheableRestErrorMapTest extends BaseTestCase {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		super.setUp();
		
	}
	
	@Test
	public void setCaetParametersFromHttpCodeTest(){
		CSIRESTException csir=RESTExceptionUtil.nativeRESTError(401, "red", "idk", "sup");
		 assertEquals(401,csir.getHTTPErrorCode());

		 csir=RESTExceptionUtil.nativeRESTError(403, "red", "idk", "sup");
		 assertEquals(403,csir.getHTTPErrorCode());

		 csir=RESTExceptionUtil.nativeRESTError(501, "red", "idk", "sup");
		 assertEquals(501,csir.getHTTPErrorCode());

		 csir=RESTExceptionUtil.nativeRESTError(503, "red", "idk", "sup");
		 assertEquals(503,csir.getHTTPErrorCode());

		 csir=RESTExceptionUtil.nativeRESTError(402, "red", "idk", "sup");
		 assertEquals(402,csir.getHTTPErrorCode());

	}
	
	@Test
	public void refreshAndgetHttpCodeForCSIErrorTest() throws Exception{
		File f=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"RestError.txt");

		RefresheableRESTErrorMap.refresh(f);
		
		try{
			RefresheableRESTErrorMap.refresh(f);
		}catch(Exception e){
			
		}
		RestError re=RefresheableRESTErrorMap.getHttpCodeForCSIError("100");
		assertEquals("ajsc.exceptions.RestError",re.getClass().getName());
	}
}
