/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.common;

public class CommonNames 
{
	// Definitions for extension CSI HTTP header values
	public static final String CSI_VERSION = "X-CSI-Version";
	public static final String CSI_ORIGINAL_VERSION = "X-CSI-OriginalVersion";
	public static final String CSI_CONVERSATION_ID = "X-CSI-ConversationId";
	public static final String CSI_UNIQUE_TXN_ID = "X-CSI-UniqueTransactionId";
	public static final String CSI_MESSAGE_ID = "X-CSI-MessageId";
	public static final String CSI_TIME_TO_LIVE = "X-CSI-TimeToLive";
	public static final String CSI_SEQUENCE_NUMBER = "X-CSI-SequenceNumber";
	public static final String CSI_TOTAL_IN_SEQUENCE = "X-CSI-TotalInSequence";
	public static final String CSI_ORIGINATOR_ID = "X-CSI-OriginatorId";
	public static final String CSI_DATE_TIME_STAMP = "X-CSI-DateTimeStamp";
	public static final String CSI_CLIENT_APP = "X-CSI-ClientApp";
	public static final String CSI_CLIENT_DME2_LOOKUP = "X-CSI-ClientDME2Lookup";
	public static final String CALL_TYPE ="CALL_TYPE";
	
	// Headers for CAET
	public static final String CAET_FAULT_CODE = "X-CAET-FaultCode";
	public static final String CAET_FAULT_DESC = "X-CAET-FaultDesc";
	public static final String CAET_FAULT_ENTITY = "X-CAET-FaultEntity";
	
	// Other request headers to access
	public static final String HTTP_LOCATION = "Location";
	public static final String HTTP_AUTHORIZATION = "Authorization";
	public static final String HTTP_ACCEPT = "accept";
	public static final String JSONP = "jsonp";
	public static final String NONSP = "nonsp";
	
	// Definitions for content type handling and request attributes
	public static final String ERROR_BODY_TYPE = "ERROR_BODY";
	public static final String REQUEST_BODY_TYPE = "REQUEST_BODY";
	public static final String RESPONSE_BODY_TYPE = "RESPONSE_BODY";
	public static final String BODY_TYPE_XML = "XML";
	public static final String BODY_TYPE_JSON = "JSON";
	public static final String REQUEST_CONTENT_WILDCARD = "*/*";
	public static final String REQUEST_CONTENT_XML = "application/xml";
	public static final String REQUEST_CONTENT_JSON = "application/json";
	public static final String RESPONSE_CONTENT_XML = "application/xml;charset=utf-8";
	public static final String RESPONSE_CONTENT_JSON = "application/json;charset=utf-8";
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String NO_CACHE = "no-cache,no-store";
	public static final String ATTR_TIME_TO_LIVE = "TIME_TO_LIVE";
	public static final String ATTR_START_TIME = "START_TIME";
	public static final String ROUTE_ENDPOINT_BEGIN_TIME = "BEGIN_TIME";
	public static final String ENDPOINT_NAME = "ENDPOINT_NAME";
	public static final String ATTR_TTL_DEFAULT = "60000";
	public static final String RESPONSE_BODY_TEXT = "RESPONSE_BODY_TEXT";
	public static final String CSI_USER_NAME = "USER_NAME";
	public static final String CSI_MOCK_USER_NAME = "ajscUser";

	public static final String CSI_PASSWORD = "PASSWORD";
	public static final int MAX_URI_LENGTH = 2048;
	
	// Other general stuff for schema, logging, etc.
	public static final String DOT_XSD = ".xsd";
	public static final String REQUEST_TAG = "Request";
	public static final String RESPONSE_TAG = "Response";

	public static final String INFO_TAG = "Info";

	public static final String CONTIVO_TRANSFORM_PACKAGE = "com.cingular.csi.transforms";
	public static final String DME2_TAG = "DME2";
	public static final String HYDRA_TAG = "HYDRA";
	public static final String CSI_M2E_LOGGER = "CSI_M2E_LOGGER";
	public static final String AUDIT_RECORD = "AUDIT_RECORD";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String M2E_CSI_RESTFUL = "M2ECSIRestful";
	public static final String PERF_RECORD = "PERF_RECORD";
	
	public static final String DOT = ".";
	public static final String EMPTY_STRING = "";
	
	//  fault codes
	public static final String CSI_AUTH_ERROR = "100";
	public static final String CSI_SERVICE_UNAVAIL_ERROR = "200";
	public static final String CSI_DATA_ERROR = "300";
	public static final String CSI_REQUEST_XML_ERROR = "400";
	public static final String CSI_BUS_PROC_ERROR = "500";
	public static final String CSI_UNKNOWN_ERROR = "900";
	public static final String CSI_SUCCESS_RESPONSE_CODE = "0";
	public static final String CSI_SUCCESS = "Success";
	
	
	// Error numbers
	public static final String CSI_GENERIC_AUTH_ERROR = "10000000001";
	public static final String CSI_GENERIC_SERVICE_UNAVAIL_ERROR = "20000000001";
	public static final String CSI_GENERIC_REQUEST_ERROR = "40000000001";
	public static final String CSI_GENERIC_UNKNOWN_ERROR = "90000000001";
	
	//Interceptor constants
	public static final String REQUEST_START_TIME = "REQUEST_START_TIME";
	// con
	public static final String COMPONENT_TYPE_RESTLET="rest";
	public static final String COMPONENT_TYPE_SERVLET="servlet";
	public static final String SOACLOUD_NAMESPACE ="SOACLOUD_NAMESPACE";
	public static final String AJSC_CSI_RESTFUL = "AjscCsiRestful";
    public static final String AFT_DME2_CLIENT_REQ_SEND_TIMESTAMP= "AFT_DME2_CLIENT_REQ_SEND_TIMESTAMP";
    public static final String HTTP_HEADER_SERVICE_NAME = "X-CSI-ServiceName";
}
