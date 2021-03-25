package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.ThresholdList;
import com.secure_srm.model.security.TeacherUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

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
                .param("thresholdUniqueId", "English")
                .param("studentTaskTitle", "English"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/newThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"))
                .andExpect(model().attributeExists("studentTasks"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postNewThresholdList(String username, String pwd) throws Exception {
        ThresholdList temp = ThresholdList.builder()
                .thresholds(Set.of(thresholdService.findById(1L)))
                .studentTaskSet(Set.of(studentTaskService.findById(1L)))
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

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void viewThresholdList(String username, String pwd) throws Exception {
        mockMvc.perform(get("/thresholdLists/1").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/viewThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"))
                .andExpect(model().attributeExists("teacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getUpdateThresholdList_permitted(String username, String pwd) throws Exception {
        TeacherUser currentTeacher = userService.findByUsername(username).getTeacherUser();
        Set<ThresholdList> thresholdListSet = thresholdListService.findAll();
        ThresholdList found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst().get();

        mockMvc.perform(get("/thresholdLists/" + found.getId() + "/edit").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/updateThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"))
                .andExpect(model().attributeExists("studentTasks"))
                .andExpect(model().attributeExists("currentTeacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getUpdateThresholdList_denied(String username, String pwd) throws Exception {
        TeacherUser currentTeacher = userService.findByUsername(username).getTeacherUser();
        Set<ThresholdList> thresholdListSet = thresholdListService.findAll();
        ThresholdList found = thresholdListSet.stream().filter(thresholdList -> !thresholdList.getUploader().equals(currentTeacher)).findFirst().get();

        mockMvc.perform(get("/thresholdLists/" + found.getId() + "/edit").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/customMessage"))
                .andExpect(model().attribute("message", "You are not permitted to edit threshold lists of other teachers"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getUpdateThresholdList_search(String username, String pwd) throws Exception {
        TeacherUser currentTeacher = userService.findByUsername(username).getTeacherUser();
        Set<ThresholdList> thresholdListSet = thresholdListService.findAll();
        ThresholdList found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst().get();

        mockMvc.perform(get("/thresholdLists/" + found.getId() + "/edit/search").with(httpBasic(username, pwd)).with(csrf())
                .param("thresholdUniqueId", "English")
                .param("studentTaskTitle", "william"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/updateThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"))
                .andExpect(model().attributeExists("studentTasks"))
                .andExpect(model().attributeExists("currentTeacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postUpdateThresholdList(String username, String pwd) throws Exception {
        TeacherUser currentTeacher = userService.findByUsername(username).getTeacherUser();
        Set<ThresholdList> thresholdListSet = thresholdListService.findAll();
        ThresholdList found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst().get();

        mockMvc.perform(post("/thresholdLists/" + found.getId() + "/edit").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("thresholdList", found))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/thresholdList/viewThresholdList"))
                .andExpect(model().attributeExists("thresholdList"))
                .andExpect(model().attributeExists("thresholds"))
                .andExpect(model().attributeExists("teacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getDeleteThresholdList(String username, String pwd) throws Exception {
        TeacherUser currentTeacher = userService.findByUsername(username).getTeacherUser();
        Set<ThresholdList> thresholdListSet = thresholdListService.findAll();
        ThresholdList found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst().get();

        mockMvc.perform(get("/thresholdLists/" + found.getId() + "/delete").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/thresholdLists/index"));
    }

    @WithUserDetails("keithjones")
    @Test
    void getDeleteThresholdList_denied() throws Exception {
        TeacherUser currentTeacher = userService.findByUsername("marymanning").getTeacherUser();
        Set<ThresholdList> thresholdListSet = thresholdListService.findAll();
        ThresholdList found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst().get();

        mockMvc.perform(get("/thresholdLists/" + found.getId() + "/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
