package com.secure_srm.web.controllers;

import com.secure_srm.model.people.SubjectClassList;
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
public class SubjectClassListController_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void getSubjectClassList(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjectClassList/index").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/subjectClasses"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subjectClasses"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void getSubjectClass(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjectClassList/1").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/subjectClass"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subjectClass"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getNewSubjectClass(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjectClassList/new").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/newSubjectClass"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subjectClass"))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getNewSubjectClass_search(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjectClassList/new/search").with(httpBasic(username, pwd)).with(csrf())
                .param("TeacherLastName", "Jones")
                .param("SubjectTitle", ""))
                .andExpect(view().name("/SRM/classLists/newSubjectClass"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subjectClass"))
                .andExpect(model().attribute("subjects", hasSize(2)))
                .andExpect(model().attribute("teachers", hasSize(1)))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postNewSubjectClass(String username, String pwd) throws Exception {
        //submit a new teacher of Mathematics (ID 1)
        TeacherUser subjectTeacher = TeacherUser.builder().firstName("first").lastName("last").build();
        SubjectClassList subjectClassList = SubjectClassList.builder().subject(subjectService.findById(1L)).teacher(subjectTeacher).studentList(studentService.findAll()).build();

        mockMvc.perform(post("/subjectClassList/new").with(httpBasic(username, pwd)).with(csrf())
                .param("groupName", "Group Z987")
                .flashAttr("subjectClass", subjectClassList))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/classLists/subjectClass"))
                .andExpect(model().attributeExists("subjectClass"))
                .andExpect(model().attributeExists("newList"))
                .andExpect(model().attributeExists("studentList"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateClassList(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjectClassList/1/edit").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/classLists/studentsOnFile_subject"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subjectClass"))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateSubjectClass_search(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjectClassList/1/search").with(httpBasic(username, pwd))
                .param("StudentLastName", ""))
                .andExpect(view().name("/SRM/classLists/studentsOnFile_subject"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subjectClass"))
                .andExpect(model().attributeExists("studentSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateClassList(String username, String pwd) throws Exception {
        mockMvc.perform(post("/subjectClassList/1/edit").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("subjectClass", subjectClassListService.findById(1L)))
                .andExpect(view().name("/SRM/classLists/subjectClass"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subjectClass"))
                .andExpect(model().attributeExists("studentList"))
                .andExpect(model().attributeExists("newList"));
    }
}
