package com.arun.springrestdynamodb.dynamodb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author arun on 8/5/20
 */

@Getter
@Setter
public class ProfileDynamoDBResponse {
    private List<ProfileCounter> profiles;
    private boolean isEligible;
}
