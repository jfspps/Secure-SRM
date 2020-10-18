package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.ForbiddenException;
import com.secure_srm.model.academic.StudentResult;
import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.StudentResultService;
import com.secure_srm.services.academicServices.StudentTaskService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/studentResult")
public class StudentResultController {

    private final StudentResultService studentResultService;
    private final StudentService studentService;
    private final TeacherUserService teacherUserService;
    private final StudentTaskService studentTaskService;
    private final AuxiliaryController auxiliaryController;
    private final UserService userService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("hasSubject")
    public Boolean teachesSubjects(){
        //determines if a User is a teacher and then if they teach anything (blocks New Student Task/Report/Result as appropriate)
        return auxiliaryController.teachesASubject();
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getResultIndex(Model model){
        model.addAttribute("results", studentResultService.findAll());
        return "/SRM/studentResults/resultsIndex";
    }

    @TeacherCreate
    @GetMapping("/new")
    public String getNewResult(Model model){
        if (!auxiliaryController.teachesASubject()){
            log.debug("User does not have prerequisites");
            throw new ForbiddenException("User does not have prerequisites");
        }

        if (studentTaskService.findAll().isEmpty()){
            log.debug("Student task DB empty");
            model.addAttribute("message", "At least one student task must exist in order to save a result. Click Resolve to create a new task.");
            model.addAttribute("resolution", "/studentTask/new");
            return "/SRM/customMessage";
        }

        TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
        Set<Subject> subjectsTaught = currentTeacher.getSubjects();
        model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));

        //list tasks relevant to current teacher's subject(s)
        Set<StudentTask> tasks = new HashSet<>();
        for (Subject subject: subjectsTaught) {
            tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
        }
        model.addAttribute("studentTasks", tasks);

        StudentResult newResult = StudentResult.builder()
                .teacher(currentTeacher)
                .build();

        model.addAttribute("result", newResult);

        return "/SRM/studentResults/newResult";
    }

    @TeacherCreate
    @GetMapping("/new/search")
    public String getNewResult_Search(Model model, String StudentLastName, String TaskTitle){
        Set<Student> studentSet;
        if (StudentLastName == null || StudentLastName.isEmpty()){
            studentSet = studentService.findAll();
            model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentSet));
        } else {
            studentSet = studentService.findAllByLastNameContainingIgnoreCase(StudentLastName);
            model.addAttribute("students", studentSet);
        }

        TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
        Set<Subject> subjectsTaught = currentTeacher.getSubjects();
        Set<StudentTask> tasks = new HashSet<>();
        for (Subject subject: subjectsTaught) {
            tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
        }

        if (TaskTitle.length() > 1){
            tasks.removeIf(studentTask -> !studentTask.getTitle().equals(TaskTitle));
        }

        model.addAttribute("studentTasks", tasks);

        StudentResult newResult = StudentResult.builder()
                .teacher(currentTeacher)
                .build();

        model.addAttribute("result", newResult);

        return "/SRM/studentResults/newResult";
    }

    @TeacherCreate
    @PostMapping("/new")
    public String postNewResult(@Valid @ModelAttribute("result") StudentResult studentResult, BindingResult bindingResult, Model model){
        TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();

        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            Set<Subject> subjectsTaught = currentTeacher.getSubjects();
            model.addAttribute("newResult", studentResult);
            model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));

            //list tasks relevant to current teacher's subject(s)
            Set<StudentTask> tasks = new HashSet<>();
            for (Subject subject: subjectsTaught) {
                tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
            }
            model.addAttribute("studentTasks", tasks);
            return "/SRM/studentResults/newResult";
        }

        //set result with current user's details
        studentResult.setTeacher(currentTeacher);

        //update StudentTask tally
        StudentTask taskOnFile = studentResult.getStudentTask();
        taskOnFile.getStudentResults().add(studentResult);
        StudentTask savedTask = studentTaskService.save(taskOnFile);
        log.debug("Task " + savedTask.getTitle() + " updated with new result");

        StudentResult savedResult = studentResultService.save(studentResult);
        log.debug("Result from task, " + savedResult.getStudentTask().getTitle() + ", saved");
        model.addAttribute("resultFeedback", "New result saved");
        model.addAttribute("result", savedResult);
        return "/SRM/studentResults/viewResult";
    }
}
