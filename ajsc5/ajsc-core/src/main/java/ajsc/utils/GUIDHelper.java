/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
/*
 * Created on Mar 24, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ajsc.utils;

import java.util.UUID;

import ajsc.common.CommonNames;

public class GUIDHelper {

	static final String CONVERSATION_PREPEND = "~CNG-CSI~";

	/**
	 * returns the unique conversation id in the CSI format
	 * @param partnerName - the partnername to prepend
	 * @return
	 */
	public static String createCSIConversationId(String partnerName) {
		return partnerName + CONVERSATION_PREPEND + createGUID();
	}

    /**
	 * returns the unique transaction id in the CSI format
	 * @return
	 */
	public static String createUniqueTransactionId()
	{
		return CommonNames.AJSC_CSI_RESTFUL 
				+ SystemParams.instance().getPid() 
				+ "@" 
				+ SystemParams.instance().getVtier() 
				+ createGUID();
	}
	

    /**
	 * returns the unique conversation id in the CSI format
	 * @param partnerName - the partnername to prepend
	 * @return
	 */
	public static String createCSIConversationId(String partnerName, String guid) {
		return partnerName + CONVERSATION_PREPEND + guid;
	}

	/**
	 * returns a Unique Universal identifier
	 * @return - the GUID generated
	 */
	public synchronized static String createGUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Checks if the conversation ID is of the proper CSI conversation id format.
	 *
	 * @param partnerName - The partner name whose conversation ID will be verified
	 * @param conversationId - The id that will be checked
	 * @return - true/false value stating if the conversation id is a valid CSI conversation Id or not
	 */
	public static boolean isValidCSIConversationId(String partnerName, String conversationId) {
		if (partnerName != null && conversationId != null)
			return conversationId.startsWith(partnerName+CONVERSATION_PREPEND);
		else
			return false;
	}

	/**
	 * Checks if the conversation ID is of the proper CSI conversation id format.
	 * If it is valid the method will return the same conversation Id. If it is not valid it will create a valid
	 * CSI conversation ID and will return the new value.
	 *
	 * @param partnerName - The partner name that will be used
	 * @param conversationId - The id that will be checked
	 * @return a valid conversation id for the provided partner name
	 */
	public static String createValidCSIConversationId(String partnerName, String conversationId) {
		if (isValidCSIConversationId(partnerName, conversationId))
			return conversationId;
		else
			return createCSIConversationId(partnerName);
	}
	
	public static String insertConversationId(byte[] message, String conversationId ) {
		
			String data = new String (message);
			String finalMessage = "";
			int firstPos = data.indexOf("conversationId>");
			int lastPos = -1;
			if(firstPos != -1) {
					lastPos = data.indexOf("</", firstPos);
					String firstPart = "";
					if(lastPos != -1) {
						firstPart = data.substring(0, firstPos + 15);
						String lastPart = data.substring(lastPos , data.length()); 
						finalMessage = firstPart.concat(conversationId).concat(lastPart);
						
					}
			}
		return finalMessage;
	}

//	public static void main(String[] args){
//		/*GUIDHelper g = new GUIDHelper();
//		System.out.println("Valid conv id for input [csitest,csitest~CNG-CSI~12345] is: " + createValidCSIConversationId("csitest","csitest~CNG-CSI~12345"));
//		System.out.println("Valid conv id for input [csitest,csitest~CN1G-CSI~12345] is: " + createValidCSIConversationId("csitest","csitest~CN1G-CSI~12345"));
//		System.out.println("Valid conv id for input [ATT,csitest~CN1G-CSI~12345] is: " + createValidCSIConversationId("ATT","csitest~CN1G-CSI~12345"));
//		System.out.println("Valid conv id for input [null,csitest~CN1G-CSI~12345] is: " + createValidCSIConversationId(null,"csitest~CN1G-CSI~12345"));
//		System.out.println("Valid conv id for input [ATT,null] is: " + createValidCSIConversationId("ATT",null));
//		System.out.println("Valid conv id for input [null,null] is: " + createValidCSIConversationId(null,null));*/
//		
//		String xmlFile = "C:/CingularCVS/CSI/ServiceGateway/R7_0/test/ValidateAddress/ValidateAddressSoapRequest.xml";
//		FileInputStream fis;
//		try {
//			fis = new FileInputStream(xmlFile);
//			int len = fis.available();
//			byte data[] = new byte[len];
//			fis.read(data);
//			fis.close();
//			//System.out.println(new String(insertConversationId(data, "csitest", "12345" )));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		
//		
//	}
}
