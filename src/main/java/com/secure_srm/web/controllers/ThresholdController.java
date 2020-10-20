package com.secure_srm.web.controllers;


import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.ThresholdListService;
import com.secure_srm.services.academicServices.ThresholdService;
import com.secure_srm.services.securityServices.TeacherUserService;
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
@RequestMapping({"/thresholds"})
public class ThresholdController {

    private final ThresholdService thresholdService;
    private final ThresholdListService thresholdListService;
    private final AuxiliaryController auxiliaryController;
    private final TeacherUserService teacherUserService;

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
}

