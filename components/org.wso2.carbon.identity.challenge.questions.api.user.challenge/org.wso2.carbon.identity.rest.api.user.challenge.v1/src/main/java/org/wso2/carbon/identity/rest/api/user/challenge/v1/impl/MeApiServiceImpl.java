/*
 * Copyright (c) 2019, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.rest.api.user.challenge.v1.impl;

import org.wso2.carbon.identity.rest.api.user.challenge.v1.MeApiService;
import org.wso2.carbon.identity.rest.api.user.challenge.v1.core.UserChallengeService;
import org.wso2.carbon.identity.rest.api.user.challenge.v1.dto.ChallengeAnswerDTO;
import org.wso2.carbon.identity.rest.api.user.challenge.v1.dto.UserChallengeAnswerDTO;
import org.wso2.carbon.identity.rest.api.user.challenge.v1.factories.UserChallengeServiceFactory;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.api.user.challenge.common.Constant.ME_CONTEXT;
import static org.wso2.carbon.identity.api.user.challenge.common.Constant.USER_CHALLENGE_ANSWERS_PATH_COMPONENT;
import static org.wso2.carbon.identity.api.user.challenge.common.Constant.V1_API_PATH_COMPONENT;
import static org.wso2.carbon.identity.api.user.common.ContextLoader.buildURIForHeader;
import static org.wso2.carbon.identity.api.user.common.ContextLoader.getUserFromContext;

/**
 * API service implementation for authenticated user's challenge operations
 */
public class MeApiServiceImpl extends MeApiService {

    private final UserChallengeService challengeService;

    public MeApiServiceImpl() {

        try {
            challengeService = UserChallengeServiceFactory.getUserChallengeService();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while initiating challenge service.", e);
        }
    }

    @Override
    public Response addChallengeAnswerOfLoggedInUser(String challengeSetId, UserChallengeAnswerDTO
            challengeAnswer) {

        challengeService.addChallengeAnswerOfUser(getUserFromContext(), challengeSetId, challengeAnswer);
        return Response.created(getMeChallengeAnswersLocation()).build();
    }

    @Override
    public Response addChallengeAnswersForLoggedInUser(List<ChallengeAnswerDTO> challengeAnswer) {

        challengeService.setChallengeAnswersOfUser(getUserFromContext(), challengeAnswer);
        return Response.created(getMeChallengeAnswersLocation()).build();
    }

    @Override
    public Response deleteChallengeAnswerOfLoggedInUser(String challengeSetId) {

        challengeService.removeChallengeAnswerOfUser(getUserFromContext(), challengeSetId);
        return Response.noContent().build();
    }

    @Override
    public Response deleteChallengeAnswersOfLoggedInUser() {

        challengeService.removeChallengeAnswersOfUser(getUserFromContext());
        return Response.noContent().build();
    }

    @Override
    public Response getAnsweredChallengesOfLoggedInUser() {

        return Response.ok().entity(challengeService.getChallengeAnswersOfUser(getUserFromContext())).build();
    }

    @Override
    public Response getChallengesForLoggedInUser(Integer offset, Integer limit) {

        return Response.ok().entity(challengeService.getChallengesForUser(getUserFromContext(), offset, limit)).build();
    }

    @Override
    public Response updateChallengeAnswerOfLoggedInUser(String challengeSetId, UserChallengeAnswerDTO
            challengeAnswer) {

        challengeService.updateChallengeAnswerOfUser(getUserFromContext(), challengeSetId, challengeAnswer);
        return Response.ok().build();
    }

    @Override
    public Response updateChallengeAnswersOfLoggedInUser(List<ChallengeAnswerDTO> challengeAnswers) {

        challengeService.updateChallengeAnswersOfUser(getUserFromContext(), challengeAnswers);
        return Response.ok().build();
    }


    private URI getMeChallengeAnswersLocation() {
        return buildURIForHeader(String.format(V1_API_PATH_COMPONENT + USER_CHALLENGE_ANSWERS_PATH_COMPONENT,
                ME_CONTEXT));
    }
}
