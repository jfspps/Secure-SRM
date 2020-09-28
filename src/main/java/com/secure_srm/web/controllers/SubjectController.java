package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.AdminUpdate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/subjects"})
public class SubjectController {

    private final SubjectService subjectService;
    private final TeacherUserService teacherUserService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listSubjects(Model model, String subjectTitle) {
        if(subjectTitle == null || subjectTitle.isEmpty()){
            model.addAttribute("subjects", subjectService.findAll());
        } else {
            model.addAttribute("subjects", subjectService.findBySubjectNameContainingIgnoreCase(subjectTitle));
        }
        return "/SRM/academicRecords/subjectIndex";
    }

    @TeacherRead
    @GetMapping("/{subjectID}")
    public String getUpdateSubject(Model model, @PathVariable("subjectID") String subjectId) {
        if (subjectService.findById(Long.valueOf(subjectId)) == null){
            log.debug("Subject not found");
            throw new NotFoundException("Subject not found");
        }

        Subject subjectOnFile = subjectService.findById(Long.valueOf(subjectId));
        model.addAttribute("subject", subjectOnFile);
        return "/SRM/academicRecords/subjectTeachers";
    }

    @AdminUpdate
    @PostMapping("/{subjectID}/teachers")
    public String postUpdateSubject(Model model, @PathVariable("subjectID") String subjectId,
        @Valid @ModelAttribute("subject") Subject subjectSubmitted, BindingResult result) {
        if (result.hasErrors()){
            log.debug("Problems with subject details submitted");
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            Subject onFile = subjectService.findById(Long.valueOf(subjectId));
            subjectSubmitted.setId(Long.valueOf(subjectId));
            subjectSubmitted.setSubjectName(onFile.getSubjectName());
            subjectSubmitted.setTeachers(onFile.getTeachers());
            model.addAttribute("subject", subjectSubmitted);
            return "/SRM/academicRecords/subjectTeachers";
        }

        Subject subjectOnFile = subjectService.findById(Long.valueOf(subjectId));
        subjectOnFile.setSubjectName(subjectSubmitted.getSubjectName());

        Set<TeacherUser> teachersRemoved = new HashSet<>(subjectOnFile.getTeachers());

        //teacherRemoved contains teachers who have been removed from the Subject's teacherSet
        teachersRemoved.removeIf(subjectSubmitted.getTeachers()::contains);

        //update each teacher in teachersRemoved
        teachersRemoved.forEach(teacherUser -> {
            teacherUser.getSubjects().remove(subjectOnFile);
            teacherUserService.save(teacherUser);
        });

        subjectOnFile.setTeachers(subjectSubmitted.getTeachers());
        Subject saved = subjectService.save(subjectOnFile);

        //not adding teachers currently, so no need to updated newly added Teacher's SubjectSet

        model.addAttribute("subjectTeachersFeedback", "\"" + saved.getSubjectName() + "\"" + " updated");
        model.addAttribute("subject", saved);
        return "/SRM/academicRecords/subjectTeachers";
    }
}
