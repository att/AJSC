node {
    // Get the maven tool.
    // ** NOTE: This 'M3' maven tool must be configured
    // **       in the Jenkins global configuration.
    def mvnHome = tool 'M3'
    sh "echo ${mvnHome}"
    
    
    // Mark the code checkout 'stage'....
    stage 'Checkout'
    // Get some code from a GitHub repository
    checkout scm    
   
    // Mark the code build 'stage'....
    stage 'Build ajsc5'
    // Run the maven build
    //sh for unix bat for windows
	
	//sh "${mvnHome}/bin/mvn -f att-camel-dme2-servlet/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f ajsc5/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f cdp-pal/cdp-pal/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f cdp-pal/cdp-pal-common/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f cdp-pal/cdp-pal-openstack/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f Camunda/sdk-java-camunda-core/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f Camunda/sdk-camunda-archetype/pom.xml clean deploy"
    
} 
