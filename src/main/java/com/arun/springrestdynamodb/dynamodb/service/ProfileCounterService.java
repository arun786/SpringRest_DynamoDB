package com.arun.springrestdynamodb.dynamodb.service;

import com.arun.springrestdynamodb.dynamodb.model.ProfileCounter;
import com.arun.springrestdynamodb.dynamodb.model.ProfileDynamoDBResponse;

import java.util.List;

/**
 * @author arun on 8/5/20
 */
public interface ProfileCounterService {
    ProfileDynamoDBResponse checkActorEligibleToGetActiveTokens(String actorId, Integer countOfTokens);

    void updateActorCountDetails(List<ProfileCounter> profileCounters, Integer profileCounts, String actorId);
}
