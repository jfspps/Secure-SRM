package com.secure_srm.web.controllers;

import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.security.TeacherUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
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
    void getNewFormGroup(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/new").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/newFormGroup"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("formGroup"))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getNewFormGroup_findTeachers(String username, String pwd) throws Exception {
        mockMvc.perform(get("/formGroupList/new").with(httpBasic(username, pwd))
                .param("TeacherLastName", "Jones"))
                .andExpect(view().name("/SRM/classLists/newFormGroup"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("formGroup"))
                .andExpect(model().attribute("teachers", hasSize(1)))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postNewFormGroup(String username, String pwd) throws Exception {
        TeacherUser tutor = TeacherUser.builder().firstName("first").lastName("last").build();
        FormGroupList formGroupList = FormGroupList.builder().teacher(tutor).studentList(studentService.findAll()).build();

        mockMvc.perform(post("/formGroupList/new").with(httpBasic(username, pwd)).with(csrf())
                .param("groupName", "Group A123")
                .flashAttr("formGroup", formGroupList))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/classLists/form"))
                .andExpect(model().attributeExists("formGroup"))
                .andExpect(model().attributeExists("newList"))
                .andExpect(model().attributeExists("studentList"));
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