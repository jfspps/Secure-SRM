package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.people.SubjectClassList;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.peopleServices.SubjectClassListService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.AdminRead;
import com.secure_srm.web.permissionAnnot.AdminUpdate;
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
@RequestMapping({"/subjectClassList"})
public class SubjectClassListController {

    private final StudentService studentService;
    private final TeacherUserService teacherUserService;
    private final SubjectClassListService subjectClassListService;

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getFormGroupList(Model model){
        model.addAttribute("subjectClasses", sortSubjectClassSetByGroupName(subjectClassListService.findAll()));
        return "/SRM/classLists/subjectClasses";
    }

    @TeacherRead
    @GetMapping("/{groupId}")
    public String getShowSubjectClass(@PathVariable("groupId") String groupID, Model model){
        if (subjectClassListService.findById(Long.valueOf(groupID)) == null){
            log.debug("Subject class not found");
            throw new NotFoundException("Subject class not found");
        } else {
            SubjectClassList found = subjectClassListService.findById(Long.valueOf(groupID));
            //build a list by lastName, then sort
            List<Student> listByLastName = sortStudentSetByLastName(found.getStudentList());
            model.addAttribute("subjectClass", found);
            model.addAttribute("studentList", listByLastName);
            return "/SRM/classLists/subjectClass";
        }
    }

    @AdminUpdate
    @GetMapping("/{groupId}/edit")
    public String getUpdateSubjectClass(@PathVariable("groupId") String groupID,  Model model){
        if (subjectClassListService.findById(Long.valueOf(groupID)) == null){
            log.debug("Subject class not found");
            throw new NotFoundException("Subject class not found");
        } else {
            model.addAttribute("subjectClass", subjectClassListService.findById(Long.valueOf(groupID)));
            //build a list by lastName, then sort
            List<Student> listByLastName = sortStudentSetByLastName(studentService.findAll());
            model.addAttribute("studentSet", listByLastName);
            return "/SRM/classLists/studentsOnFile_subject";
        }
    }

    @AdminRead
    @GetMapping("/{groupId}/search")
    public String getUpdateClassListSearch(@PathVariable("groupId") String groupID, Model model, String StudentLastName){
        if (subjectClassListService.findById(Long.valueOf(groupID)) == null){
            log.debug("Subject class not found");
            throw new NotFoundException("Subject class not found");
        } else {
            model.addAttribute("subjectClass", subjectClassListService.findById(Long.valueOf(groupID)));
            //build a list by lastName, then sort
            List<Student> listByLastName = sortStudentSetByLastName(studentService.findAllByLastNameLike(StudentLastName));
            model.addAttribute("studentSet", listByLastName);
            return "/SRM/classLists/studentsOnFile_subject";
        }
    }

    @AdminUpdate
    @PostMapping("/{groupId}/edit")
    public String postUpdateClassList(@PathVariable("groupId") String groupID,  Model model,
                                      @ModelAttribute("formGroup") SubjectClassList subjectClassList){

        SubjectClassList listOnFile = subjectClassListService.findById(Long.valueOf(groupID));

        //copy listOnFile student set and remove those from subjectClassList
        Set<Student> removed = new HashSet<>(listOnFile.getStudentList());
        removed.removeIf(subjectClassList.getStudentList()::contains);

        //clear removed students' subjectClassList property and update the subjectClassList on file
        removed.forEach(student -> {
            student.getSubjectClassLists().remove(listOnFile);
            listOnFile.getStudentList().remove(student);
            studentService.save(student);
        });

        //update each added student
        subjectClassList.getStudentList().forEach(student -> {
            student.getSubjectClassLists().add(listOnFile);
            studentService.save(student);
        });

        listOnFile.setStudentList(subjectClassList.getStudentList());
        SubjectClassList saved = subjectClassListService.save(listOnFile);

        model.addAttribute("subjectClass", saved);
        model.addAttribute("studentList", saved.getStudentList());
        model.addAttribute("newList", "Subject class updated");
        return "/SRM/classLists/subjectClass";
    }

    /**
     * Returns an ArrayList of items, sorted by Groupname
     * */
    @TeacherRead
    private List<SubjectClassList> sortSubjectClassSetByGroupName(Set<SubjectClassList> subjectClassListSet) {
        List<SubjectClassList> listByGroupName = new ArrayList<>(subjectClassListSet);
        //see SubjectClassList's model string comparison method, compareTo()
        Collections.sort(listByGroupName);
        return listByGroupName;
    }

    /**
     * Returns an ArrayList of items, sorted by student's lastName
     * */
    @TeacherRead
    private List<Student> sortStudentSetByLastName(Set<Student> studentSet) {
        List<Student> listByLastName = new ArrayList<>(studentSet);
        //see Student's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }
}
