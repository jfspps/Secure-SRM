package com.secure_srm.web.controllers;

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
public class SubjectControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listSubjects(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/subjectIndex"))
                .andExpect(model().attribute("subjects", hasSize(2)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void searchSubjects_updateSubject(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects").with(httpBasic(username, pwd)).with(csrf())
                .param("subjectTitle", "english"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/subjectIndex"))
                .andExpect(model().attribute("subjects", hasSize(1)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void searchSubjectsPartial(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects").with(httpBasic(username, pwd)).with(csrf())
                .param("subjectTitle", "math"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/subjectIndex"))
                .andExpect(model().attribute("subjects", hasSize(1)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getNewSubject_updateSubject(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects/new").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/newSubject"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attribute("teachers", hasSize(2)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void searchTeachers_newSubject(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects/new/teachers/search").with(httpBasic(username, pwd)).with(csrf())
                .param("TeacherLastName", "jones"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/newSubject"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attribute("teachers", hasSize(1)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postNewSubject(String username, String pwd) throws Exception {
        mockMvc.perform(post("/subjects/new").with(httpBasic(username, pwd)).with(csrf())
                .param("subjectName", "new subject")
                .flashAttr("teachers", teacherUserService.findAll()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/updateSubject"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attributeExists("subjectTeachersFeedback"))
                .andExpect(model().attribute("teachers", hasSize(2)));
    }

    //subject 1 is Mathematics, subject 2 is English
    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void getUpdateSubject(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects/1").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/updateSubject"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attribute("teachers", hasSize(1)));
    }

    //subject 1 is Mathematics, subject 2 is English
    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void searchTeachers_updateSubject_None(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects/1/teachers/search").with(httpBasic(username, pwd)).with(csrf())
                .param("TeacherLastName", "manning"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/updateSubject"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attribute("teachers", hasSize(0)));
    }

    //subject 1 is Mathematics, subject 2 is English
    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void searchTeachers_updateSubject(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects/2/teachers/search").with(httpBasic(username, pwd)).with(csrf())
                .param("TeacherLastName", "manning"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/updateSubject"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attribute("teachers", hasSize(0)));
    }

    //subject 1 is Mathematics, subject 2 is English
    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateSubject(String username, String pwd) throws Exception {
        mockMvc.perform(post("/subjects/1/teachers").with(httpBasic(username, pwd)).with(csrf())
                .param("subjectName", "Mathematics")
                .param("TeacherLastName", ""))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/subjects/updateSubject"))
                .andExpect(model().attributeExists("subjectTeachersFeedback"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attribute("teachers", hasSize(0)));
    }

//    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
//    @ParameterizedTest
//    void postUpdateSubject_BlankSubjectTitle(String username, String pwd) throws Exception {
//        mockMvc.perform(post("/subjects/1/teachers").with(httpBasic(username, pwd)).with(csrf())
//                .param("subjectName", "")
//                .param("TeacherLastName", ""))
//                .andExpect(status().is(200))
//                .andExpect(status().isOk())
//                .andExpect(view().name("/SRM/subjects/updateSubject"))
//                .andExpect(model().attributeExists("subject"))
//                .andExpect(model().attribute("teachers", hasSize(2)));
//    }
}
