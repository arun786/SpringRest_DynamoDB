package com.arun.springrestdynamodb.controller;

import com.arun.springrestdynamodb.dynamodb.model.ProfileCounter;
import com.arun.springrestdynamodb.dynamodb.model.ProfileDynamoDBResponse;
import com.arun.springrestdynamodb.dynamodb.service.ProfileCounterService;
import com.arun.springrestdynamodb.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author arun on 8/5/20
 */

@RestController
public class ProfileController {

    private final ProfileCounterService profileCounterService;

    @Autowired
    public ProfileController(ProfileCounterService profileCounterService) {
        this.profileCounterService = profileCounterService;
    }

    @PostMapping("/v1/profile/{actorId}")
    public ResponseEntity<List<ProfileCounter>> getProfileCounter(@PathVariable String actorId, @RequestBody List<Profile> profiles) {

        int countOfTokenRequested = profiles.size();

        ProfileDynamoDBResponse profileDynamoDBResponse = profileCounterService.checkActorEligibleToGetActiveTokens(actorId, countOfTokenRequested);
        //call to dmp, get back the request
        profileCounterService.updateActorCountDetails(profileDynamoDBResponse.getProfiles(), countOfTokenRequested, actorId);

        profileDynamoDBResponse = profileCounterService.checkActorEligibleToGetActiveTokens(actorId, countOfTokenRequested);
        return ResponseEntity.ok(profileDynamoDBResponse.getProfiles());

    }


}
