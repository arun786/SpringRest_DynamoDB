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
