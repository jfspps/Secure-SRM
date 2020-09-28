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
                .andExpect(view().name("/SRM/academicRecords/subjectIndex"))
                .andExpect(model().attribute("subjects", hasSize(2)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void searchSubjects(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects").with(httpBasic(username, pwd)).with(csrf())
                .param("subjectTitle", "english"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/academicRecords/subjectIndex"))
                .andExpect(model().attribute("subjects", hasSize(1)));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void searchSubjectsPartial(String username, String pwd) throws Exception {
        mockMvc.perform(get("/subjects").with(httpBasic(username, pwd)).with(csrf())
                .param("subjectTitle", "math"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/academicRecords/subjectIndex"))
                .andExpect(model().attribute("subjects", hasSize(1)));
    }
}
