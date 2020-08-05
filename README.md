# SpringRest_DynamoDB

This API works with High level language to connect to local dynamo db. There will also be a configuration where it will 
connect to Global Table dynamo DB table.

## Dynamo DB configuration

1. ProfileCounter

The model maps to DynamoDb table with partition kay and sort key


    package com.arun.springrestdynamodb.dynamodb.model;
    
    import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
    import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
    import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
    import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
    import lombok.Getter;
    import lombok.Setter;
    
    /**
     * TokenCounter has composite key which consists of
     * 1. actorId as partition Key
     * 2. duration as sort key
     * <p>
     * the values can be as below
     * An actorId will have 2 sets of records, example below shows the value
     * <p>
     * actorId  duration   count
     * abc      for24Hr     3
     * abc      for30Days   3
     *
     * @author arun on 8/5/20
     */
    
    @DynamoDBTable(tableName = "TokenCounter")
    @Getter
    @Setter
    public class ProfileCounter {
        @DynamoDBHashKey(attributeName = "actorId")
        private String actorId;
        @DynamoDBRangeKey(attributeName = "duration")
        private String duration;
        @DynamoDBAttribute(attributeName = "count")
        private int count;
    }


 2. Create a DynamoDBMapper
 
 Create a DynamoDBMapper.
 
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
