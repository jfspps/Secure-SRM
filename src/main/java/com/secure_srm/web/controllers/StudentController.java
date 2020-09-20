package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/students"})
public class StudentController {

    private final StudentService studentService;
    private final GuardianUserService guardianUserService;
    private final TeacherUserService teacherUserService;
    private final SubjectService subjectService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listStudents(Model model, String lastName) {
        if(lastName == null || lastName.isEmpty()){
            model.addAttribute("students", studentService.findAll());
        } else {
            model.addAttribute("students", studentService.findAllByLastNameContainingIgnoreCase(lastName));
        }
        return "/SRM/students/studentIndex";
    }

    @TeacherRead
    @GetMapping("/{studentID}")
    public String getStudentDetails(@PathVariable String studentID, Model model){
        if (studentService.findById(Long.valueOf(studentID)) == null){
            log.debug("Student with ID: " + studentID + " not found");
            throw new NotFoundException("Student not found");
        } else {
            Student found = studentService.findById(Long.valueOf(studentID));
            model.addAttribute("student", found);
            model.addAttribute("guardians", found.getGuardians());
            model.addAttribute("contactDetail", found.getContactDetail());
            model.addAttribute("teacher", found.getTeacher());
            model.addAttribute("formGroupList", found.getFormGroupList());
            model.addAttribute("subjectClassLists", found.getSubjectClassLists());
            return "/SRM/students/studentDetails";
        }
    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewStudent(Model model) {
        model.addAttribute("student", Student.builder().build());
        return "/SRM/students/newStudent";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewStudent(@Valid @ModelAttribute("student") Student student, BindingResult bindingResult,
                                             Model model) {
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return "/SRM/students/newStudent";
        }

        // save() handles the id allocation (no further intervention needed for new saves)
        if (studentService.findByFirstLastAndMiddleNames(
                student.getFirstName(), student.getLastName(), student.getMiddleNames()) == null) {
            Student savedStudent = studentService.save(student);
            //head straight to the update page to edit other properties
            return "redirect:/students/" + savedStudent.getId() + "/edit";
        } else {
            log.info("Current student is already on file");
            model.addAttribute("newStudent", "Student already on file, record presented here");
            Student found = studentService.findByFirstLastAndMiddleNames(
                            student.getFirstName(), student.getLastName(), student.getMiddleNames());
            model.addAttribute("student", found);
            model.addAttribute("guardians", found.getGuardians());
            model.addAttribute("contactDetail", found.getContactDetail());
            model.addAttribute("teacher", found.getTeacher());
            model.addAttribute("formGroupList", found.getFormGroupList());
            model.addAttribute("subjectClassLists", found.getSubjectClassLists());
            return "/SRM/students/studentDetails";
        }
    }

    @AdminUpdate
    @GetMapping("/{studentID}/edit")
    public String getUpdateStudent(@PathVariable String studentID, Model model) {
        if (studentService.findById(Long.valueOf(studentID)) == null){
            log.debug("Student with ID: " + studentID + " not found");
            throw new NotFoundException("Student not found");
        } else {
            model.addAttribute("student", studentService.findById(Long.valueOf(studentID)));
            return "/SRM/students/updateStudent";
        }
    }

    @AdminUpdate
    @PostMapping("/{studentId}/edit")
    public String postUpdateStudent(@Valid @ModelAttribute("student") Student student,
                                           BindingResult bindingResult, @PathVariable String studentId, Model model) {
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            Student studentOnFile = studentService.findById(Long.valueOf(studentId));
            student.setId(studentOnFile.getId());
            student.setGuardians(studentOnFile.getGuardians());
            student.setContactDetail(studentOnFile.getContactDetail());
            student.setTeacher(studentOnFile.getTeacher());
            model.addAttribute("student", student);
            return "/SRM/students/updateStudent";
        }

        Student studentOnFile = studentService.findById(Long.valueOf(studentId));
        studentOnFile.setFirstName(student.getFirstName());
        studentOnFile.setMiddleNames(student.getMiddleNames());
        studentOnFile.setLastName(student.getLastName());
        Student savedStudent = studentService.save(studentOnFile);

        model.addAttribute("student", savedStudent);
        model.addAttribute("guardians", savedStudent.getGuardians());
        model.addAttribute("contactDetail", savedStudent.getContactDetail());
        model.addAttribute("teacher", savedStudent.getTeacher());
        model.addAttribute("formGroupList", savedStudent.getFormGroupList());
        model.addAttribute("subjectClassLists", savedStudent.getSubjectClassLists());
        model.addAttribute("newStudent", "Changes saved to the database");
        return "/SRM/students/studentDetails";
    }
}
