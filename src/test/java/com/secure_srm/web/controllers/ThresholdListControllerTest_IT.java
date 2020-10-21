package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.ThresholdList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Slf4j
@SpringBootTest
public class ThresholdListControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listThresholds(String username, String pwd) throws Exception {
        mockMvc.perform(get("/thresholdLists/").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/thresholdListIndex"))
                .andExpect(model().attributeExists("thresholdLists"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewThresholdList(String username, String pwd) throws Exception {
        mockMvc.perform(get("/thresholdLists/new").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/newThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewThresholdList_search(String username, String pwd) throws Exception {
        mockMvc.perform(get("/thresholdLists/new/search").with(httpBasic(username, pwd)).with(csrf())
                .param("thresholdUniqueId", "English"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/newThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postNewThresholdList(String username, String pwd) throws Exception {
        ThresholdList temp = ThresholdList.builder()
                .thresholds(Set.of(thresholdService.findById(1L)))
                .uniqueID("something")
                .uploader(userService.findByUsername(username).getTeacherUser()).build();

        mockMvc.perform(post("/thresholdLists/new").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("threshold", temp))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/viewThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"))
                .andExpect(model().attributeExists("thresholdListFeedback"));
    }
}
