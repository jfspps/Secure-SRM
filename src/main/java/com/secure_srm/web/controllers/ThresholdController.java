package com.secure_srm.web.controllers;


import com.secure_srm.model.academic.Threshold;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.ThresholdListService;
import com.secure_srm.services.academicServices.ThresholdService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
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

        TeacherUser teacherUser = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
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

        TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
        submittedThreshold.setUploader(currentTeacher);

        if (thresholdService.findAllByUniqueIDContainingIgnoreCase(submittedThreshold.getUniqueId()) != null){
            Set<Threshold> thresholdsOneFile = thresholdService.findAllByUniqueIDContainingIgnoreCase(submittedThreshold.getUniqueId());
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
        return "/SRM/threshold/thresholdDetails";
    }
}

