package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.peopleServices.FormGroupListService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.TeacherUserService;
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

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/formGroupList"})
public class FormGroupListController {

    private final FormGroupListService formGroupListService;
    private final StudentService studentService;
    private final TeacherUserService teacherUserService;

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getFormGroupList(Model model) {
        model.addAttribute("formGroups", sortFormGroupSetByGroupName(formGroupListService.findAll()));
        return "/SRM/classLists/formGroups";
    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewFormGroup(Model model, String TeacherLastName) {
        updateTutorList(model, TeacherLastName);
        model.addAttribute("studentSet", sortStudentSetByLastName(studentService.findAll()));
        model.addAttribute("formGroup", FormGroupList.builder().build());
        return "/SRM/classLists/newFormGroup";
    }

    @AdminCreate
    @GetMapping("/new/teachers/search")
    public String getNewFormGroup_findTeachers(Model model, @ModelAttribute("formGroup") FormGroupList formGroupList,
                                               String TeacherLastName) {
        updateTutorList(model, TeacherLastName);
        model.addAttribute("studentSet", sortStudentSetByLastName(studentService.findAll()));
        model.addAttribute("formGroup", formGroupList);
        return "/SRM/classLists/newFormGroup";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewFormGroup(Model model, @Valid @ModelAttribute("formGroup") FormGroupList formGroupList,
                                   BindingResult result) {
        if (FormGroup_FormHasErrors(model, formGroupList, result)) return "/SRM/classLists/newFormGroup";

        //check if the groupName already exists
        if (formGroupListService.findByGroupName(formGroupList.getGroupName()) != null) {
            log.debug("Form group name submitted already exists");
            model.addAttribute("groupNameFeedback", "Form group name submitted already exists");
            model.addAttribute("studentSet", sortStudentSetByLastName(studentService.findAll()));
            model.addAttribute("formGroup", formGroupList);
            model.addAttribute("teachers", sortTeacherSetByLastName(teacherUserService.findAll()));
            return "/SRM/classLists/newFormGroup";
        }

        FormGroupList saved = formGroupListService.save(formGroupList);

        //update each student's formGroup registration (overwrite current settings)
        saved.getStudentList().forEach(student -> {
            student.setFormGroupList(saved);
            studentService.save(student);
        });

        log.debug("New form group saved");

        model.addAttribute("formGroup", saved);
        model.addAttribute("studentList", sortStudentSetByLastName(saved.getStudentList()));
        model.addAttribute("newList", "New form group \"" + saved.getGroupName() + "\" saved");
        return "/SRM/classLists/form";
    }

    @TeacherRead
    @GetMapping("/{groupId}")
    public String getShowFormGroup(@PathVariable("groupId") String groupID, Model model) {
        checkFormGroupExists(groupID);

        FormGroupList found = formGroupListService.findById(Long.valueOf(groupID));
        //build a list by lastName, then sort
        List<Student> listByLastName = sortStudentSetByLastName(found.getStudentList());
        model.addAttribute("formGroup", found);
        model.addAttribute("studentList", listByLastName);
        return "/SRM/classLists/form";

    }

    @AdminUpdate
    @GetMapping("/{groupId}/edit")
    public String getUpdateFormGroup(@PathVariable("groupId") String groupID, Model model) {
        checkFormGroupExists(groupID);

        model.addAttribute("formGroup", formGroupListService.findById(Long.valueOf(groupID)));
        //build a list by lastName, then sort
        List<Student> listByLastName = sortStudentSetByLastName(studentService.findAll());
        model.addAttribute("studentSet", listByLastName);
        return "/SRM/classLists/studentsOnFile";
    }

    @AdminRead
    @GetMapping("/{groupId}/search")
    public String getUpdateFormGroupSearch(@PathVariable("groupId") String groupID, Model model, String StudentLastName) {
        checkFormGroupExists(groupID);

        model.addAttribute("formGroup", formGroupListService.findById(Long.valueOf(groupID)));
        Set<Student> found = studentService.findAllByLastNameContainingIgnoreCase(StudentLastName);

        //add students who are already registered to the class along with search results
        Set<Student> registered = formGroupListService.findById(Long.valueOf(groupID)).getStudentList();
        found.addAll(registered);

        List<Student> sorted = sortStudentSetByLastName(found);
        model.addAttribute("searchQuery", StudentLastName);
        model.addAttribute("studentSet", sorted);
        return "/SRM/classLists/studentsOnFile";
    }

    @AdminUpdate
    @PostMapping("/{groupId}/edit")
    public String postUpdateFormGroup(@PathVariable("groupId") String groupID, Model model,
                                      @Valid @ModelAttribute("formGroup") FormGroupList formGroupList,
                                      BindingResult result) {
        if (FormGroup_FormHasErrors(model, formGroupList, result)) return "/SRM/classLists/studentsOnFile";

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

    @AdminUpdate
    private boolean FormGroup_FormHasErrors(Model model, FormGroupList formGroupList, BindingResult result) {
        if (result.hasErrors()) {
            log.debug("Problems with form group details submitted");
            result.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            model.addAttribute("studentSet", sortStudentSetByLastName(studentService.findAll()));
            model.addAttribute("formGroup", formGroupList);
            model.addAttribute("teachers", sortTeacherSetByLastName(teacherUserService.findAll()));
            return true;
        }
        return false;
    }

    @AdminUpdate
    private void checkFormGroupExists(String groupID) {
        if (formGroupListService.findById(Long.valueOf(groupID)) == null) {
            log.debug("Form group not found");
            throw new NotFoundException("Form group not found");
        }
    }

    @AdminRead
    private void updateTutorList(Model model, String TeacherLastName) {
        if (TeacherLastName == null || TeacherLastName.isEmpty()) {
            model.addAttribute("teachers", sortTeacherSetByLastName(teacherUserService.findAll()));
        } else {
            model.addAttribute("teachers",
                    sortTeacherSetByLastName(teacherUserService.findAllByLastNameContainingIgnoreCase(TeacherLastName)));
        }
    }

    /**
     * Returns an ArrayList of items, sorted by student's lastName
     */
    @TeacherRead
    private List<Student> sortStudentSetByLastName(Set<Student> studentSet) {
        List<Student> listByLastName = new ArrayList<>(studentSet);
        //see Student's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }

    /**
     * Returns an ArrayList of items, sorted by Teachers's lastName
     */
    @TeacherRead
    private List<TeacherUser> sortTeacherSetByLastName(Set<TeacherUser> teacherUserSet) {
        List<TeacherUser> listByLastName = new ArrayList<>(teacherUserSet);
        //see Teacher's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }

    /**
     * Returns an ArrayList of items, sorted by Groupname
     */
    @TeacherRead
    private List<FormGroupList> sortFormGroupSetByGroupName(Set<FormGroupList> formGroupListSet) {
        List<FormGroupList> listByLastName = new ArrayList<>(formGroupListSet);
        //see FormGroupList's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }
}
