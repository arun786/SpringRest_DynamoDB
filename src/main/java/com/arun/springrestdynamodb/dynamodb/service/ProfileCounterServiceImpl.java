package com.arun.springrestdynamodb.dynamodb.service;

import com.arun.springrestdynamodb.dynamodb.config.TimeToLiveConfig;
import com.arun.springrestdynamodb.dynamodb.dao.ProfileCounterDao;
import com.arun.springrestdynamodb.dynamodb.model.ProfileCounter;
import com.arun.springrestdynamodb.dynamodb.model.ProfileDynamoDBResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author arun on 8/5/20
 */
@Service
@Slf4j
public class ProfileCounterServiceImpl implements ProfileCounterService {
    private final ProfileCounterDao profileCounterDao;
    private final TimeToLiveConfig timeToLiveConfig;

    @Autowired
    public ProfileCounterServiceImpl(ProfileCounterDao profileCounterDao, TimeToLiveConfig timeToLiveConfig) {
        this.profileCounterDao = profileCounterDao;
        this.timeToLiveConfig = timeToLiveConfig;
    }

    @Override
    public ProfileDynamoDBResponse checkActorEligibleToGetActiveTokens(String actorId, Integer countOfTokens) {

        List<ProfileCounter> profileDetails = profileCounterDao.getProfileDetails(actorId);
        for (ProfileCounter profileDetail : profileDetails) {
            log.info("Details of Actor {}", profileDetail);
        }

        //todo
        //logic to check if actor is eligible for 24hrs and for 30 days

        ProfileDynamoDBResponse profileDynamoDBResponse = new ProfileDynamoDBResponse();
        profileDynamoDBResponse.setEligible(true);
        profileDynamoDBResponse.setProfiles(profileDetails);
        return profileDynamoDBResponse;
    }

    /**
     * The below method will create a profile counter for the actor if there are no records
     * <p>
     * or will overwrite the records if present
     *
     * @param profileCounters - list of profile counters which are to be updated for the actor
     * @param profileCounts   - number of profiles requested for the actor
     * @param actorId
     */
    @Override
    public void updateActorCountDetails(List<ProfileCounter> profileCounters, Integer profileCounts, String actorId) {
        long currentEpochTime = System.currentTimeMillis() / 1000L;
        if (profileCounters.size() == 0) {

            //update table
            ProfileCounter profileCounterFor24Hr = new ProfileCounter();
            profileCounterFor24Hr.setActorId(actorId);
            profileCounterFor24Hr.setCount(profileCounts);
            profileCounterFor24Hr.setDuration("for24Hrs");
            long for24Hrs = Long.parseLong(timeToLiveConfig.getFor24Hrs());
            profileCounterFor24Hr.setTtl(currentEpochTime + for24Hrs);

            ProfileCounter profileCounterFor30Days = new ProfileCounter();
            profileCounterFor30Days.setActorId(actorId);
            profileCounterFor30Days.setCount(profileCounts);
            profileCounterFor30Days.setDuration("for30Days");
            long for30Days = Long.parseLong(timeToLiveConfig.getFor30Days());
            profileCounterFor30Days.setTtl(currentEpochTime + for30Days);

            profileCounterDao.save(profileCounterFor24Hr);
            profileCounterDao.save(profileCounterFor30Days);

        } else {
            profileCounters.forEach(profileCounter -> {

                String duration = profileCounter.getDuration();
                long ttl = profileCounter.getTtl();

                if (duration.equals("for24Hr")) {
                    if (ttl == 0) {
                        long for24Hrs = Long.parseLong(timeToLiveConfig.getFor24Hrs());
                        profileCounter.setTtl(currentEpochTime + for24Hrs);
                    }
                } else if (duration.equals("for30Days")) {
                    if (ttl == 0) {
                        long for30Days = Long.parseLong(timeToLiveConfig.getFor30Days());
                        profileCounter.setTtl(currentEpochTime + for30Days);
                    }
                }

                int totalCount = profileCounter.getCount() + profileCounts;
                profileCounter.setCount(totalCount);

                //save to the dynamo db table.
                profileCounterDao.save(profileCounter);
            });
        }
    }
}
