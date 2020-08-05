package com.arun.springrestdynamodb.dynamodb.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.arun.springrestdynamodb.dynamodb.model.ProfileCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author arun on 8/5/20
 */

@Repository
public class ProfileCounterDaoImpl implements ProfileCounterDao {
    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public ProfileCounterDaoImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }


    /**
     * Save/update the ProfileCounter details to the ProfileCounter
     *
     * @param profileCounter - List of Profile Counter for a particular ActorId
     */
    @Override
    public void save(ProfileCounter profileCounter) {
        dynamoDBMapper.save(profileCounter);
    }

    /**
     * Get the profileCounter details based on the
     *
     * @param actorId - based on the actorId details of the Profile is to be retrieved
     * @return - List of ProfileCounter, here there should be 2 records, one for 24hrs and the other for 30days
     */
    @Override
    public List<ProfileCounter> getProfileDetails(String actorId) {
        ProfileCounter profileCounter = new ProfileCounter();
        profileCounter.setActorId(actorId);
        DynamoDBQueryExpression<ProfileCounter> dynamoDBQueryExpression = new DynamoDBQueryExpression<>();
        dynamoDBQueryExpression.withHashKeyValues(profileCounter);

        return dynamoDBMapper.query(ProfileCounter.class, dynamoDBQueryExpression);
    }
}
