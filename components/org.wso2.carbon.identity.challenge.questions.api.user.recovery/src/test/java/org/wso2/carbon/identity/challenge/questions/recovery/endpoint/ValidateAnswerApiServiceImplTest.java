/*
 * Copyright (c) 2017, WSO2 LLC. (https://www.wso2.com).
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

package org.wso2.carbon.identity.challenge.questions.recovery.endpoint;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.challenge.questions.recovery.endpoint.dto.AnswerVerificationRequestDTO;
import org.wso2.carbon.identity.challenge.questions.recovery.endpoint.dto.SecurityAnswerDTO;
import org.wso2.carbon.identity.challenge.questions.recovery.endpoint.impl.ValidateAnswerApiServiceImpl;
import org.wso2.carbon.identity.challenge.questions.recovery.model.UserChallengeAnswer;
import org.wso2.carbon.identity.challenge.questions.recovery.password.SecurityQuestionPasswordRecoveryManager;
import org.wso2.carbon.identity.challenge.questions.recovery.endpoint.Utils.Utils;
import org.wso2.carbon.identity.recovery.IdentityRecoveryClientException;
import org.wso2.carbon.identity.recovery.IdentityRecoveryConstants;
import org.wso2.carbon.identity.recovery.IdentityRecoveryException;
import org.wso2.carbon.identity.recovery.endpoint.Utils.RecoveryUtil;
import org.wso2.carbon.identity.recovery.endpoint.dto.PropertyDTO;
import org.wso2.carbon.identity.recovery.model.Property;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.testng.Assert.assertEquals;

/**
 * This class contains unit tests for ValidateAnswerApiServiceImpl.java
 */
public class ValidateAnswerApiServiceImplTest {

    @Mock
    SecurityQuestionPasswordRecoveryManager securityQuestionPasswordRecoveryManager;

    @InjectMocks
    ValidateAnswerApiServiceImpl validateAnswerApiService;

    private AnswerVerificationRequestDTO buildAnswerVerificationRequestDTO() {

        AnswerVerificationRequestDTO answerVerificationRequestDTO = new AnswerVerificationRequestDTO();
        answerVerificationRequestDTO.setKey("DummyKey");
        answerVerificationRequestDTO.setAnswers(buildsecurityAnswerDTOList());
        answerVerificationRequestDTO.setProperties(buildPropertyListDTO());
        return answerVerificationRequestDTO;
    }

    private List<SecurityAnswerDTO> buildsecurityAnswerDTOList() {

        SecurityAnswerDTO securityAnswerDTO = new SecurityAnswerDTO();
        List<SecurityAnswerDTO> securityAnswerDTOList = new ArrayList<>();
        securityAnswerDTO.setQuestionSetId("DummyId");
        securityAnswerDTO.setAnswer("Dummy answer");
        securityAnswerDTOList.add(securityAnswerDTO);
        return securityAnswerDTOList;
    }

    private List<PropertyDTO> buildPropertyListDTO() {

        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setKey("DummyPropertyKey");
        propertyDTO.setValue("Dummy property value");
        List<PropertyDTO> propertyDTOList = new ArrayList<>();
        propertyDTOList.add(propertyDTO);
        return propertyDTOList;
    }

    private MockedStatic<RecoveryUtil> mockedRecoveryUtil;
    private MockedStatic<Utils> mockedUtils;

    @BeforeMethod
    public void setUp() throws IdentityRecoveryException {

        MockitoAnnotations.openMocks(this);
        mockedRecoveryUtil = Mockito.mockStatic(RecoveryUtil.class);
        mockedUtils = Mockito.mockStatic(Utils.class);
        securityQuestionPasswordRecoveryManager = Mockito.mock(SecurityQuestionPasswordRecoveryManager.class);
    }

    @AfterMethod
    public void tearDown() {

        mockedRecoveryUtil.close();
        mockedUtils.close();
    }

    @Test
    public void testValidateAnswerPost() {

        mockedUtils.when(Utils::getSecurityQuestionBasedPwdRecoveryManager).thenReturn(
                securityQuestionPasswordRecoveryManager);
        UserChallengeAnswer[] userChallengeAnswers = new UserChallengeAnswer[1];
        Property[] properties = new Property[1];
        mockedUtils.when(() -> Utils.getUserChallengeAnswers(buildsecurityAnswerDTOList())).thenReturn(
                userChallengeAnswers);
        mockedRecoveryUtil.when(() -> RecoveryUtil.getProperties(buildPropertyListDTO())).thenReturn(properties);
        assertEquals(validateAnswerApiService.validateAnswerPost(buildAnswerVerificationRequestDTO()).getStatus(), 200);
    }

    @Test
    public void testIdentityRecoveryClientExceptionwithCodeforValidateAnswerPost() throws IdentityRecoveryException {

        mockedUtils.when(Utils::getSecurityQuestionBasedPwdRecoveryManager).thenReturn(
                securityQuestionPasswordRecoveryManager);
        Mockito.when(securityQuestionPasswordRecoveryManager.validateUserChallengeQuestions
                (isNull(), anyString(), isNull())).thenThrow
                (new IdentityRecoveryClientException(IdentityRecoveryConstants.ErrorMessages.
                        ERROR_CODE_INVALID_ANSWER_FOR_SECURITY_QUESTION.getCode(), ""));
        assertEquals(validateAnswerApiService.validateAnswerPost(buildAnswerVerificationRequestDTO()).getStatus(), 412);
    }

    @Test
    public void testIdentityRecoveryClientExceptionforValidateAnswerPost() throws IdentityRecoveryException {

        mockedUtils.when(Utils::getSecurityQuestionBasedPwdRecoveryManager).thenReturn(
                securityQuestionPasswordRecoveryManager);
        Mockito.when(securityQuestionPasswordRecoveryManager.validateUserChallengeQuestions
                (any(UserChallengeAnswer[].class), anyString(), any(Property[].class))).thenThrow
                (new IdentityRecoveryClientException(IdentityRecoveryConstants.ErrorMessages.
                        ERROR_CODE_INVALID_ANSWER_FOR_SECURITY_QUESTION.toString(), ""));
        assertEquals(validateAnswerApiService.validateAnswerPost(buildAnswerVerificationRequestDTO()).getStatus(), 200);
    }

    @Test
    public void testIdentityRecoveryExceptionforValidateAnswerPost() throws IdentityRecoveryException {

        mockedUtils.when(Utils::getSecurityQuestionBasedPwdRecoveryManager).thenReturn(
                securityQuestionPasswordRecoveryManager);
        Mockito.when(securityQuestionPasswordRecoveryManager.validateUserChallengeQuestions
                (any(UserChallengeAnswer[].class), anyString(), any(Property[].class))).thenThrow
                (new IdentityRecoveryException(""));
        assertEquals(validateAnswerApiService.validateAnswerPost(buildAnswerVerificationRequestDTO()).getStatus(), 200);
    }

    @Test
    public void testThrowableforValidateAnswerPost() throws IdentityRecoveryException {

        mockedUtils.when(Utils::getSecurityQuestionBasedPwdRecoveryManager).thenReturn(
                securityQuestionPasswordRecoveryManager);
        assertEquals(validateAnswerApiService.validateAnswerPost(null).getStatus(), 200);
    }
}
