package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.web.permissionAnnot.AdminCreate;
import com.secure_srm.web.permissionAnnot.AdminUpdate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public String postNewAssignmentType(@Valid @ModelAttribute("assignmentType") AssignmentType assignmentType,
                                        BindingResult result, Model model){
        if (result.hasErrors()){
            log.debug("Problems with description submitted");
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            model.addAttribute("assignmentType", assignmentType);
            return "/SRM/assignmentType/newAssignmentType";
        }

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

    @AdminUpdate
    @GetMapping("/{id}/edit")
    public String getNewAssignmentType(Model model, @PathVariable("id") String id){
        if (assignmentTypeService.findById(Long.valueOf(id)) == null){
            log.debug("Assignment type not found");
            throw new NotFoundException("Assignment type not found");
        }

        AssignmentType found = assignmentTypeService.findById(Long.valueOf(id));
        model.addAttribute("assignmentType", found);
        return "/SRM/assignmentType/updateAssignmentType";
    }

    @AdminUpdate
    @PostMapping("/{id}/edit")
    public String postNewAssignmentType(Model model, @PathVariable("id") String id,
                                        @Valid @ModelAttribute("assignmentType") AssignmentType assignmentType,
                                        BindingResult result){
        if (result.hasErrors()){
            log.debug("Problems with description submitted");
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            model.addAttribute("assignmentType", assignmentType);
            return "/SRM/assignmentType/updateAssignmentType";
        }

        AssignmentType found = assignmentTypeService.findById(Long.valueOf(id));

        if (assignmentTypeService.findByDescription(assignmentType.getDescription()) != null){
            log.debug("Assignment type with given description already exists");
            model.addAttribute("assignmentType", found);
            model.addAttribute("assignmentTypeFeedback", "Assignment type with given description already exists");
            return "/SRM/assignmentType/updateAssignmentType";
        }

        found.setDescription(assignmentType.getDescription());
        AssignmentType saved = assignmentTypeService.save(found);
        model.addAttribute("assignmentType", saved);
        log.debug("Assignment type updated");
        model.addAttribute("assignmentTypeFeedback", "Assignment type updated");

        return "/SRM/assignmentType/updateAssignmentType";
    }
}
