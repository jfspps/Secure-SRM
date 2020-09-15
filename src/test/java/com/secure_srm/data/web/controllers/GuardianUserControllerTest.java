package com.secure_srm.data.web.controllers;

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
public class GuardianUserControllerTest extends UserControllerTest {

    // user and GuardianUser CRUD tests ===============================================================================
    //context loads adminUsers, teacherUsers, followed by guardianUsers
    //IDs are [1,2], [3,4] and [5,6] respectively

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getCreateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(get("/createGuardian").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("guardianCreate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("newUser"))
                .andExpect(model().attributeExists("newGuardian"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void getCreateGuardian_FAIL(String username, String pwd) throws Exception {
        mockMvc.perform(get("/createGuardian").with(httpBasic(username, pwd)))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postCreateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(post("/createGuardian").with(httpBasic(username, pwd)).with(csrf())
                .param("guardianUserName", "TeamGreen and TeamRed")
                .param("username", "bigteeee")
                .param("password", "bigteeee123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/adminPage"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void postCreateGuardian_FAIL(String username, String pwd) throws Exception {
        mockMvc.perform(post("/createGuardian").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(get("/updateGuardian/5").with(httpBasic(username, pwd)))
                .andExpect(status().isOk())
                .andExpect(view().name("guardianUpdate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("currentGuardianUser"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void getUpdateGuardianNOTFOUND(String username, String pwd) throws Exception {
        mockMvc.perform(get("/updateGuardian/1").with(httpBasic(username, pwd)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/adminPage"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void getUpdateGuardian_FAIL(String username, String pwd) throws Exception {
        mockMvc.perform(get("/updateGuardian/6").with(httpBasic(username, pwd)))
                .andExpect(status().isForbidden());
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateGuardian(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateGuardian/5").with(httpBasic(username, pwd)).with(csrf())
                .param("guardianUserName", "blablablabla")
                .param("username", "someoneNotOnFile"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("guardianUpdate"))
                .andExpect(model().attributeExists("GuardianUserSaved"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("currentGuardianUser"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateGuardian_UsernameBlank(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateGuardian/5").with(httpBasic(username, pwd)).with(csrf())
                .param("guardianUserName", "blablablabla")
                .param("username", ""))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("guardianUpdate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("usernameError"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("currentGuardianUser"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateGuardian_GuardianUserNameBlank(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateGuardian/6").with(httpBasic(username, pwd)).with(csrf())
                .param("guardianUserName", "")
                .param("username", "asduafajlkasjdjlk"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("guardianUpdate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("guardianUserNameError"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("currentGuardianUser"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateGuardian_UserExists(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateGuardian/5").with(httpBasic(username, pwd)).with(csrf())
                .param("guardianUserName", "fdsfdsfds")
                .param("username", "alexsmith"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("guardianUpdate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("usernameExists"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("currentGuardianUser"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamSchoolAdminUsers")
    @ParameterizedTest
    void postUpdateGuardian_GuardianUserExists(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateGuardian/5").with(httpBasic(username, pwd)).with(csrf())
                .param("guardianUserName", "Alex Smith")
                .param("username", "paulsmith"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("guardianUpdate"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("guardianUserExists"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("currentGuardianUser"));
    }

    @MethodSource("com.secure_srm.data.web.controllers.SecurityCredentialsTest#streamAllNonAdminUsers")
    @ParameterizedTest
    void postUpdateGuardianFAIL(String username, String pwd) throws Exception {
        mockMvc.perform(post("/updateGuardian/6").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
