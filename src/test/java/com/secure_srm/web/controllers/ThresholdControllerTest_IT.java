package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.Threshold;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;

import java.util.HashSet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@Slf4j
@SpringBootTest
public class ThresholdControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listThresholds(String username, String pwd) throws Exception {
        mockMvc.perform(get("/thresholds/").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/threshold/thresholdIndex"))
                .andExpect(model().attributeExists("thresholds"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listThresholds_searchUniqueID(String username, String pwd) throws Exception {
        mockMvc.perform(get("/thresholds/").with(httpBasic(username, pwd)).with(csrf())
                .param("uniqueID", "something"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/threshold/thresholdIndex"))
                .andExpect(model().attributeExists("thresholds"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewThreshold(String username, String pwd) throws Exception {
        mockMvc.perform(get("/thresholds/new").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/threshold/newThreshold"))
                .andExpect(model().attributeExists("threshold"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postNewThreshold(String username, String pwd) throws Exception {
        Threshold tempThreshold = Threshold.builder()
                .uploader(userService.findByUsername("marymanning").getTeacherUser())
                .thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("D")
                .uniqueId("something different").build();

        mockMvc.perform(post("/thresholds/new").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("threshold", tempThreshold))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/threshold/thresholdDetails"))
                .andExpect(model().attributeExists("threshold"))
                .andExpect(model().attributeExists("thresholdFeedback"))
                .andExpect(model().attributeExists("teacher"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getViewThreshold(String username, String pwd) throws Exception {
        Threshold tempThreshold = Threshold.builder()
                .uploader(userService.findByUsername("marymanning").getTeacherUser())
                .thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("D")
                .uniqueId("something different").build();
        Threshold saved = thresholdService.save(tempThreshold);

        mockMvc.perform(get("/thresholds/" + saved.getId()).with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/threshold/thresholdDetails"))
                .andExpect(model().attributeExists("threshold"))
                .andExpect(model().attributeExists("teacher"));
    }

    @WithUserDetails("marymanning")
    @Test
    void getUpdateThreshold_permitted() throws Exception {
        Threshold tempThreshold = Threshold.builder()
                .uploader(userService.findByUsername("marymanning").getTeacherUser())
                .thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("D")
                .uniqueId("something different").build();
        Threshold saved = thresholdService.save(tempThreshold);

        mockMvc.perform(get("/thresholds/" + saved.getId() + "/edit").with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/threshold/updateThreshold"))
                .andExpect(model().attributeExists("threshold"))
                .andExpect(model().attributeExists("currentTeacher"));
    }

    @WithUserDetails("keithjones")
    @Test
    void getUpdateThreshold_denied() throws Exception {
        Threshold tempThreshold = Threshold.builder()
                .uploader(userService.findByUsername("marymanning").getTeacherUser())
                .thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("D")
                .uniqueId("something different").build();
        Threshold saved = thresholdService.save(tempThreshold);

        mockMvc.perform(get("/thresholds/" + saved.getId() + "/edit").with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/customMessage"))
                .andExpect(model().attribute("message", "You are only permitted to edit your own thresholds"));
    }

    @WithUserDetails("marymanning")
    @Test
    void postUpdateThreshold() throws Exception {
        Threshold tempThreshold = Threshold.builder()
                .uploader(userService.findByUsername("marymanning").getTeacherUser())
                .thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("D")
                .uniqueId("something different").build();
        Threshold saved = thresholdService.save(tempThreshold);

        mockMvc.perform(post("/thresholds/" + saved.getId() + "/edit").with(csrf())
                .flashAttr("threshold", saved))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/threshold/thresholdDetails"))
                .andExpect(model().attributeExists("threshold"))
                .andExpect(model().attributeExists("thresholdFeedback"))
                .andExpect(model().attributeExists("teacher"));
    }

    @WithUserDetails("keithjones")
    @Test
    void getDeleteThreshold_denied() throws Exception {
        Threshold tempThreshold = Threshold.builder()
                .uploader(userService.findByUsername("marymanning").getTeacherUser())
                .thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("D")
                .uniqueId("something different").build();
        Threshold saved = thresholdService.save(tempThreshold);

        mockMvc.perform(get("/thresholds/" + saved.getId() + "/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails("marymanning")
    @Test
    void getDeleteThreshold() throws Exception {
        Threshold tempThreshold = Threshold.builder()
                .uploader(userService.findByUsername("marymanning").getTeacherUser())
                .thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("D")
                .uniqueId("something different").build();
        Threshold saved = thresholdService.save(tempThreshold);

        mockMvc.perform(get("/thresholds/" + saved.getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/thresholds/index"));
    }
}
