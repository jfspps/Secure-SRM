package com.secure_srm.web.controllers;

import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.model.security.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Slf4j
@Transactional
@SpringBootTest
public class TeacherUserController_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getCreateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(get("/teachers/new").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/teachers/newTeacher"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("teacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postCreateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(post("/teachers/new").with(httpBasic(username, pwd)).with(csrf())
                .param("username", "hfasjsakldaksj")
                .param("password", "fsdfjsdlkjsd")
                .param("firstName", "saldjajskdjaskl")
                .param("lastName", "dfkasjdlkasj"))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/teachers/teacherDetails"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("userFeedback"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void viewTeacherDetails(String username, String pwd) throws Exception {
        mockMvc.perform(get("/teachers/1").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/teachers/teacherDetails"))
                .andExpect(model().attributeExists("teacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(get("/teachers/1/edit").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/teachers/updateTeacher"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("subjectsOnFile"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(post("/teachers/1/edit").with(httpBasic(username, pwd)).with(csrf())
                .param("username", "ggsdgdfssd")
                .param("password", "fsdfjsdsgsfdgdfglkjsd")
                .param("firstName", "saldjergergdfajskdjaskl")
                .param("lastName", "gsfgfdgdsfgds")
                .param("department", "kujdhafdsjfkdjh")
                .param("contactDetail.email", "jflksdjfsdkfljl")
                .param("contactDetail.phoneNumber", "23847234923784"))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/teachers/teacherDetails"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("userFeedback"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getAnonTeacher(String username, String pwd) throws Exception {
        mockMvc.perform(get("/teachers/1/anon").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/teachers/confirmAnon"))
                .andExpect(model().attributeExists("teacher"));
    }
}
