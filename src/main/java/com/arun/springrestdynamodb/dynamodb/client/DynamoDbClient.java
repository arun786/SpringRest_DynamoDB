package com.arun.springrestdynamodb.dynamodb.client;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author arun on 8/5/20
 */
@Configuration
public class DynamoDbClient {

    /**
     * Configure DynamoDB Mapper to a consistent read, which means the latest updated records will be read from the table
     * This will guarantee to read the latest update from the table, default is eventual
     * <p>
     * saveBehavior required is the default one which is UPDATE, means during a save operation, all modeled attributes are updated,
     * and un modelled attributes are unaffected. Primitive number types (byte, int, long) are set to 0. Object types are set to null.
     * saveOperation can be CLOBBER, which has a behaviour of putItem rather than updateItem
     *
     * @return - DynamoDBMapper
     */
    @Bean
    public DynamoDBMapper configureDynamoDBMapper() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();
        return new DynamoDBMapper(client, mapperConfig);
    }
}
