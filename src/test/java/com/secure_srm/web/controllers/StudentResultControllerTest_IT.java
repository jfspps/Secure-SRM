package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.StudentResult;
import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.model.security.TeacherUser;
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

//    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
//    @ParameterizedTest
//    void getNewStudentResults_EmptyTaskDB(String username, String pwd) throws Exception {
//        mockMvc.perform(get("/studentResult/new").with(httpBasic(username, pwd)))
//                .andExpect(status().is(200))
//                .andExpect(status().isOk())
//                .andExpect(view().name("/SRM/customMessage"))
//                .andExpect(model().attributeExists("message"))
//                .andExpect(model().attributeExists("resolution"));
//    }

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void postNewStudentResults(String username, String pwd) throws Exception {
        TeacherUser tempTeacher = userService.findByUsername(username).getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("distinction")
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

    @MethodSource("com.secure_srm.web.controllers.SecurityCredentialsTest#streamSchoolTeachers")
    @ParameterizedTest
    void getViewStudentResult(String username, String pwd) throws Exception {
        TeacherUser tempTeacher = userService.findByUsername(username).getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(tempTask);

        StudentResult tempResult = StudentResult.builder()
                .comments("did OK")
                .score("merit")
                .student(studentService.findById(1L))
                .studentTask(tempTask)
                .teacher(teacherUserService.findById(1L)).build();
        StudentResult saved = studentResultService.save(tempResult);

        mockMvc.perform(get("/studentResult/" + saved.getId()).with(httpBasic(username, pwd))
                .flashAttr("result", tempResult))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/viewResult"))
                .andExpect(model().attributeExists("result"));
    }

    @WithUserDetails("keithjones")
    @Test
    void getUpdateStudentResult_permitted() throws Exception {
        TeacherUser tempTeacher = userService.findByUsername("keithjones").getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(tempTask);

        StudentResult tempResult = StudentResult.builder()
                .comments("did OK")
                .score("merit")
                .student(studentService.findById(1L))
                .studentTask(tempTask)
                .teacher(userService.findByUsername("keithjones").getTeacherUser()).build();
        StudentResult saved = studentResultService.save(tempResult);

        mockMvc.perform(get("/studentResult/" + saved.getId() + "/edit")
                .flashAttr("result", tempResult))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/updateResult"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("studentTasks"));
    }

    @WithUserDetails("keithjones")
    @Test
    void getUpdateStudentResult_denied() throws Exception {
        TeacherUser tempTeacher = userService.findByUsername("keithjones").getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("120")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(tempTask);

        StudentResult tempResult = StudentResult.builder()
                .comments("did OK")
                .score("merit")
                .student(studentService.findById(1L))
                .studentTask(tempTask)
                .teacher(userService.findByUsername("marymanning").getTeacherUser()).build();
        StudentResult saved = studentResultService.save(tempResult);

        mockMvc.perform(get("/studentResult/" + saved.getId() + "/edit")
                .flashAttr("result", tempResult))
                .andExpect(status().is(403))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails("keithjones")
    @Test
    void postUpdateStudentResult_permitted() throws Exception {
        TeacherUser tempTeacher = userService.findByUsername("keithjones").getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("distinction")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(tempTask);

        StudentResult tempResult = StudentResult.builder()
                .comments("did OK")
                .score("merit")
                .student(studentService.findById(1L))
                .studentTask(tempTask)
                .teacher(tempTeacher).build();
        StudentResult tempSaved = studentResultService.save(tempResult);

        mockMvc.perform(post("/studentResult/" + tempSaved.getId() + "/edit").with(csrf())
                .flashAttr("result", tempSaved))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/viewResult"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("resultFeedback"));
    }

    @WithUserDetails("marymanning")
    @Test
    void postUpdateStudentResult_denied() throws Exception {
        TeacherUser tempTeacher = userService.findByUsername("keithjones").getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("distinction")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        studentTaskService.save(tempTask);

        StudentResult tempResult = StudentResult.builder()
                .comments("did OK")
                .score("merit")
                .student(studentService.findById(1L))
                .studentTask(tempTask)
                .teacher(tempTeacher).build();
        StudentResult tempSaved = studentResultService.save(tempResult);

        mockMvc.perform(post("/studentResult/" + tempSaved.getId() + "/edit").with(csrf())
                .flashAttr("result", tempSaved))
                .andExpect(status().is(403))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails("keithjones")
    @Test
    void postUpdateStudentResult_search() throws Exception {
        TeacherUser tempTeacher = userService.findByUsername("keithjones").getTeacherUser();

        StudentTask tempTask = StudentTask.builder().assignmentType(assignmentTypeService.findById(1L))
                .contributor(true)
                .maxScore("distinction")
                .studentResults(new HashSet<>())
                .subject(tempTeacher.getSubjects().stream().findAny().orElse(null))
                .teacherUploader(teacherUserService.findById(1L))
                .title("new student task")
                .build();
        StudentTask savedTempTask = studentTaskService.save(tempTask);

        StudentResult tempResult = StudentResult.builder()
                .comments("did OK")
                .score("merit")
                .student(studentService.findById(1L))
                .studentTask(savedTempTask)
                .teacher(tempTeacher).build();
        StudentResult tempSaved = studentResultService.save(tempResult);

        mockMvc.perform(get("/studentResult/" + tempSaved.getId() + "/edit/search").with(csrf())
                .flashAttr("result", tempSaved)
                .param("StudentLastName", "Smith")
                .param("TaskTitle", "new student task"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(view().name("/SRM/studentResults/updateResult"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("studentTasks"));
    }
}
