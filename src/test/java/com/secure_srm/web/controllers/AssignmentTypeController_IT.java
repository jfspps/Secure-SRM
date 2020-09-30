package com.secure_srm.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
