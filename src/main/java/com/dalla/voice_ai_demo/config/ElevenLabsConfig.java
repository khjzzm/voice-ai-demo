package com.dalla.voice_ai_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class ElevenLabsConfig {

    @Value("${elevenlabs.api.key}")
    private String apiKey;

    @Value("${elevenlabs.api.base-url}")
    private String baseUrl;

    @Value("${elevenlabs.api.timeout}")
    private int timeout;

    @Bean
    public RestTemplate elevenLabsRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
} 