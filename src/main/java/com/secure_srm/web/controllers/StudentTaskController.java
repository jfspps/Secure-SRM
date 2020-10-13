package com.secure_srm.web.controllers;

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

    private final TeacherUserService teacherUserService;
    private final SubjectService subjectService;
    private final AssignmentTypeService assignmentTypeService;
    private final StudentResultService studentResultService;
    private final StudentTaskService studentTaskService;
    private final UserService userService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
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
        if (userService.findAllByUsername(getUsername()) == null){
            log.debug("Current username not recognised");
            throw new NotFoundException("Username not recognised");
        }

        //todo: check if teacher is assigned at least one subject

        TeacherUser currentTeacher = userService.findByUsername(getUsername()).getTeacherUser();
        Set<Subject> subjectsTaught = currentTeacher.getSubjects();
        Set<AssignmentType> assignmentTypeSet = assignmentTypeService.findAll();

        StudentTask studentTask = StudentTask.builder().teacherUploader(currentTeacher)
                .studentResults(new HashSet<>()).subject(subjectsTaught.stream().findAny().get())
                .assignmentType(assignmentTypeSet.stream().findAny().get()).build();

        model.addAttribute("assignmentTypes", sortAssignmentTypeSetByDescription(assignmentTypeSet));
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
            TeacherUser currentTeacher = userService.findByUsername(getUsername()).getTeacherUser();
            Set<Subject> subjectsTaught = currentTeacher.getSubjects();

            model.addAttribute("assignmentTypes", sortAssignmentTypeSetByDescription(assignmentTypeService.findAll()));
            model.addAttribute("subjects", subjectsTaught);
            model.addAttribute("task", studentTask);
            return "/SRM/studentTask/newTask";
        }

        //check teacher hasn't already uploaded record with same title
        if (studentTaskService.findByTitleAndTeacherUploaderId(studentTask.getTitle(), Long.valueOf(getUserId())) != null){
            log.debug("Task with given title already exits");
            TeacherUser currentTeacher = userService.findByUsername(getUsername()).getTeacherUser();
            Set<Subject> subjectsTaught = currentTeacher.getSubjects();

            model.addAttribute("assignmentTypes", sortAssignmentTypeSetByDescription(assignmentTypeService.findAll()));
            model.addAttribute("subjects", subjectsTaught);
            model.addAttribute("task", studentTask);
            model.addAttribute("newTaskFeedback", "Task with given title already exists");
            return "/SRM/studentTask/newTask";
        }

        //set current teacherUser
        TeacherUser currentTeacher = userService.findById(Long.valueOf(getUserId())).getTeacherUser();
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

    @TeacherCreate
    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @TeacherCreate
    private String getUserId() {
        User found = userService.findByUsername(getUsername());
        if (found != null) {
            return found.getId().toString();
        } else {
            log.debug("User with given ID not found");
            throw new NotFoundException("User with given ID not found");
        }
    }

    /**
     * Returns an ArrayList of items, sorted by assignmentType's description
     * */
    @TeacherRead
    private List<AssignmentType> sortAssignmentTypeSetByDescription(Set<AssignmentType> assignmentTypeSet) {
        List<AssignmentType> listByDescription = new ArrayList<>(assignmentTypeSet);
        //see StudentTasks's model string comparison method, compareTo()
        Collections.sort(listByDescription);
        return listByDescription;
    }
}
