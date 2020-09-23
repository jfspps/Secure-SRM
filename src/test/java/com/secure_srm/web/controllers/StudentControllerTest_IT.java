package com.secure_srm.web.controllers;

import com.secure_srm.model.people.Student;
import com.secure_srm.services.peopleServices.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@SpringBootTest
class StudentControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listStudents(String username, String pwd) throws Exception {
        mockMvc.perform(get("/students").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentIndex"))
                .andExpect(model().attribute("students", hasSize(3)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listStudentsIndex(String username, String pwd) throws Exception {
        mockMvc.perform(get("/students/index").with(httpBasic(username, pwd)))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentIndex"))
                .andExpect(model().attribute("students", hasSize(3)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    public void testGetStudentNotFound(String username, String pwd) throws Exception {
        mockMvc.perform(get("/students/234").with(httpBasic(username, pwd)))
                .andExpect(status().isNotFound());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void numberFormatStudentFindByIdTest(String username, String pwd) throws Exception{
        mockMvc.perform(get("/students/ITitURREhcjc").with(httpBasic(username, pwd)))
                .andExpect(status().isNotFound());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void showStudentById(String username, String pwd) throws Exception{
        mockMvc.perform(get("/students/1").with(httpBasic(username, pwd)))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentDetails"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("guardians"))
                .andExpect(model().attributeExists("contactDetail"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("formGroupList"))
                .andExpect(model().attributeExists("subjectClassLists"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void showStudentByIdCheckFirstName(String username, String pwd) throws Exception{
        mockMvc.perform(get("/students/1").with(httpBasic(username, pwd)))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentDetails"))
                .andExpect(model().attribute("student",
                        hasProperty("firstName", Matchers.is("John"))));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getNewStudent(String username, String pwd) throws Exception {
        mockMvc.perform(get("/students/new").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/newStudent"))
                .andExpect(model().attributeExists("student"));
    }

    //choose one admin User since successive adminUsers will post different studentIDs in the same service
    //next available new Student ID in the context is 4L
    @WithUserDetails("amysmith")
    @Test
    void postNewStudentSuccess() throws Exception {
        mockMvc.perform(post("/students/new").with(csrf())
                .param("firstName", "some name")
                .param("lastName", "some other name")
                .param("contactDetail.email", "")
                .param("contactDetail.phoneNumber", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/students/4/edit"));

        assertEquals("some name", studentService.findById(4L).getFirstName());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postNewStudentBlankFirstName(String username, String pwd) throws Exception {
        mockMvc.perform(post("/students/new").with(httpBasic(username, pwd)).with(csrf())
                .param("firstName", "")
                .param("lastName", "some other name"))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/newStudent"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postNewStudentBlankLastName(String username, String pwd) throws Exception {
        mockMvc.perform(post("/students/new").with(httpBasic(username, pwd)).with(csrf())
                .param("firstName", "some other name")
                .param("lastName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/newStudent"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postNewStudentAlreadyExists(String username, String pwd) throws Exception {
        mockMvc.perform(post("/students/new").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("student", studentService.findById(1L))
                .param("firstName", "John")
                .param("lastName", "Smith")
                .param("contactDetail.email", "")
                .param("contactDetail.phoneNumber", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentDetails"))
                .andExpect(model().attributeExists("newStudent"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateStudent(String username, String pwd) throws Exception {
        mockMvc.perform(get("/students/3/edit").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/updateStudent"))
                .andExpect(model().attributeExists("student"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateStudentNOTFOUND(String username, String pwd) throws Exception {
        mockMvc.perform(get("/students/342/edit").with(httpBasic(username, pwd)))
                .andExpect(status().isNotFound());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateStudentFormSuccess(String username, String pwd) throws Exception {
        mockMvc.perform(post("/students/1/edit").with(httpBasic(username, pwd)).with(csrf())
                .param("firstName", "some name")
                .param("lastName", "some other name")
                .param("contactDetail.email", "")
                .param("contactDetail.phoneNumber", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentDetails"))
                .andExpect(model().attributeExists("newStudent"));

        assertEquals("some other name", studentService.findById(1L).getLastName());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateStudentFormBlankNames(String username, String pwd) throws Exception {
        mockMvc.perform(post("/students/1/edit").with(httpBasic(username, pwd)).with(csrf())
                .param("firstName", "")
                .param("lastName", "")
                .param("contactDetail.email", "")
                .param("contactDetail.phoneNumber", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/updateStudent"))
                .andExpect(model().attributeExists("student"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateGuardians(String username, String pwd) throws Exception{
        mockMvc.perform(get("/students/3/addRemoveGuardians").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/guardianSet"))
                .andExpect(model().attributeExists("guardianSet"))
                .andExpect(model().attributeExists("student"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateGuardians(String username, String pwd) throws Exception{
        mockMvc.perform(post("/students/3/addRemoveGuardians").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentDetails"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("guardians"))
                .andExpect(model().attributeExists("contactDetail"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("formGroupList"))
                .andExpect(model().attributeExists("subjectClassLists"))
                .andExpect(model().attributeExists("newStudent"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getRefineGuardianList(String username, String pwd) throws Exception {
        mockMvc.perform(get("/students/1/addRemoveGuardians/search").with(httpBasic(username, pwd))
                .param("GuardianLastName", ""))
                .andExpect(view().name("/SRM/students/guardianSet"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("guardianSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateTutorForm(String username, String pwd) throws Exception{
        mockMvc.perform(get("/students/3/addRemoveTutor").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/personalTutor"))
                .andExpect(model().attributeExists("teacherSet"))
                .andExpect(model().attributeExists("student"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateTutorFormNOTFOUND(String username, String pwd) throws Exception{
        mockMvc.perform(get("/students/52343/addRemoveTutor").with(httpBasic(username, pwd)))
                .andExpect(status().isNotFound());
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateTutorForm(String username, String pwd) throws Exception{
        mockMvc.perform(post("/students/3/addRemoveTutor").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("student", studentService.findById(3L)))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentDetails"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("guardians"))
                .andExpect(model().attributeExists("contactDetail"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("formGroupList"))
                .andExpect(model().attributeExists("subjectClassLists"))
                .andExpect(model().attributeExists("newStudent"));
    }

    //teacher will be null here
    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void processUpdateTutorForm_Remove(String username, String pwd) throws Exception{
        mockMvc.perform(post("/students/2/removeTutor").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/students/studentDetails"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("guardians"))
                .andExpect(model().attributeExists("contactDetail"))
                .andExpect(model().attributeExists("formGroupList"))
                .andExpect(model().attributeExists("subjectClassLists"))
                .andExpect(model().attributeExists("newStudent"));
    }
}