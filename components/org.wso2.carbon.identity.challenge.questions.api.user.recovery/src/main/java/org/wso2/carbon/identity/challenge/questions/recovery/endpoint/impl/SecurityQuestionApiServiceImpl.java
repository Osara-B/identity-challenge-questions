/*
 * Copyright (c) 2016, WSO2 LLC. (https://www.wso2.com).
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

package org.wso2.carbon.identity.challenge.questions.recovery.endpoint.impl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.recovery.IdentityRecoveryClientException;
import org.wso2.carbon.identity.recovery.IdentityRecoveryConstants;
import org.wso2.carbon.identity.recovery.IdentityRecoveryException;
import org.wso2.carbon.identity.challenge.questions.recovery.bean.ChallengeQuestionResponse;
import org.wso2.carbon.identity.recovery.endpoint.Constants;
import org.wso2.carbon.identity.challenge.questions.recovery.endpoint.SecurityQuestionApiService;
import org.wso2.carbon.identity.recovery.endpoint.Utils.RecoveryUtil;
import org.wso2.carbon.identity.challenge.questions.recovery.endpoint.Utils.Utils;
import org.wso2.carbon.identity.challenge.questions.recovery.endpoint.dto.InitiateQuestionResponseDTO;
import org.wso2.carbon.identity.recovery.endpoint.dto.UserDTO;
import org.wso2.carbon.identity.recovery.internal.IdentityRecoveryServiceDataHolder;
import org.wso2.carbon.identity.challenge.questions.recovery.password.SecurityQuestionPasswordRecoveryManager;
import org.wso2.carbon.user.core.UserStoreConfigConstants;
import org.wso2.carbon.user.core.service.RealmService;


import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

public class SecurityQuestionApiServiceImpl extends SecurityQuestionApiService {

    private static final Log LOG = LogFactory.getLog(SecurityQuestionApiServiceImpl.class);

    @Override
    public Response securityQuestionGet(String username, String realm, String tenantDomain) {

        if (IdentityUtil.threadLocalProperties.get().get(Constants.TENANT_NAME_FROM_CONTEXT) != null) {
            tenantDomain = (String) IdentityUtil.threadLocalProperties.get().get(Constants.TENANT_NAME_FROM_CONTEXT);
        }

        User user = new User();
        user.setUserName(username);

        if (StringUtils.isNotBlank(realm)) {
            user.setUserStoreDomain(realm);
        } else {
            user.setUserStoreDomain(UserStoreConfigConstants.PRIMARY);
        }

        if (StringUtils.isBlank(tenantDomain)) {
            user.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        } else {
            user.setTenantDomain(tenantDomain);
        }

        int tenantId = IdentityTenantUtil.getTenantId(user.getTenantDomain());

        if (StringUtils.isBlank(realm)) {
            String[] userList = RecoveryUtil.getUserList(tenantId, username);

            if (ArrayUtils.isEmpty(userList)) {
                String msg = "Unable to find an user with username: " + username + " in the system.";
                LOG.error(msg);
            } else if (userList.length == 1) {
                user.setUserStoreDomain(IdentityUtil.extractDomainFromName(userList[0]));
            } else {
                String msg = "There are multiple users with username: " + username + " in the system, " +
                        "please send the correct user-store domain along with the username.";
                LOG.error(msg);
                RecoveryUtil.handleBadRequest(msg, Constants.ERROR_CODE_MULTIPLE_USERS_MATCHING);
            }
        }

        InitiateQuestionResponseDTO initiateQuestionResponseDTO = null;

        SecurityQuestionPasswordRecoveryManager securityQuestionBasedPwdRecoveryManager =
                Utils.getSecurityQuestionBasedPwdRecoveryManager();

        try {
            ChallengeQuestionResponse challengeQuestionResponse = securityQuestionBasedPwdRecoveryManager
                    .initiateUserChallengeQuestion(user);
            initiateQuestionResponseDTO = Utils.getInitiateQuestionResponseDTO(challengeQuestionResponse);
        } catch (IdentityRecoveryClientException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Client Error while initiating password recovery flow using security questions ", e);
            }

            if (IdentityRecoveryConstants.ErrorMessages.ERROR_CODE_CHALLENGE_QUESTION_NOT_FOUND.getCode()
                    .equals(e.getErrorCode())) {
                return Response.noContent().build();
            }

            RecoveryUtil.handleBadRequest(e.getMessage(), e.getErrorCode());

        } catch (IdentityRecoveryException e) {
            RecoveryUtil.handleInternalServerError(Constants.SERVER_ERROR, e.getErrorCode(), LOG, e);

        } catch (Throwable throwable) {
            RecoveryUtil.handleInternalServerError(Constants.SERVER_ERROR, IdentityRecoveryConstants
                    .ErrorMessages.ERROR_CODE_UNEXPECTED.getCode(), LOG, throwable);
        }
        return Response.accepted(initiateQuestionResponseDTO).build();
    }
}
