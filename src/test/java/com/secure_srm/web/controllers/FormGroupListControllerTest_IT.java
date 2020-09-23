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

@Slf4j
@Transactional
@SpringBootTest
class FormGroupListControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void getFormGroupList(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/index").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/formGroups"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("formGroups"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void getShowFormGroup(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/1").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("formGroup"))
                .andExpect(model().attributeExists("studentList"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void getShowFormGroupNOTFOUND(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/123432").with(httpBasic(username, pwd)))
                .andExpect(status().isNotFound());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateFormGroup(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/1/edit").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/studentsOnFile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("formGroup"))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateFormGroupNOTFOUND(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/132426/edit").with(httpBasic(username, pwd)))
                .andExpect(status().isNotFound());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateFormGroupSearch(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/1/search").with(httpBasic(username, pwd))
                .param("StudentLastName", ""))
                .andExpect(view().name("/SRM/classLists/studentsOnFile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("formGroup"))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateFormGroup(String username, String pwd) throws Exception {
        mockMvc.perform(post("/formGroupList/1/edit").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("formGroup", formGroupListService.findById(1L)))
                .andExpect(view().name("/SRM/classLists/form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("formGroup"))
                .andExpect(model().attributeExists("studentList"))
                .andExpect(model().attributeExists("newList"));
    }
}