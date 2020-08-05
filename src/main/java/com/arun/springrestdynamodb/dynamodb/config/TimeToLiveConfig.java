package com.arun.springrestdynamodb.dynamodb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author arun on 8/5/20
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "profile.counter.ttl")
@Getter
@Setter
public class TimeToLiveConfig {
    private String for24Hrs;
    private String for30Days;
}
