package com.springsecurity.weblogin.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
@SpringBootTest
public class TeacherUserControllerTest extends UserControllerTest {

    // user and TeacherUser CRUD tests ===============================================================================
    //context loads adminUsers, teacherUsers, followed by guardianUsers
    //IDs are [1,2], [3,4] and [5,6] respectively

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getCreateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(get("/createTeacher").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherCreate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("newUser"))
                .andExpect(model().attributeExists("newTeacher"));
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void getCreateTeacher_FAIL(String username, String pwd) throws Exception {
        mockMvc.perform(get("/createTeacher").with(httpBasic(username, pwd)))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postCreateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(post("/createTeacher").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/adminPage"));
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void postCreateTeacher_FAIL(String username, String pwd) throws Exception {
        mockMvc.perform(post("/createTeacher").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(get("/updateTeacher/4").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherUpdate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("currentTeacherUser"));
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateTeacherNOTFOUND(String username, String pwd) throws Exception {
        mockMvc.perform(get("/updateTeacher/5").with(httpBasic(username, pwd)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/adminPage"));
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void getUpdateTeacher_FAIL(String username, String pwd) throws Exception {
        mockMvc.perform(get("/updateTeacher/3").with(httpBasic(username, pwd)))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateTeacher/3").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/updateTeacher/3"));
    }

    @MethodSource("com.springsecurity.weblogin.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void postUpdateTeacherFAIL(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateTeacher/3").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
