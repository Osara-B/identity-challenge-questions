/*
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
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

package org.wso2.carbon.identity.rest.api.server.challenge.v1.core.factories;

import org.wso2.carbon.identity.api.server.challenge.common.ChallengeQuestionDataHolder;
import org.wso2.carbon.identity.challenge.questions.recovery.ChallengeQuestionManager;
import org.wso2.carbon.identity.rest.api.server.challenge.v1.core.ServerChallengeService;

public class ServerChallengeServiceFactory {

    private static final ServerChallengeService SERVICE;

    static {

        ChallengeQuestionManager challengeQuestionManager = ChallengeQuestionDataHolder.getChallengeQuestionManager();
        if (challengeQuestionManager == null) {
            throw new IllegalStateException("ChallengeQuestionManager not available in the OSGi context.");
        }
        SERVICE = new ServerChallengeService(challengeQuestionManager);
    }

    /**
     * Get ServerChallengeService.
     *
     * @return ServerChallengeService.
     */
    public static ServerChallengeService getServerChallengeService() {

        return SERVICE;
    }
}
