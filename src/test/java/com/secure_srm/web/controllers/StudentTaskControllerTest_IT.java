package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.StudentTask;
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

@Slf4j
@Transactional
@SpringBootTest
public class StudentTaskControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listStudentTasks(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentTask/").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/taskIndex"))
                .andExpect(model().attributeExists("tasks"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewStudentTask(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentTask/new").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/newTask"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("assignmentTypes"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postNewStudentTask(String username, String pwd) throws Exception {
        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(subjectService.findById(1L))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();

        mockMvc.perform(post("/studentTask/new").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("task", temp))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/taskDetails"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("taskFeedback"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getStudentTaskDetails(String username, String pwd) throws Exception {
        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(subjectService.findById(1L))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(temp);

        mockMvc.perform(get("/studentTask/" + temp.getId()).with(httpBasic(username, pwd)))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/taskDetails"))
                .andExpect(model().attributeExists("task"));
    }

    @WithUserDetails("keithjones")
    @Test
    void getUpdateStudentTask() throws Exception {
        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(subjectService.findById(1L))
                .teacherUploader(userService.findByUsername("keithjones").getTeacherUser())
                .title("new student task")
                .build();
        studentTaskService.save(temp);

        mockMvc.perform(get("/studentTask/" + temp.getId() + "/edit"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/updateTask"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("assignmentTypes"));
    }

    @WithUserDetails("marymanning")
    @Test
    void getUpdateStudentTask_Denied() throws Exception {
        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(subjectService.findById(1L))
                .teacherUploader(userService.findByUsername("keithjones").getTeacherUser())
                .title("new student task")
                .build();
        studentTaskService.save(temp);

        mockMvc.perform(get("/studentTask/" + temp.getId() + "/edit"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/taskDetails"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("taskFeedback"));
    }

    @WithUserDetails("keithjones")
    @Test
    void postUpdateStudentTask() throws Exception {
        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(subjectService.findById(1L))
                .teacherUploader(userService.findByUsername("keithjones").getTeacherUser())
                .title("new student task 22")
                .build();
        studentTaskService.save(temp);

        mockMvc.perform(post("/studentTask/" + temp.getId() + "/edit").with(csrf())
                .flashAttr("task", temp))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/taskDetails"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attribute("taskFeedback", "Student task updated"));
    }

    @WithUserDetails("marymanning")
    @Test
    void postUpdateStudentTask_denied() throws Exception {
        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(subjectService.findById(1L))
                .teacherUploader(userService.findByUsername("keithjones").getTeacherUser())
                .title("new student task 22")
                .build();
        studentTaskService.save(temp);

        mockMvc.perform(post("/studentTask/" + temp.getId() + "/edit").with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentTask/updateTask"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attribute("taskFeedback", "You are not permitted to edit this task"));
    }
}
