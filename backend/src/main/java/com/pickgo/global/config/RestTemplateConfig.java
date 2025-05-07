package com.pickgo.global.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Primary
	@Bean(name = "defaultClient")
	public RestTemplate defaultRestTemplate(RestTemplateBuilder builder) {
		return builder
			.connectTimeout(Duration.ofSeconds(3))
			.readTimeout(Duration.ofSeconds(5))
			.build();
	}
}

