package com.secure_srm.web.controllers;


import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Threshold;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.ThresholdListService;
import com.secure_srm.services.academicServices.ThresholdService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import com.secure_srm.web.permissionAnnot.TeacherUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/thresholds"})
public class ThresholdController {

    private final ThresholdService thresholdService;
    private final ThresholdListService thresholdListService;
    private final AuxiliaryController auxiliaryController;
    private final TeacherUserService teacherUserService;
    private final UserService userService;

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
    public String listThresholds(Model model, String uniqueID) {
        if (uniqueID == null || uniqueID.isBlank()){
            model.addAttribute("thresholds", auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll()));
        } else {
            model.addAttribute("thresholds",
                    auxiliaryController.sortThresholdByUniqueID(thresholdService.findAllByUniqueIDContainingIgnoreCase(uniqueID)));
        }

        return "/SRM/threshold/thresholdIndex";
    }

    @TeacherCreate
    @GetMapping("/new")
    public String getNewThreshold(Model model) {
        if (!auxiliaryController.teachesASubject()){
            model.addAttribute("message", "You are currently not registered to teach anything yet.");
            return "/SRM/customMessage";
        }

        TeacherUser teacherUser = auxiliaryController.getCurrentTeacherUser();
        model.addAttribute("threshold", Threshold.builder().uploader(teacherUser).build());
        return "/SRM/threshold/newThreshold";
    }

    @TeacherCreate
    @PostMapping("/new")
    public String postNewThreshold(Model model, @ModelAttribute("threshold") Threshold submittedThreshold) {
        if (!auxiliaryController.teachesASubject()){
            model.addAttribute("message", "You are currently not registered to teach anything yet.");
            return "/SRM/customMessage";
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        submittedThreshold.setUploader(currentTeacher);

        if (!submittedThreshold.getUniqueId().isBlank() && thresholdService.findAllByUniqueID(submittedThreshold.getUniqueId()) != null){
            Set<Threshold> thresholdsOneFile = thresholdService.findAllByUniqueID(submittedThreshold.getUniqueId());
            Optional<Threshold> found = thresholdsOneFile.stream().filter(threshold -> threshold.getUploader().equals(currentTeacher)).findAny();

            if (found.isPresent()){
                log.debug("Unique ID already in use");
                model.addAttribute("thresholdFeedback", "Unique ID already in use");
                model.addAttribute("threshold", submittedThreshold);
                return "/SRM/threshold/newThreshold";
            }
        }

        //all checks out
        submittedThreshold.setThresholdLists(new HashSet<>());
        Threshold saved = thresholdService.save(submittedThreshold);
        log.debug("New threshold saved");
        model.addAttribute("thresholdFeedback", "New threshold saved");
        model.addAttribute("threshold", saved);
        model.addAttribute("teacher", auxiliaryController.getCurrentTeacherUser());
        return "/SRM/threshold/thresholdDetails";
    }

    @TeacherRead
    @GetMapping("/{id}")
    public String getViewThreshold(@PathVariable("id") String thresholdId, Model model){
        if (thresholdService.findById(Long.valueOf(thresholdId)) == null){
            log.debug("Threshold not found");
            throw new NotFoundException("Threshold not found");
        }

        model.addAttribute("threshold", thresholdService.findById(Long.valueOf(thresholdId)));
        model.addAttribute("teacher", auxiliaryController.getCurrentTeacherUser());
        return "/SRM/threshold/thresholdDetails";
    }

    @TeacherUpdate
    @GetMapping("/{id}/edit")
    public String getUpdateThreshold(@PathVariable("id") String thresholdId, Model model){
        if (thresholdService.findById(Long.valueOf(thresholdId)) == null){
            log.debug("Threshold not found");
            throw new NotFoundException("Threshold not found");
        }

        Threshold onFile = thresholdService.findById(Long.valueOf(thresholdId));
        TeacherUser currentUser = auxiliaryController.getCurrentTeacherUser();

        if (!onFile.getUploader().equals(currentUser)){
            log.debug("Teachers not allowed to edit other teachers' thresholds");
            model.addAttribute("message", "You are only permitted to edit your own thresholds");
            return "/SRM/customMessage";
        }

        model.addAttribute("threshold", onFile);
        return "/SRM/threshold/updateThreshold";
    }

    @TeacherUpdate
    @PostMapping("/{id}/edit")
    public String postUpdateThreshold(@ModelAttribute("threshold") Threshold submittedThreshold, Model model,
                                      @PathVariable("id") String thresholdId){
        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        Threshold onFile = thresholdService.findById(Long.valueOf(thresholdId));

        //allow for the same uniqueID (equivalent to no change)
        if (!onFile.getUniqueId().equals(submittedThreshold.getUniqueId())){
            if (thresholdService.findAllByUniqueID(submittedThreshold.getUniqueId()) != null){
                Set<Threshold> thresholdsOneFile = thresholdService.findAllByUniqueID(submittedThreshold.getUniqueId());
                Optional<Threshold> found = thresholdsOneFile.stream().filter(threshold -> threshold.getUploader().equals(currentTeacher)).findAny();

                if (found.isPresent()){
                    log.debug("Unique ID already in use");
                    model.addAttribute("thresholdFeedback", "Unique ID \"" + submittedThreshold.getUniqueId() + "\" already exists");
                    model.addAttribute("threshold", onFile);
                    return "/SRM/threshold/updateThreshold";
                }
            }
        }

        onFile.setAlphabetical(submittedThreshold.getAlphabetical());
        onFile.setNumerical(submittedThreshold.getNumerical());
        onFile.setUniqueId(submittedThreshold.getUniqueId());
        Threshold saved = thresholdService.save(onFile);
        log.debug("Threshold updated");
        model.addAttribute("thresholdFeedback", "Threshold updated");
        model.addAttribute("threshold", saved);
        model.addAttribute("teacher", auxiliaryController.getCurrentTeacherUser());
        return "/SRM/threshold/thresholdDetails";
    }
}

