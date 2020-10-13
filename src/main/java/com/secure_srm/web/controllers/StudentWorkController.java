package com.secure_srm.web.controllers;

import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.services.academicServices.StudentResultService;
import com.secure_srm.services.academicServices.StudentWorkService;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/studentWork"})
public class StudentWorkController {

    private final TeacherUserService teacherUserService;
    private final SubjectService subjectService;
    private final AssignmentTypeService assignmentTypeService;
    private final StudentResultService studentResultService;
    private final StudentWorkService studentWorkService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listStudentTasks(Model model) {
        model.addAttribute("tasks", studentWorkService.findAll());
        return "/SRM/studentWork/workIndex";
    }
}
