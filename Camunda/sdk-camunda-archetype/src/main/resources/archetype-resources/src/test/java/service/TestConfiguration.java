package ${package}.service;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;

public class TestConfiguration {
    @Bean("jaxrsProviders")
    public ArrayList<Object> provider(){
        return new ArrayList<Object>();
    }
    @Bean("jaxrsServices")
    public ArrayList<Object> service(){
        return new ArrayList<Object>();
    }

}
