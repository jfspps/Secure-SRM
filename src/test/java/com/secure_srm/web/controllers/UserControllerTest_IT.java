package com.secure_srm.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//USER <--> ROLE <--> AUTHORITY

// Cross-site request forgery, also known as session riding (sometimes pronounced sea-surf) or XSRF, is a type of
// malicious exploit of a website where unauthorized commands are submitted from a user that the web application trusts.
// POST requests, with csrf enabled, will be denied (HTTP 403) in the browser but likely pass in Spring MVC tests
// (tests bypass Spring security); if POST fails in the browser, add:
// <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
// in the given form where other <input> tags are declared (the above token is added with the requisite info to the server)

// more info here: https://portswigger.net/web-security/csrf/tokens

@Slf4j
@Transactional
@SpringBootTest
//substitute @WebMvcTest for @SpringBootTest to guarantee tests capture all Spring Boot dependencies which were loaded at the time
class UserControllerTest_IT extends SecurityCredentialsTest {

    @WithAnonymousUser
    @Test
    void welcomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/SRM_home"));
    }

    //this fails with Spring Security with any username ('random' is effectively replaced with anyString())
    @WithMockUser("random")
    @Test
    void loginPage_random() throws Exception {
        mockMvc.perform(get("/accountSettings"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void loginAuthHttpBasicFAIL() throws Exception {
        MvcResult unauthenticatedResult = mockMvc.perform(get("/accountSettings").with(httpBasic("randomPerson", "xyz")))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //not printing anything it seems...
        System.out.println("loginAuthHttpBasicFAIL(), redirected URL: " + unauthenticatedResult.getResponse().getContentAsString());
    }

    @Test
    void logoutPage() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is2xxSuccessful());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamAllUsers")
    @ParameterizedTest
    void redirectToLoginWhenRequestingAccountSettings_AllUsers(String username, String pwd) throws Exception {
        //see https://www.baeldung.com/spring-security-redirect-login

        MockHttpServletRequestBuilder securedResourceAccess = get("/accountSettings");

        //gather what happens when accessing /accountSettings as an anonymous user
        MvcResult unauthenticatedResult = mockMvc
                .perform(securedResourceAccess)
                .andExpect(status().is4xxClientError())
                .andReturn();

        //retrieve any session data
        MockHttpSession session = (MockHttpSession) unauthenticatedResult
                .getRequest()
                .getSession();

        //post login data under same session
        mockMvc
                .perform(post("/login")
                        .param("username", username)
                        .param("password", pwd)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/accountSettings"))
                .andReturn();

        //verify that the user session enables future access without re-logging in
        mockMvc
                .perform(securedResourceAccess.session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userID"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamAllUsers")
    @ParameterizedTest
    void loginAuthHttpBasic_AllUsers_AccountSettings(String username, String pwd) throws Exception {
        mockMvc.perform(get("/accountSettings").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("accountSettings"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userID"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void adminPagePASS_withAdmin(String username, String pwd) throws Exception {
        mockMvc.perform(get("/adminPage").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("adminPage"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userID"))
                .andExpect(model().attributeExists("AdminUsersFound"));
    }

    //same test as above with new annotation (username and pwd are pulled from JPAUserDetails)
    @Test
    @WithUserDetails("johnsmith")
    void adminPagePASS_withAdmin_withoutHttpBasic() throws Exception {
        mockMvc.perform(get("/adminPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminPage"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userID"))
                .andExpect(model().attributeExists("AdminUsersFound"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void adminPageFAIL_withNonAdmin(String username, String pwd) throws Exception {
        mockMvc.perform(get("/adminPage").with(httpBasic(username, pwd)))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postResetPassword_ADMIN(String username, String pwd) throws Exception {
        mockMvc.perform(post("/resetPassword/1").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("adminUpdate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("confirmReset"))
                .andExpect(model().attributeExists("currentAdminUser"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postResetPassword_TEACHER(String username, String pwd) throws Exception {
        mockMvc.perform(post("/resetPassword/3").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/SRM/teachers/updateTeacher"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("confirmReset"))
                .andExpect(model().attributeExists("teacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postResetPassword_GUARDIAN(String username, String pwd) throws Exception {
        mockMvc.perform(post("/resetPassword/5").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/SRM/guardians/updateGuardian"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("confirmReset"))
                .andExpect(model().attributeExists("guardian"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postChangePassword_ADMIN(String username, String pwd) throws Exception {
        mockMvc.perform(post("/changePassword/1").with(httpBasic(username, pwd)).with(csrf())
                .param("password", "johnsmith12345678"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("adminPage"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void postChangePassword_ADMIN_UnAuth(String username, String pwd) throws Exception {
        mockMvc.perform(post("/changePassword/1").with(httpBasic(username, pwd)).with(csrf())
                .param("password", "johnsmith12345678"))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postChangePassword_outOfBounds(String username, String pwd) throws Exception {
        mockMvc.perform(post("/changePassword/5000").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamAllUsers")
    @ParameterizedTest
    void getChangePassword(String username, String pwd) throws Exception {
        mockMvc.perform(get("/changePassword").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("changePassword"))
                .andExpect(model().attributeExists("userID"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamAllUsers")
    @ParameterizedTest
    void postChangePassword(String username, String pwd) throws Exception {
        mockMvc.perform(post("/changePassword").with(httpBasic(username, pwd)).with(csrf())
                .param("newPassword", "hfh8fh09fhsdfkjw3"))
                .andExpect(status().isOk())
                .andExpect(view().name("changePassword"))
                .andExpect(model().attributeExists("userID"))
                .andExpect(model().attribute("pwdFeedback", "Password changed and saved"));
    }
}