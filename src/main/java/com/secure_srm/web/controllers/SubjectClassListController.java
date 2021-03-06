package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.people.SubjectClassList;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.model.security.User;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.peopleServices.SubjectClassListService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.AdminCreate;
import com.secure_srm.web.permissionAnnot.AdminRead;
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
@RequestMapping({"/subjectClassList"})
public class SubjectClassListController {

    private final StudentService studentService;
    private final TeacherUserService teacherUserService;
    private final SubjectClassListService subjectClassListService;
    private final SubjectService subjectService;
    private final AuxiliaryController auxiliaryController;

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("hasSubject")
    public Boolean teachesSubjects(){
        //determines if a User is a teacher and then if they teach anything (blocks New Student Task/Report/Result as appropriate)
        return auxiliaryController.teachesASubject();
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getFormGroupList(Model model) {
        model.addAttribute("subjectClasses", auxiliaryController.sortSubjectClassSetByGroupName(subjectClassListService.findAll()));
        return "/SRM/classLists/subjectClasses";
    }

    @TeacherRead
    @GetMapping("/{groupId}")
    public String getShowSubjectClass(@PathVariable("groupId") String groupID, Model model) {
        checkSubjectExists(groupID);

        SubjectClassList found = subjectClassListService.findById(Long.valueOf(groupID));
        //build a list by lastName, then sort
        List<Student> listByLastName = auxiliaryController.sortStudentSetByLastName(found.getStudentList());
        model.addAttribute("subjectClass", found);
        model.addAttribute("studentList", listByLastName);
        return "/SRM/classLists/subjectClass";

    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewSubjectClass(Model model, String TeacherLastName, String SubjectTitle) {
        refineTeacherAndSubjectLists(model, TeacherLastName, SubjectTitle);

        model.addAttribute("studentSet", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));
        model.addAttribute("subjectClass", SubjectClassList.builder().build());
        return "/SRM/classLists/newSubjectClass";
    }

    @AdminCreate
    @GetMapping("/new/search")
    public String getNewSubjectClass_Search(Model model, @ModelAttribute("subjectClass") SubjectClassList subjectClassList,
                                            String TeacherLastName, String SubjectTitle) {
        refineTeacherAndSubjectLists(model, TeacherLastName, SubjectTitle);

        model.addAttribute("studentSet", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));
        model.addAttribute("subjectClass", subjectClassList);
        return "/SRM/classLists/newSubjectClass";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewSubjectClass(Model model, @Valid @ModelAttribute("subjectClass") SubjectClassList subjectClassList,
                                      BindingResult result) {
        if (SubjectClass_formHasErrors(model, subjectClassList, result)) return "/SRM/classLists/newSubjectClass";

        //check if the groupName already exists
        if (subjectClassListService.findByGroupName(subjectClassList.getGroupName()) != null) {
            log.debug("Class group name submitted already exists");
            model.addAttribute("groupNameFeedback", "Subject class name submitted already exists");
            model.addAttribute("studentSet", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));
            model.addAttribute("subjectClass", subjectClassList);
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
            return "/SRM/classLists/newSubjectClass";
        }

        SubjectClassList saved = subjectClassListService.save(subjectClassList);

        //update each student's subjectClassList registration (overwrite current settings)
        saved.getStudentList().forEach(student -> {
            student.getSubjectClassLists().add(saved);
            studentService.save(student);
        });

        log.debug("New subject class saved");

        model.addAttribute("subjectClass", saved);
        model.addAttribute("studentList", auxiliaryController.sortStudentSetByLastName(saved.getStudentList()));
        model.addAttribute("newList", "New subject class \"" + saved.getGroupName() + "\" saved");
        return "/SRM/classLists/subjectClass";
    }

    @AdminUpdate
    @GetMapping("/{groupId}/edit")
    public String getUpdateSubjectClass(@PathVariable("groupId") String groupID, Model model) {
        checkSubjectExists(groupID);

        model.addAttribute("subjectClass", subjectClassListService.findById(Long.valueOf(groupID)));
        //build a list by lastName, then sort
        List<Student> listByLastName = auxiliaryController.sortStudentSetByLastName(studentService.findAll());
        model.addAttribute("studentSet", listByLastName);
        return "/SRM/classLists/studentsOnFile_subject";
    }

