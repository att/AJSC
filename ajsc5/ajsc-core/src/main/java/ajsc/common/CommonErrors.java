/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.common;

/**
 *  
 * The CommonErrors class is a generic class to hold standard 900 level errors and 400 level errors. The 900 level errors are sent back when no other
 * error matches. The 400 level errors are send back on deserialization exceptions. The JSON strings are send back as the error payload if there is no
 * <i>Accept</i> header value or if the <i>Accept</i> header value is <i>application/json</i>. The XML errors are sent back if the <i>Accept</i> header
 * values are <i>application/xml</i>.
 */

public class CommonErrors 
{
	/**
	 * Standard CAET codes
	 * status code 401: 10000000005 / Authorization/Authentication Failure
	 * status code 403: 10000000003 / Incomplete Credentials Provided
	 * status code 501: 20000000005 / System Configuration Error
	 * status code 503: 20000000003 / A Resource Required By Service Is Not Available
	 * status code 500: 20000000013 / Unknown Error Returned
	 * status code 4NN: 40000000001 / Incorrect request
	 * status code 5NN: 20000000013 / Unknown Error Returned
	 */
	public static final String DEF_401_FAULT_CODE = "10000000005";
	public static final String DEF_401_FAULT_DESC = "Authorization/Authentication Failure";
	
	public static final String DEF_403_FAULT_CODE = "10000000003";
	public static final String DEF_403_FAULT_DESC = "Incomplete Credentials Provided" ;
	
	public static final String DEF_501_FAULT_CODE = "20000000005";
	public static final String DEF_501_FAULT_DESC = "System Configuration Error";
	
	public static final String DEF_503_FAULT_CODE = "20000000003";
	public static final String DEF_503_FAULT_DESC = "A Resource Required By Service Is Not Available";
	
	public static final String DEF_500_FAULT_CODE = "20000000013";
	public static final String DEF_500_FAULT_DESC = "Unknown Error Returned";
	
	public static final String DEF_5NN_FAULT_CODE = "20000000013";
	public static final String DEF_5NN_FAULT_DESC = "Unknown Error Returned";
	
	public static final String DEF_4NN_FAULT_CODE = "40000000001";
	public static final String DEF_4NN_FAULT_DESC = "Incorrect request";
	
	/**
	 * Status code 401
	 */
	public static final String INVALID_USER = "POL0002";
	public static final String INVALID_USER_MSG = "Invalid credentials in the request";
	
	/**
	 * Status code 404
	 */
	public static final String NO_RESOURCE = "SVC3001";
	public static final String NO_RESOURCE_MSG = "The resource does not exist";
	
	/**
	 * Status code 405
	 */
	public static final String METHOD_NOT_ALLOWED = "POL0003";
	public static final String METHOD_NOT_ALLOWED_MSG = "The method is not allowed %1";
	
	/**
	 * Status code 406
	 */
	public static final String NOT_ACCEPTABLE = "SVC4001";
	public static final String NOT_ACCEPTABLE_MSG = "The response media type is not acceptable %1";
	
	/**
	 * Status code 415
	 */
	public static final String INVALID_CONTENT = "SVC4002";
	public static final String INVALID_CONTENT_MSG = "The request media type is not supported %1";
	
	/**
	 * status code 400 - No parameter
	 */
	public static final String NO_PARAM = "SVC4003";
	public static final String NO_PARAM_MSG = "A required parameter was not passed %1";
	
	/**
	 * Status code 	403
	 */
	public static final String NOT_PERMITTED = "POL1000";
	public static final String NOT_PERMITTED_MSG = "Operation not permitted: %1";
	
	/**
	 * Status code 503
	 */
	public static final String NOT_AVAILABLE = "SVC2000";
	public static final String NOT_AVAILABLE_MSG = "A resource required by the service is not available: %1";
	
	/**
	 * Status code 503
	 */
	public static final String RESOURCE_BUSY = "POL2000";
	public static final String RESOURCE_BUSY_MSG = "A downstream resource is busy or slow: %1";
	
	/**
	 * Status code 400
	 */
	public static final String DATA_ERROR = "SVC3000";
	public static final String DATA_ERROR_MSG = "A data error has occurred: %1";
	
	/**
	 * status code 400
	 */
	public static final String INVALID_REQUEST = "SVC4000";
	public static final String INVALID_REQUEST_MSG = "The request is invalid: %1";
	
	/**
	 * Status code 400
	 */
	public static final String BUSPROC_ERROR = "SVC5000";
	public static final String BUSPROC_ERROR_MSG = "A business processing error has occurred: %1";
	
	
	/**
	 * Unknown errors correspond to status code 500
	 */
	public static final String UNKOWN_ERROR = "SVC9999";
	public static final String UNKNOWN_ERROR_MSG = "An internal error has occurred";
	
	/**
	 * 
	 */
	/**
	 * The generic XML error for unknown processing exceptions.
	 */
	public static final String GENERIC_XML_ERROR = "<RequestError xmlns=\"http://csi.cingular.com/CSI/Namespaces/Rest/RequestError.xsd\">\n"
		+ "\t<ServiceException>\n"
		+ "\t\t<MessageId>SVC9999</MessageId>\n"
		+ "\t\t<Text>An internal error has occurred</Text>\n"
		+ "\t</ServiceException>\n"
		+ "</RequestError>";

	/**
	 * The generic XML error for unknown processing exceptions.
	 */
	public static final String GENERIC_XML_NONSP_ERROR = "<RequestError>\n"
		+ "\t<ServiceException>\n"
		+ "\t\t<MessageId>SVC9999</MessageId>\n"
		+ "\t\t<Text>An internal error has occurred</Text>\n"
		+ "\t</ServiceException>\n"
		+ "</RequestError>";	
	
	/**
	 * The generic JSON error for unknown processing exceptions.
	 */
	public static final String GENERIC_JSON_ERROR = "{\n"
			 + "\t\"ServiceException\": {\n"
			 + "\t\t\"MessageId\": \"SVC9999\",\n"
			 + "\t\t\"Text\": \"An internal error has occurred\"\n"
			 + "\t}\n"
			 + "}\n";

}
