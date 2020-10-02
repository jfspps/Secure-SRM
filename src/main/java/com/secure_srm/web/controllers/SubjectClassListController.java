package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.people.SubjectClassList;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.peopleServices.SubjectClassListService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    public String getShowFormGroup(@PathVariable("groupId") String groupID, Model model){
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
