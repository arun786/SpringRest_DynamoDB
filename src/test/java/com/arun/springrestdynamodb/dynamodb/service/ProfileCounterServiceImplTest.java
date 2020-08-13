package com.arun.springrestdynamodb.dynamodb.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.arun.springrestdynamodb.dynamodb.model.ProfileCounter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

/**
 * @author arun on 8/13/20
 */
class ProfileCounterServiceImplTest {

    static DynamoDBProxyServer sServer;
    static AmazonDynamoDB sClient;

    @BeforeAll
    public static void runDynamoDB() {
        System.setProperty("sqlite4java.library.path", "./build/libs/");

        final String[] localArgs = {"-inMemory"};

        try {
            sServer = ServerRunner.createServerFromCommandLineArgs(localArgs);
            sServer.start();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return;
        }

        createAmazonDynamoDBClient();

        createMyTables();


    }


    @Test
    void checkActorEligibleToGetActiveTokens() {
        System.out.println("eS");
    }

    @Test
    void updateActorCountDetails() {
    }

    @AfterClass
    public static void shutdownDynamoDB() {
        if (sServer != null) {
            try {
                sServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Profile("test")
    private static void createAmazonDynamoDBClient() {

//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//                .withCredentials(awsStaticCredentialsProvider)
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamoDBConfig.getEndPoint(), dynamoDBConfig.getRegion()))
//                .build();
//        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
//                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
//                .build();
//        return new DynamoDBMapper(client, mapperConfig);
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials("", "");

        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(basicAWSCredentials);
        sClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(awsStaticCredentialsProvider)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
    }

    private static void createMyTables() {
        //Create task tables
        DynamoDBMapper mapper = new DynamoDBMapper(sClient);
        CreateTableRequest tableRequest = mapper.generateCreateTableRequest(ProfileCounter.class);
        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        sClient.createTable(tableRequest);
    }
}