    @AdminRead
    @GetMapping("/{groupId}/search")
    public String getUpdateClassListSearch(@PathVariable("groupId") String groupID, Model model, String StudentLastName) {
        checkSubjectExists(groupID);

        model.addAttribute("subjectClass", subjectClassListService.findById(Long.valueOf(groupID)));
        Set<Student> found = studentService.findAllByLastNameContainingIgnoreCase(StudentLastName);

        //add students who are already registered to the class along with search results
        Set<Student> registered = subjectClassListService.findById(Long.valueOf(groupID)).getStudentList();
        found.addAll(registered);

        List<Student> sorted = auxiliaryController.sortStudentSetByLastName(found);

        model.addAttribute("studentSet", sorted);
        //send back the search string (used for update)
        model.addAttribute("searchQuery", StudentLastName);
        return "/SRM/classLists/studentsOnFile_subject";
    }

    @AdminUpdate
    @PostMapping("/{groupId}/edit")
    public String postUpdateClassList(@PathVariable("groupId") String groupID, Model model,
                                      @Valid @ModelAttribute("subjectClass") SubjectClassList classListSubmitted,
                                      BindingResult result) {
        if (SubjectClass_formHasErrors(model, classListSubmitted, result))
            return "/SRM/classLists/studentsOnFile_subject";

        SubjectClassList listOnFile = subjectClassListService.findById(Long.valueOf(groupID));

        //students listed on the form with checkbox checked stored in classListSubmitted
        //all other students on the DB are not stored

        //remove listOnFile from removed students and update the subjectClassList on file
        Set<Student> allOthers = new HashSet<>(studentService.findAll());
        allOthers.removeIf(classListSubmitted.getStudentList()::contains);

        allOthers.forEach(student -> {
            student.getSubjectClassLists().remove(listOnFile);
            listOnFile.getStudentList().remove(student);
            studentService.save(student);
        });

        //update each added student
        classListSubmitted.getStudentList().forEach(student -> {
            student.getSubjectClassLists().add(listOnFile);
            studentService.save(student);
            listOnFile.getStudentList().add(student);
        });

        SubjectClassList saved = subjectClassListService.save(listOnFile);

        model.addAttribute("subjectClass", saved);
        model.addAttribute("studentList", saved.getStudentList());
        model.addAttribute("newList", "Subject class updated");
        return "/SRM/classLists/subjectClass";
    }


    @AdminUpdate
    private void checkSubjectExists(String groupID) {
        if (subjectClassListService.findById(Long.valueOf(groupID)) == null) {
            log.debug("Subject class not found");
            throw new NotFoundException("Subject class not found");
        }
    }

    @AdminRead
    private void refineTeacherAndSubjectLists(Model model, String TeacherLastName, String SubjectTitle) {
        if (TeacherLastName == null || TeacherLastName.isEmpty()) {
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        } else {
            model.addAttribute("teachers",
                    auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAllByLastNameContainingIgnoreCase(TeacherLastName)));
        }

        if (SubjectTitle == null || SubjectTitle.isEmpty()) {
            model.addAttribute("subjects", auxiliaryController.sortSetBySubjectName(subjectService.findAll()));
        } else {
            model.addAttribute("subjects",
                    auxiliaryController.sortSetBySubjectName(subjectService.findBySubjectNameContainingIgnoreCase(SubjectTitle)));
        }
    }

    @AdminUpdate
    private boolean SubjectClass_formHasErrors(Model model, SubjectClassList subjectClassList, BindingResult result) {
        if (result.hasErrors()) {
            log.debug("Problems with subject class details submitted");
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            model.addAttribute("studentSet", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));
            model.addAttribute("subjectClass", subjectClassList);
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
            model.addAttribute("subjects", auxiliaryController.sortSetBySubjectName(subjectService.findAll()));
            return true;
        }
        return false;
    }
}
