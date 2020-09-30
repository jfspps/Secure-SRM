package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

// AssignmentType stores the description of the type of assignment (exam, quiz, test, coursework, performance etc.)
// and related StudentResults

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/assignmentTypes"})
public class AssignmentTypeController {

    private final AssignmentTypeService assignmentTypeService;

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getAssignmentTypes(Model model){
        Set<AssignmentType> assignmentTypeSet = assignmentTypeService.findAll();
        model.addAttribute("assignmentTypeSet", assignmentTypeSet);
        return "/SRM/assignmentType/assignmentTypeIndex";
    }
}
