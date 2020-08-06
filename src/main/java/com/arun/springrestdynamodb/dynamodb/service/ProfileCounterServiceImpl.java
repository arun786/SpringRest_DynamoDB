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

    private final String FOR24HRS = "for24Hrs";
    private final String FOR30DAYS = "for30Days";

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
     * @param actorId         - the customer of who requested the profile
     */
    @Override
    public void updateActorCountDetails(List<ProfileCounter> profileCounters, Integer profileCounts, String actorId) {
        long currentEpochTime = System.currentTimeMillis() / 1000L;

        if (profileCounters.size() == 0) {
            //when there are no records for the actorId in the table, update both 24hr and 30 days
            long for24Hrs = Long.parseLong(timeToLiveConfig.getFor24Hrs());
            ProfileCounter profileCounterFor24Hr = setProfileCounter(actorId, profileCounts, FOR24HRS, currentEpochTime + for24Hrs);

            long for30Days = Long.parseLong(timeToLiveConfig.getFor30Days());
            ProfileCounter profileCounterFor30Days = setProfileCounter(actorId, profileCounts, FOR30DAYS, currentEpochTime + for30Days);

            profileCounterDao.save(profileCounterFor24Hr);
            profileCounterDao.save(profileCounterFor30Days);

        } else if (profileCounters.size() == 1) {
            //means we have record for 30days only in the table
            ProfileCounter profileCounter = profileCounters.get(0);
            long for24Hrs = Long.parseLong(timeToLiveConfig.getFor24Hrs());
            ProfileCounter profileCounterFor24Hrs = setProfileCounter(actorId, profileCounts, FOR24HRS, currentEpochTime + for24Hrs);

            //update the 30days count to the new count which is previous count + the new token list
            int totalCount = profileCounts + profileCounter.getCount();
            profileCounter.setCount(totalCount);

            profileCounterDao.save(profileCounterFor24Hrs);
            profileCounterDao.save(profileCounter);
        } else {
            profileCounters.forEach(profileCounter -> {
                //in this case both the 24 hrs record is present and
                //30 days record is present and we need to update the total count
                int totalCount = profileCounter.getCount() + profileCounts;
                profileCounter.setCount(totalCount);
                //save to the dynamo db table.
                profileCounterDao.save(profileCounter);
            });
        }
    }

    /**
     * @param actorId       - client
     * @param profileCounts - total number of tokens requested, including one present in the table
     * @param duration      - it can be 24hrs or 30days
     * @param ttl           - the time to live
     * @return - returns the object which can be saved in the table
     */
    private ProfileCounter setProfileCounter(String actorId, Integer profileCounts, String duration, long ttl) {
        ProfileCounter profileCounter = new ProfileCounter();
        profileCounter.setActorId(actorId);
        profileCounter.setCount(profileCounts);
        profileCounter.setDuration(duration);
        profileCounter.setTtl(ttl);
        return profileCounter;
    }
}
