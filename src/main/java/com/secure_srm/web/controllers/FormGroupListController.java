package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.services.peopleServices.FormGroupListService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/formGroupList"})
public class FormGroupListController {

    private final FormGroupListService formGroupListService;
    private final StudentService studentService;

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getFormGroupList(Model model){
        model.addAttribute("formGroups", formGroupListService.findAll());
        return "/SRM/classLists/formGroups";
    }

    @TeacherRead
    @GetMapping("/{groupId}")
    public String getShowFormGroup(@PathVariable("groupId") String groupID,  Model model){
        if (formGroupListService.findById(Long.valueOf(groupID)) == null){
            log.debug("Form group not found");
            throw new NotFoundException("Form group not found");
        } else {
            FormGroupList found = formGroupListService.findById(Long.valueOf(groupID));
            //build a list by lastName, then sort
            List<Student> listByLastName = sortSetByLastName(found.getStudentList());
            model.addAttribute("formGroup", found);
            model.addAttribute("studentList", listByLastName);
            return "/SRM/classLists/form";
        }
    }

    @TeacherRead
    @GetMapping("/{groupId}/edit")
    public String getUpdateFormGroup(@PathVariable("groupId") String groupID,  Model model){
        if (formGroupListService.findById(Long.valueOf(groupID)) == null){
            log.debug("Form group not found");
            throw new NotFoundException("Form group not found");
        } else {
            model.addAttribute("formGroup", formGroupListService.findById(Long.valueOf(groupID)));
            //build a list by lastName, then sort
            List<Student> listByLastName = sortSetByLastName(studentService.findAll());
            model.addAttribute("studentSet", listByLastName);
            return "/SRM/classLists/studentsOnFile";
        }
    }

    @TeacherRead
    @GetMapping("/{groupId}/search")
    public String getUpdateFormGroupSearch(@PathVariable("groupId") String groupID,  Model model, String StudentLastName){
        if (formGroupListService.findById(Long.valueOf(groupID)) == null){
            log.debug("Form group not found");
            throw new NotFoundException("Form group not found");
        } else {
            model.addAttribute("formGroup", formGroupListService.findById(Long.valueOf(groupID)));
            //build a list by lastName, then sort
            List<Student> listByLastName = sortSetByLastName(studentService.findAllByLastNameLike(StudentLastName));
            model.addAttribute("studentSet", listByLastName);
            return "/SRM/classLists/studentsOnFile";
        }
    }

    @TeacherRead
    @PostMapping("/{groupId}/edit")
    public String postUpdateFormGroup(@PathVariable("groupId") String groupID,  Model model,
                                      @ModelAttribute("formGroup") FormGroupList formGroupList){

        FormGroupList listOnFile = formGroupListService.findById(Long.valueOf(groupID));

        //copy listOnFile student set and remove those from formGroupList
        Set<Student> removed = new HashSet<>(listOnFile.getStudentList());
        removed.removeIf(formGroupList.getStudentList()::contains);

        //clear removed students' formGroupList property and update the formGroupList on file
        removed.forEach(student -> {
            student.setFormGroupList(null);
            student.setTeacher(null);
            listOnFile.getStudentList().remove(student);
            studentService.save(student);
        });

        //update each added student
        formGroupList.getStudentList().forEach(student -> {
            student.setFormGroupList(listOnFile);
            student.setTeacher(listOnFile.getTeacher());
            studentService.save(student);
        });

        listOnFile.setStudentList(formGroupList.getStudentList());
        FormGroupList saved = formGroupListService.save(listOnFile);

        model.addAttribute("formGroup", saved);
        model.addAttribute("studentList", saved.getStudentList());
        model.addAttribute("newList", "Form group updated");
        return "/SRM/classLists/form";
    }

    /**
     * Returns an ArrayList of items, sorted by student's lastName
     * */
    @TeacherRead
    private List<Student> sortSetByLastName(Set<Student> studentSet) {
        List<Student> listByLastName = new ArrayList<>(studentSet);
        //see Student's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }
}
