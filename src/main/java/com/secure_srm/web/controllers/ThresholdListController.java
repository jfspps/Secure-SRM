package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Threshold;
import com.secure_srm.model.academic.ThresholdList;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.ThresholdListService;
import com.secure_srm.services.academicServices.ThresholdService;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/thresholdLists"})
public class ThresholdListController {

    private final AuxiliaryController auxiliaryController;
    private final ThresholdService thresholdService;
    private final ThresholdListService thresholdListService;

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
    public String listThresholdLists(Model model, String uniqueID) {
        if (uniqueID == null || uniqueID.isBlank()){
            model.addAttribute("thresholdLists", auxiliaryController.sortThresholdListByUniqueID(thresholdListService.findAll()));
        } else {
            model.addAttribute("thresholdLists",
                    auxiliaryController.sortThresholdListByUniqueID(thresholdListService.findAllByUniqueIDContainingIgnoreCase(uniqueID)));
        }

        return "/SRM/thresholdList/thresholdListIndex";
    }

    @TeacherCreate
    @GetMapping("/new")
    public String getNewThresholdList(Model model){
        if (!auxiliaryController.teachesASubject()){
            log.debug("User is not registered with any subject");
            model.addAttribute("message", "You are not currently registered to teach any subject");
            return "/SRM/customMessage";
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        List<Threshold> thresholdSet = auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll());
        ThresholdList newList = ThresholdList.builder().thresholds(new HashSet<>()).uploader(currentTeacher).build();
        model.addAttribute("thresholds", thresholdSet);
        model.addAttribute("thresholdList", newList);

        return "/SRM/thresholdList/newThresholdList";
    }

    @TeacherCreate
    @GetMapping("/new/search")
    public String getNewThresholdList(Model model, String thresholdUniqueId){
        if (!auxiliaryController.teachesASubject()){
            log.debug("User is not registered with any subject");
            model.addAttribute("message", "You are not currently registered to teach any subject");
            return "/SRM/customMessage";
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        List<Threshold> thresholdSet;
        if (thresholdUniqueId != null || !thresholdUniqueId.isBlank()){
            thresholdSet = auxiliaryController.sortThresholdByUniqueID(thresholdService.findAllByUniqueIDContainingIgnoreCase(thresholdUniqueId));
        } else {
            thresholdSet = auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll());
        }
        ThresholdList newList = ThresholdList.builder().thresholds(new HashSet<>()).uploader(currentTeacher).build();
        model.addAttribute("thresholds", thresholdSet);
        model.addAttribute("thresholdList", newList);

        return "/SRM/thresholdList/newThresholdList";
    }

    @TeacherCreate
    @PostMapping("/new")
    public String postNewThresholdList(Model model, @ModelAttribute("threshold") ThresholdList submittedThresholdList){
        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();

        if (!submittedThresholdList.getUniqueID().isBlank() && thresholdListService.findByUniqueID(submittedThresholdList.getUniqueID()) != null){
            Set<ThresholdList> thresholdListSet =
                    thresholdListService.findAllByUniqueIDContainingIgnoreCase(submittedThresholdList.getUniqueID());
            Optional<ThresholdList> found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst();
            if (found.isPresent()){
                log.debug("Unique ID already in use");
                model.addAttribute("thresholdListFeedback", "Unique id " + submittedThresholdList.getUniqueID() + " already exists");
                model.addAttribute("thresholds", auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll()));
                ThresholdList newList = ThresholdList.builder().thresholds(new HashSet<>()).uploader(currentTeacher).build();
                model.addAttribute("thresholdList", newList);
                return "/SRM/thresholdList/newThresholdList";
            }
        }

        //sync thresholds with this list and save
        Set<Threshold> thresholdSet = submittedThresholdList.getThresholds();
        thresholdSet.forEach(threshold -> {
            threshold.getThresholdLists().add(submittedThresholdList);
            thresholdService.save(threshold);
        });

        submittedThresholdList.setUploader(auxiliaryController.getCurrentTeacherUser());
        ThresholdList saved = thresholdListService.save(submittedThresholdList);
        log.debug("Threshold-list saved");
        model.addAttribute("thresholdList", saved);
        model.addAttribute("thresholds", saved.getThresholds());
        model.addAttribute("thresholdListFeedback", "Threshold-list saved");

        model.addAttribute("teacher", currentTeacher);
        return "/SRM/thresholdList/viewThresholdList";
    }

    @TeacherRead
    @GetMapping("/{id}")
    public String viewThresholdList(Model model, @PathVariable("id") String thresholdListID){
        if (thresholdListService.findById(Long.valueOf(thresholdListID)) == null){
            log.debug("Threshold list not found");
            throw new NotFoundException("Threshold list not found");
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        model.addAttribute("teacher", currentTeacher);

        ThresholdList found = thresholdListService.findById(Long.valueOf(thresholdListID));
        model.addAttribute("thresholdList", found);
        model.addAttribute("thresholds", found.getThresholds());
        return "/SRM/thresholdList/viewThresholdList";
    }
}
