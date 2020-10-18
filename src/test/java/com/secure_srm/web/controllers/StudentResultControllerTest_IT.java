package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.StudentResult;
import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.model.security.TeacherUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

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
public class StudentResultControllerTest_IT extends SecurityCredentialsTest {

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolStaff")
    @ParameterizedTest
    void listStudentResults(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentResult/").with(httpBasic(username, pwd)).with(csrf()))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/resultsIndex"))
                .andExpect(model().attributeExists("results"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewStudentResults(String username, String pwd) throws Exception {
        //need at least one student task to propose a new result (only the subject must match)
        TeacherUser tempTeacher = userService.findByUsername(username).getTeacherUser();

        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(temp);

        mockMvc.perform(get("/studentResult/new").with(httpBasic(username, pwd)))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/newResult"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("studentTasks"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewStudentResults_search(String username, String pwd) throws Exception {
        //need at least one student task to propose a new result (only the subject must match)
        TeacherUser tempTeacher = userService.findByUsername(username).getTeacherUser();

        StudentTask temp = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(temp);

        mockMvc.perform(get("/studentResult/new/search").with(httpBasic(username, pwd)).with(csrf())
                .param("StudentLastName", "Smith")
                .param("TaskTitle", "new student task"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/newResult"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("studentTasks"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getNewStudentResults_EmptyTaskDB(String username, String pwd) throws Exception {
        mockMvc.perform(get("/studentResult/new").with(httpBasic(username, pwd)))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/customMessage"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("resolution"));
    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postNewStudentResults(String username, String pwd) throws Exception {
        TeacherUser tempTeacher = userService.findByUsername(username).getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();

        StudentResult tempResult = StudentResult.builder()
                .comments("did OK")
                .score("merit")
                .student(studentService.findById(1L))
                .studentTask(tempTask)
                .teacher(teacherUserService.findById(1L)).build();

        mockMvc.perform(post("/studentResult/new").with(httpBasic(username, pwd)).with(csrf())
                .flashAttr("result", tempResult))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/viewResult"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("resultFeedback"));
    }


}
