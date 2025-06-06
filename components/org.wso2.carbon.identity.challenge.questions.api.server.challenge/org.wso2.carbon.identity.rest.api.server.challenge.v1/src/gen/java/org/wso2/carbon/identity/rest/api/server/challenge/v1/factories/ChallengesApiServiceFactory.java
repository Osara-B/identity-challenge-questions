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

package org.wso2.carbon.identity.rest.api.server.challenge.v1.factories;

import org.wso2.carbon.identity.rest.api.server.challenge.v1.ChallengesApiService;
import org.wso2.carbon.identity.rest.api.server.challenge.v1.impl.ChallengesApiServiceImpl;

public class ChallengesApiServiceFactory {

   private final static ChallengesApiService SERVICE = new ChallengesApiServiceImpl();

   public static ChallengesApiService getChallengesApi() {

      return SERVICE;
   }
}
