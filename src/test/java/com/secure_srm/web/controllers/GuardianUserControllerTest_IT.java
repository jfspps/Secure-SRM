package com.secure_srm.web.controllers;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Slf4j
@Transactional
@SpringBootTest
public class GuardianUserControllerTest_IT extends SecurityCredentialsTest{

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getCreateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(get("/guardians/new").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/newGuardian"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("guardian"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postCreateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(post("/guardians/new").with(httpBasic(username, pwd)).with(csrf())
                .param("username", "hfasjsakldaksj")
                .param("password", "fsdfjsdlkjsd")
                .param("firstName", "saldjajskdjaskl")
                .param("lastName", "dfkasjdlkasj"))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/guardianDetails"))
                .andExpect(model().attributeExists("guardian"))
                .andExpect(model().attributeExists("userFeedback"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void viewGuardianDetails(String username, String pwd) throws Exception {
        mockMvc.perform(get("/guardians/1").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/guardianDetails"))
                .andExpect(model().attributeExists("guardian"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(get("/guardians/1/edit").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/updateGuardian"))
                .andExpect(model().attributeExists("guardian"))
                .andExpect(model().attributeExists("user"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(post("/guardians/1/edit").with(httpBasic(username, pwd)).with(csrf())
                .param("username", "ggsdgdfssd")
                .param("password", "fsdfjsdsgsfdgdfglkjsd")
                .param("firstName", "saldjergergdfajskdjaskl")
                .param("lastName", "gsfgfdgdsfgds")
                .param("address.firstLine", "efsd093fewofiewh")
                .param("address.secondLine", "efsd093fewofiewh")
                .param("address.postcode", "efsd093fewofiewh")
                .param("contactDetail.email", "jflksdjfsdkfljl")
                .param("contactDetail.phoneNumber", "23847234923784"))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/guardianDetails"))
                .andExpect(model().attributeExists("guardian"))
                .andExpect(model().attributeExists("userFeedback"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateStudentSet(String username, String pwd) throws Exception {
        mockMvc.perform(get("/guardians/1/addRemoveStudents").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/studentSet"))
                .andExpect(model().attributeExists("guardian"))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateStudentSet_search(String username, String pwd) throws Exception {
        mockMvc.perform(get("/guardians/1/addRemoveStudents/search").with(httpBasic(username, pwd)).with(csrf())
                .param("StudentLastName", "smith"))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/studentSet"))
                .andExpect(model().attributeExists("guardian"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateStudentSet(String username, String pwd) throws Exception {
        mockMvc.perform(post("/guardians/1/addRemoveStudents").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/guardianDetails"))
                .andExpect(model().attributeExists("guardian"))
                .andExpect(model().attributeExists("userFeedback"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getDeleteGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(get("/guardians/1/delete").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/guardians/confirmDelete"))
                .andExpect(model().attributeExists("guardian"));
    }

//    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
//    @ParameterizedTest
//    void postDeleteGuardian(String username, String pwd) throws Exception {
//        mockMvc.perform(post("/guardians/1/delete").with(httpBasic(username, pwd)).with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(view().name("/SRM/guardians/deleteConfirmed"))
//                .andExpect(model().attributeExists("reply"));
//    }
}
