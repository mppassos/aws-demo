package com.matheus.awsdemo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.dynamodb")
public class DynamoDBConfig {
    private String endpoint;
    private String tableName;
}