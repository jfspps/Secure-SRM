package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.ForbiddenException;
import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.model.security.User;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.services.academicServices.StudentResultService;
import com.secure_srm.services.academicServices.StudentTaskService;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.AdminRead;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import com.secure_srm.web.permissionAnnot.TeacherUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/studentTask"})
public class StudentTaskController {

    private final AssignmentTypeService assignmentTypeService;
    private final StudentTaskService studentTaskService;
    private final UserService userService;
    private final AuxiliaryController auxiliaryController;

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
    @GetMapping({"", "/", "/index"})
    public String listStudentTasks(Model model) {
        model.addAttribute("tasks", studentTaskService.findAll());
        return "/SRM/studentTask/taskIndex";
    }

    @TeacherCreate
    @GetMapping("/new")
    public String getNewTask(Model model) {
        //retrieve this teacher details and tie to studentTask
        if (userService.findAllByUsername(auxiliaryController.getUsername()) == null){
            log.debug("Current username not recognised");
            throw new NotFoundException("Username not recognised");
        }

        TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
        if (currentTeacher == null){
            log.debug("TeacherUser not recognised");
            throw new ForbiddenException("TeacherUser not recognised");
        }

        Set<Subject> subjectsTaught = currentTeacher.getSubjects();
        Set<AssignmentType> assignmentTypeSet = assignmentTypeService.findAll();

        StudentTask studentTask = StudentTask.builder().teacherUploader(currentTeacher)
                .studentResults(new HashSet<>()).subject(subjectsTaught.stream().findAny().get())
                .assignmentType(assignmentTypeSet.stream().findAny().get()).build();

        model.addAttribute("assignmentTypes", auxiliaryController.sortAssignmentTypeSetByDescription(assignmentTypeSet));
        model.addAttribute("subjects", subjectsTaught);
        model.addAttribute("task", studentTask);
        return "/SRM/studentTask/newTask";
    }

    @TeacherCreate
    @PostMapping("/new")
    public String postNewTask(@Valid @ModelAttribute("task") StudentTask studentTask, BindingResult result,
                              Model model) {
        if (result.hasErrors()){
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
            if (currentTeacher == null){
                log.debug("TeacherUser not recognised");
                throw new ForbiddenException("TeacherUser not recognised");
            }

            Set<Subject> subjectsTaught = currentTeacher.getSubjects();

            model.addAttribute("assignmentTypes", auxiliaryController.sortAssignmentTypeSetByDescription(assignmentTypeService.findAll()));
            model.addAttribute("subjects", subjectsTaught);
            model.addAttribute("task", studentTask);
            return "/SRM/studentTask/newTask";
        }

        //check teacher hasn't already uploaded record with same title
        if (studentTaskService.findByTitleAndTeacherUploaderId(studentTask.getTitle(), Long.valueOf(auxiliaryController.getUserId())) != null){
            log.debug("Task with given title already exits");
            TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
            Set<Subject> subjectsTaught = currentTeacher.getSubjects();

            model.addAttribute("assignmentTypes", auxiliaryController.sortAssignmentTypeSetByDescription(assignmentTypeService.findAll()));
            model.addAttribute("subjects", subjectsTaught);
            model.addAttribute("task", studentTask);
            model.addAttribute("newTaskFeedback", "Task with given title already exists");
            return "/SRM/studentTask/newTask";
        }

        //set current teacherUser
        TeacherUser currentTeacher = userService.findById(Long.valueOf(auxiliaryController.getUserId())).getTeacherUser();
        studentTask.setTeacherUploader(currentTeacher);

        StudentTask saved = studentTaskService.save(studentTask);
        log.debug("New task saved by: " + currentTeacher.getFirstName() + ' ' + currentTeacher.getLastName());
        model.addAttribute("taskFeedback", "New task saved");
        model.addAttribute("task", saved);
        return "/SRM/studentTask/taskDetails";
    }

    @TeacherRead
    @GetMapping("/{taskId}")
    public String postNewTask(@PathVariable("taskId") String taskID, Model model) {
        if (studentTaskService.findById(Long.valueOf(taskID)) == null){
            log.debug("Student task not found");
            throw new NotFoundException("Student task not found");
        }

        model.addAttribute("task", studentTaskService.findById(Long.valueOf(taskID)));
        return "/SRM/studentTask/taskDetails";
    }

    @TeacherUpdate
    @GetMapping("/{taskId}/edit")
    public String getUpdateTask(@PathVariable("taskId") String taskID, Model model) {
        if (userService.findAllByUsername(auxiliaryController.getUsername()) == null){
            log.debug("Current username not recognised");
            throw new NotFoundException("Username not recognised");
        }

        if (studentTaskService.findById(Long.valueOf(taskID)) == null){
            log.debug("Student task not found");
            throw new NotFoundException("Student task not found");
        }

        StudentTask onFile = studentTaskService.findById(Long.valueOf(taskID));
        //make sure teacher records match
        if (!onFile.getTeacherUploader().equals(userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser())){
            log.debug("Current teacher is not allowed to edit this resource");
            model.addAttribute("taskFeedback", "You are not permitted to edit this task");
            model.addAttribute("task", studentTaskService.findById(Long.valueOf(taskID)));
            return "/SRM/studentTask/taskDetails";
        }

        Set<Subject> subjectsTaught = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser().getSubjects();

        model.addAttribute("assignmentTypes", auxiliaryController.sortAssignmentTypeSetByDescription(assignmentTypeService.findAll()));
        model.addAttribute("subjects", subjectsTaught);
        model.addAttribute("task", onFile);
        return "/SRM/studentTask/updateTask";
    }

    @TeacherUpdate
    @PostMapping("/{taskId}/edit")
    public String postUpdateTask(@PathVariable("taskId") String taskID, Model model,
                                @Valid @ModelAttribute("task") StudentTask studentTask,
                                BindingResult result) {
        if (userService.findAllByUsername(auxiliaryController.getUsername()) == null){
            log.debug("Current username not recognised");
            throw new NotFoundException("Username not recognised");
        }

        StudentTask onFile = studentTaskService.findById(Long.valueOf(taskID));
        //make sure teacher records match
        if (!onFile.getTeacherUploader().equals(userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser())){
            log.debug("Current teacher is not allowed to edit this resource");
            model.addAttribute("taskFeedback", "You are not permitted to edit this task");
            model.addAttribute("task", studentTaskService.findById(Long.valueOf(taskID)));
            return "/SRM/studentTask/updateTask";
        }

        if (result.hasErrors()){
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
            Set<Subject> subjectsTaught = currentTeacher.getSubjects();

            model.addAttribute("assignmentTypes", auxiliaryController.sortAssignmentTypeSetByDescription(assignmentTypeService.findAll()));
            model.addAttribute("subjects", subjectsTaught);
            model.addAttribute("task", studentTask);
            return "/SRM/studentTask/updateTask";
        }

        onFile.setAssignmentType(studentTask.getAssignmentType());
        onFile.setContributor(studentTask.isContributor());
        onFile.setMaxScore(studentTask.getMaxScore());
        onFile.setTitle(studentTask.getTitle());

        StudentTask saved = studentTaskService.save(onFile);
        log.debug("Student task updated");
        model.addAttribute("taskFeedback", "Student task updated");
        model.addAttribute("task", saved);
        return "/SRM/studentTask/taskDetails";
    }
}
