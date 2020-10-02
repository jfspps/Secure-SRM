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
}
