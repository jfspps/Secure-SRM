package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.Report;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@SpringBootTest
public class StudentReportControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listStudents(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentReports/").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/reportIndex"))
                .andExpect(model().attributeExists("reports"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewReport(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentReports/new").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/newReport"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("report"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewReport_search(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentReports/new/search").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/newReport"))
                .andExpect(model().attributeExists("report"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subjects"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewReport_searchWithParams(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentReports/new/search").with(httpBasic(username, pwd)).with(csrf())
                .param("StudentLastName", "smith")
                .param("SubjectName", "math"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/newReport"))
                .andExpect(model().attributeExists("report"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subjects"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postNewReport(String username, String pwd) throws Exception {
        Report tempReport = Report.builder()
                .student(studentService.findById(1L))
                .subject(subjectService.findById(1L))
                .teacher(teacherUserService.findById(1L))
                .comments("Bla")
                .uniqueIdentifier("something").build();

        mockMvc.perform(post("/studentReports/new").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("report", tempReport))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/viewReport"))
                .andExpect(model().attributeExists("reportFeedback"))
                .andExpect(model().attributeExists("report"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void viewReport(String username, String pwd) throws Exception {
        Report tempReport = Report.builder()
                .student(studentService.findById(1L))
                .subject(subjectService.findById(1L))
                .teacher(teacherUserService.findById(1L))
                .comments("Bla")
                .uniqueIdentifier("something").build();
        Report saved = reportService.save(tempReport);

        mockMvc.perform(get("/studentReports/" + saved.getId()).with(httpBasic(username, pwd)))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/viewReport"))
                .andExpect(model().attributeExists("report"));
    }

    @WithUserDetails("marymanning")
    @Test
    void getUpdateReport_permitted() throws Exception {
        Report tempReport = Report.builder()
                .student(studentService.findById(1L))
                .subject(subjectService.findById(1L))
                .teacher(userService.findByUsername("marymanning").getTeacherUser())
                .comments("Bla")
                .uniqueIdentifier("something").build();
        Report saved = reportService.save(tempReport);

        mockMvc.perform(get("/studentReports/" + saved.getId() + "/edit"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/updateReport"))
                .andExpect(model().attributeExists("report"));
    }

    @WithUserDetails("marymanning")
    @Test
    void getUpdateReport_denied() throws Exception {
        Report tempReport = Report.builder()
                .student(studentService.findById(1L))
                .subject(subjectService.findById(1L))
                .teacher(userService.findByUsername("keithjones").getTeacherUser())
                .comments("Bla")
                .uniqueIdentifier("something").build();
        Report saved = reportService.save(tempReport);

        mockMvc.perform(get("/studentReports/" + saved.getId() + "/edit"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/customMessage"))
                .andExpect(model().attribute("message", "You are not permitted to edit reports of other teachers"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postUpdateReport(String username, String pwd) throws Exception {
        Report tempReport = Report.builder()
                .student(studentService.findById(1L))
                .subject(subjectService.findById(1L))
                .teacher(teacherUserService.findById(1L))
                .comments("Bla")
                .uniqueIdentifier("something").build();
        Report saved = reportService.save(tempReport);

        mockMvc.perform(post("/studentReports/" + saved.getId() + "/edit").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("report", saved))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentReports/viewReport"))
                .andExpect(model().attributeExists("report"))
                .andExpect(model().attributeExists("reportFeedback"));
    }
}
