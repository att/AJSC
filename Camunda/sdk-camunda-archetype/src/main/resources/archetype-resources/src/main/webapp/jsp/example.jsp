 <%-- Copyright (c) 2017 AT&T Intellectual Property. All rights reserved --%>
 
<%@page import="java.util.ArrayList"%>
<html>
	<head>
	<title>Sample JSP Page</title>
	<meta>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	</meta>
	</head>
	<body>
	    <c:out value="Jetty JSP Example"></c:out>
	    <br />
	    Current date is: <%=new java.util.Date()%>
	</body>
</html>