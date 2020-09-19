package com.secure_srm.web.controllers;

import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.GuardianUserService;
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
@RequestMapping({"/students"})
public class StudentController {

    private final StudentService studentService;
    private final GuardianUserService guardianUserService;
    private final TeacherUserService teacherUserService;
    private final SubjectService subjectService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listStudents(Model model, String lastName) {
        if(lastName == null || lastName.isEmpty()){
            model.addAttribute("students", studentService.findAll());
        } else {
            model.addAttribute("students", studentService.findAllByLastNameLike(lastName));
        }
        return "/SRM/students/studentIndex";
    }
}
