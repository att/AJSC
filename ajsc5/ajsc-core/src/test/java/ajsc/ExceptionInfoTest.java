/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ajsc.utils.ExceptionInfo;
import static org.junit.Assert.*;
public class ExceptionInfoTest extends BaseTestCase {
	ExceptionInfo i;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		super.setUp();
		i=new ExceptionInfo();
	}
	@Test
	public void getSetText(){
		if(i==null)System.out.println("I'm super null like sooooooooooooooo null");
		i.setText("Set Text");
		String s=i.getText();
		assertEquals("Set Text", s);
		
	}
	
	@Test
	public void getSetID(){
		i.setMessageId("2a");
		String s=i.getMessageId();
		assertEquals("2a", s);
	}
	
	@Test
	public void getVariables(){
		List<String> list1 =i.getVariables();
		list1.add("hello1");
		String s1=list1.get(0);
		assertEquals("hello1", s1);
		
		List<String>list2=i.getVariables();
		list2.add("hello2");
		String s2=list2.get(1);
		assertEquals("hello2", s2);
	}
	public static void main(String[] args) {
		ExceptionInfo i=new ExceptionInfo();
		i.setText("fap");
		System.out.println(i.getText());
	}
}
