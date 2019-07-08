package com.intuit.benten.splunk.MockServer;

import com.intuit.benten.splunk.actionhandlers.SplunkUserLogsActionHandler;
import com.intuit.benten.splunk.http.SplunkHttpClient;
import com.intuit.benten.splunk.properties.SplunkProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("mock")
@EnableAutoConfiguration
public class ServerConfig {
//    @Bean
//    public SplunkProperties splunkProperties() {
//        SplunkProperties props = new SplunkProperties();
//        props.setUsername("username");
//        props.setPassword("password");
//        return props;
//    }
//
//    @Bean
//    public SplunkHttpClient splunkClient() {
//        return new SplunkHttpClient();
//    }
//
//    @Bean
//    public SplunkUserLogsActionHandler splunkUserLogsActionHandler() {
//        return new SplunkUserLogsActionHandler();
//    }
//
//    @Bean
//    public RestTemplate restTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//        return restTemplate;
//    }
}
