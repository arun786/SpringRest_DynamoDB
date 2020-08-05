package com.arun.springrestdynamodb.dynamodb.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.arun.springrestdynamodb.client.DynamoDBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author arun on 8/5/20
 */
@Configuration
public class DynamoDbClient {

    private final DynamoDBConfig dynamoDBConfig;

    @Autowired
    public DynamoDbClient(DynamoDBConfig dynamoDBConfig) {
        this.dynamoDBConfig = dynamoDBConfig;
    }

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
    @Bean("dynamoDBMapper")
    @Profile("local")
    public DynamoDBMapper dynamoDBMapper() {

        /**
         * For local dynamo db table, we require the end point and the region. Credentials are not required for Local Dynamo DB table
         */
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamoDBConfig.getEndPoint(), dynamoDBConfig.getRegion()))
                .build();
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();
        return new DynamoDBMapper(client, mapperConfig);
    }

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
    @Bean("dynamoDBMapper")
    public DynamoDBMapper dynamoDBMapperForGlobal() {
        /**
         * For getting the DynamoDBMapper which can update the aws region, we require the access key and secret key for a role
         * which has got full access to Dynamo DB Table.
         */
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(dynamoDBConfig.getAccessKey(), dynamoDBConfig.getSecretKey());

        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(basicAWSCredentials);
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(awsStaticCredentialsProvider)
                .withRegion(dynamoDBConfig.getRegion())
                .build();

        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .build();

        return new DynamoDBMapper(client, mapperConfig);
    }
}
