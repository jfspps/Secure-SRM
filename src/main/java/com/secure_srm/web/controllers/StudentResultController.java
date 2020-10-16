package com.secure_srm.web.controllers;

import com.secure_srm.services.academicServices.StudentResultService;
import com.secure_srm.services.academicServices.StudentTaskService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/studentResult")
public class StudentResultController {

    private final StudentResultService studentResultService;
    private final StudentService studentService;
    private final TeacherUserService teacherUserService;
    private final StudentTaskService studentTaskService;
    private final UserService userService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("hasSubject")
    public Boolean teachesSubjects(){
        //determines if a User is a teacher and then if they teach anything (blocks New Student Task/Report/Result as appropriate)
        AuxiliaryController auxiliaryController = new AuxiliaryController(userService);
        return auxiliaryController.teachesASubject();
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getResultIndex(Model model){
        model.addAttribute("results", studentResultService.findAll());
        return "/SRM/studentResults/resultsIndex";
    }
}
