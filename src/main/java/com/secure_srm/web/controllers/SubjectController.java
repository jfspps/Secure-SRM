package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.model.security.User;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.AdminCreate;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/subjects"})
public class SubjectController {

    private final SubjectService subjectService;
    private final TeacherUserService teacherUserService;
    private final AuxiliaryController auxiliaryController;

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
    public String listSubjects(Model model, String subjectTitle) {
        if(subjectTitle == null || subjectTitle.isEmpty()){
            model.addAttribute("subjects", auxiliaryController.sortSetBySubjectName(subjectService.findAll()));
        } else {
            model.addAttribute("subjects", auxiliaryController.sortSetBySubjectName(subjectService.findBySubjectNameContainingIgnoreCase(subjectTitle)));
        }
        return "/SRM/subjects/subjectIndex";
    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewSubject(Model model) {
        Subject newSubject = Subject.builder().subjectName("").build();
        model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        model.addAttribute("subject", newSubject);
        return "/SRM/subjects/newSubject";
    }

    @TeacherRead
    @GetMapping("/new/teachers/search")
    public String getNewSubject_SearchTeachers(Model model, @ModelAttribute("subject") Subject subjectSubmitted,
                                               String TeacherLastName) {
        if (TeacherLastName == null || TeacherLastName.isEmpty()){
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        } else {
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAllByLastNameContainingIgnoreCase(TeacherLastName)));
        }

        //note that subjectSubmitted is not saved to the DB, and is composed of a new Subject with a blank subject title
        model.addAttribute("subject", subjectSubmitted);
        return "/SRM/subjects/newSubject";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewSubject(@Valid @ModelAttribute("subject") Subject subjectSubmitted,
                                 BindingResult result, Model model) {
        if (result.hasErrors()){
            log.debug("Problems with subject details submitted");
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            model.addAttribute("subject", subjectSubmitted);
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
            return "/SRM/subjects/newSubject";
        }

        //check the subject title is not already on file
        if (subjectService.findBySubjectName(subjectSubmitted.getSubjectName()) != null){
            log.debug("Subject with given title already exists");
            model.addAttribute("newSubjectFeedback", "Subject already exists with given title");
            model.addAttribute("subject", subjectSubmitted);
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
            return "/SRM/subjects/newSubject";
        }

        Subject saved = subjectService.save(subjectSubmitted);

        //update the Teacher's selected
        subjectSubmitted.getTeachers().forEach(teacherUser -> {
            teacherUser.getSubjects().add(saved);
            teacherUserService.save(teacherUser);
        });

        log.debug("New subject saved");
        model.addAttribute("subjectTeachersFeedback", "New subject \"" + saved.getSubjectName() + "\"" + " saved");
        model.addAttribute("subject", saved);
        model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        return "/SRM/subjects/updateSubject";
    }

    @TeacherRead
    @GetMapping("/{subjectID}")
    public String getUpdateSubject(Model model, @PathVariable("subjectID") String subjectId) {
        if (subjectService.findById(Long.valueOf(subjectId)) == null){
            log.debug("Subject not found");
            throw new NotFoundException("Subject not found");
        }

        Subject subjectOnFile = subjectService.findById(Long.valueOf(subjectId));

        // if the subject has no registered teachers then add all to the list
        if (subjectOnFile.getTeachers().isEmpty()){
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        } else {
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(subjectOnFile.getTeachers()));
        }

        model.addAttribute("subject", subjectOnFile);
        return "/SRM/subjects/updateSubject";
    }

    @TeacherRead
    @GetMapping("/{subjectID}/teachers/search")
    public String getUpdateSubject_SearchTeachers(Model model, @PathVariable("subjectID") String subjectId,
                                                  String TeacherLastName) {
        if (subjectService.findById(Long.valueOf(subjectId)) == null){
            log.debug("Subject not found");
            throw new NotFoundException("Subject not found");
        }
        Subject found = subjectService.findById(Long.valueOf(subjectId));
        Set<TeacherUser> teachersOfSubject = found.getTeachers();

        if (TeacherLastName == null || TeacherLastName.isEmpty()){
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teachersOfSubject));
        } else {
            Set<TeacherUser> teachersFound = teachersOfSubject.stream().filter(teacherUser -> teacherUser.getLastName().equals(TeacherLastName)).collect(Collectors.toSet());
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teachersFound));
        }

        model.addAttribute("subject", found);
        return "/SRM/subjects/updateSubject";
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
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
            return "/SRM/subjects/updateSubject";
        }

        Subject subjectOnFile = subjectService.findById(Long.valueOf(subjectId));
        String originalSubjectName = subjectOnFile.getSubjectName();
        subjectOnFile.setSubjectName(originalSubjectName);

        Set<TeacherUser> teachersRemoved = new HashSet<>(subjectOnFile.getTeachers());

        //teacherRemoved contains teachers who have been removed from the Subject's teacherSet
        teachersRemoved.removeIf(subjectSubmitted.getTeachers()::contains);

        //update each teacher in teachersRemoved
        teachersRemoved.forEach(teacherUser -> {
            teacherUser.getSubjects().remove(subjectOnFile);
            teacherUserService.save(teacherUser);
        });

        //update newly added Teacher's subjectSet
        subjectSubmitted.getTeachers().forEach(teacherUser -> {
            teacherUser.getSubjects().add(subjectOnFile);
            teacherUserService.save(teacherUser);
        });

        subjectOnFile.setTeachers(subjectSubmitted.getTeachers());
        Subject saved = subjectService.save(subjectOnFile);

        log.debug("Subject updated");
        // if all teachers removed from subject
        if (saved.getTeachers() == null || saved.getTeachers().isEmpty()){
            model.addAttribute("subjectTeachersFeedback", originalSubjectName + " not assigned any teachers");
        } else {
            model.addAttribute("subjectTeachersFeedback", "\"" + saved.getSubjectName() + "\"" + " updated");
        }

        model.addAttribute("subject", saved);
        model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(saved.getTeachers()));
        return "/SRM/subjects/updateSubject";
    }
}
