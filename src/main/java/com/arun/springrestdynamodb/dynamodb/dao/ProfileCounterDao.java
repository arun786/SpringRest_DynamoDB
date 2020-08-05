package com.arun.springrestdynamodb.dynamodb.dao;

import com.arun.springrestdynamodb.dynamodb.model.ProfileCounter;

import java.util.List;

/**
 * @author arun on 8/5/20
 */
public interface ProfileCounterDao {
    void save(ProfileCounter profileCounter);

    List<ProfileCounter> getProfileDetails(String actorId);
}
