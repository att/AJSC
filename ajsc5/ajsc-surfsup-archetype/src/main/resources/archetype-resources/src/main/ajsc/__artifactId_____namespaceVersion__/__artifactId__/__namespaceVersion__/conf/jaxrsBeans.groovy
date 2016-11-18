beans{
	xmlns cxf: "http://camel.apache.org/schema/cxf"
	xmlns jaxrs: "http://cxf.apache.org/jaxrs"
	xmlns util: "http://www.springframework.org/schema/util"
	
	echoService(${package}.JaxrsEchoService)
	userService(${package}.JaxrsUserService)
	
	util.list(id: 'jaxrsServices') {
		ref(bean:'echoService')
		ref(bean:'userService')
	}
}