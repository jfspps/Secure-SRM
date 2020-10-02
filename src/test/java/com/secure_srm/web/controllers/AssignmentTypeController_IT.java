package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.AssignmentType;
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
public class AssignmentTypeController_IT extends SecurityCredentialsTest{

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void getAssignmentList(String username, String pwd) throws Exception {
        mockMvc.perform(get("/assignmentTypes/index").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/assignmentType/assignmentTypeIndex"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("assignmentTypeSet"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getNewAssignment(String username, String pwd) throws Exception {
        mockMvc.perform(get("/assignmentTypes/new").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/assignmentType/newAssignmentType"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("assignmentType"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postNewAssignment(String username, String pwd) throws Exception {
        mockMvc.perform(post("/assignmentTypes/new").with(httpBasic(username, pwd)).with(csrf())
                .param("description", "sample assignment description"))
                .andExpect(view().name("/SRM/assignmentType/newAssignmentType"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("assignmentType"))
                .andExpect(model().attributeExists("assignmentTypeFeedback"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateAssignment(String username, String pwd) throws Exception {
        mockMvc.perform(get("/assignmentTypes/1/edit").with(httpBasic(username, pwd)))
                .andExpect(view().name("/SRM/assignmentType/updateAssignmentType"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("assignmentType"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateAssignment(String username, String pwd) throws Exception {
        mockMvc.perform(post("/assignmentTypes/1/edit").with(httpBasic(username, pwd)).with(csrf())
                .param("description", "new description")
                .flashAttr("assignmentType", AssignmentType.builder().description("something").build()))
                .andExpect(view().name("/SRM/assignmentType/updateAssignmentType"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("assignmentType"))
                .andExpect(model().attributeExists("assignmentTypeFeedback"));
    }
}
