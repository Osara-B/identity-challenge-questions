/*
 * Copyright (c) 2014-2025, WSO2 LLC. (https://www.wso2.com).
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

package org.wso2.carbon.identity.challenge.questions.recovery.model;

import java.util.Objects;

/**
 * encapsulates challenge questions data
 */
public class ChallengeQuestion {

    private String question;

    private String questionId;

    private String questionSetId;

    private String locale;

    public ChallengeQuestion() {
        //default constructor
    }

    public ChallengeQuestion(String questionSetId, String question) {
        this.question = question;
        this.questionSetId = questionSetId;
    }

    public ChallengeQuestion(String questionSetId, String questionId, String question, String locale) {
        this.questionSetId = questionSetId;
        this.questionId = questionId;
        this.question = question;
        this.locale = locale;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionSetId() {
        return questionSetId;
    }

    public void setQuestionSetId(String questionSetId) {
        this.questionSetId = questionSetId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChallengeQuestion that = (ChallengeQuestion) o;
        return Objects.equals(questionId, that.questionId) && Objects.equals(questionSetId, that.questionSetId) &&
                Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {

        return Objects.hash(questionId, questionSetId, locale);
    }
}
