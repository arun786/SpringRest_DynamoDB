package com.arun.springrestdynamodb.client;

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
@ConfigurationProperties(prefix = "dynamo.details")
@Getter
@Setter
public class DynamoDBConfig {
    private String tableName;
    private String region;
    private String endPoint;
    private String maxRetry;
    private String accessKey;
    private String secretKey;
}
