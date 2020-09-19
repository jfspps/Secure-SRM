package com.secure_srm.web.controllers;

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
@RequestMapping({"/teachers"})
public class TeacherController {

    private final TeacherUserService teacherUserService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listTeachers(Model model, String lastName) {
        if(lastName == null || lastName.isEmpty()){
            model.addAttribute("teachers", teacherUserService.findAll());
        } else {
            model.addAttribute("teachers", teacherUserService.findAllByLastNameContainingIgnoreCase(lastName));
        }
        return "/SRM/teachers/teacherIndex";
    }
}
