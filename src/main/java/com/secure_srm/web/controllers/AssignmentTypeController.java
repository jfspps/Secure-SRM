package com.secure_srm.web.controllers;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.web.permissionAnnot.AdminCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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

    @AdminCreate
    @GetMapping("/new")
    public String getNewAssignmentType(Model model){
        model.addAttribute("assignmentType", AssignmentType.builder().build());
        return "/SRM/assignmentType/newAssignmentType";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewAssignmentType(@ModelAttribute("assignmentType") AssignmentType assignmentType, Model model){
        if (assignmentTypeService.findByDescription(assignmentType.getDescription()) != null){
            log.debug("Assignment type with given description already exists");
            model.addAttribute("assignmentType", assignmentType);
            model.addAttribute("assignmentTypeFeedback", "Assignment type with given description already exists");
            return "/SRM/assignmentType/newAssignmentType";
        }

        AssignmentType saved = assignmentTypeService.save(assignmentType);
        log.debug("New assignment type with ID: " + saved.getId() + " saved");
        model.addAttribute("assignmentTypeFeedback", "New assignment type saved");
        return "/SRM/assignmentType/newAssignmentType";
    }
}
