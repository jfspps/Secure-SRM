package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.TeacherDelete;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import com.secure_srm.web.permissionAnnot.TeacherUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @TeacherUpdate
    @GetMapping("/{studentID}/edit")
    public String getStudentDetailsEdit(@PathVariable String studentID, Model model) {
        if (studentService.findById(Long.valueOf(studentID)) == null){
            log.debug("Student with ID: " + studentID + " not found");
            throw new NotFoundException("Student not found");
        } else {
            Student found = studentService.findById(Long.valueOf(studentID));
            model.addAttribute("student", studentService.findById(Long.valueOf(studentID)));
            return "/SRM/students/updateStudent";
        }
    }

    @TeacherUpdate
    @PostMapping("/{studentId}/edit")
    public String processStudentUpdateForm(@Valid @ModelAttribute("student") Student student,
                                           BindingResult bindingResult, @PathVariable String studentId, Model model) {
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return "/SRM/students/updateStudent";
        }

        //recall all other variables and pass to the DB
        Student studentOnFile = studentService.findById(Long.valueOf(studentId));
        studentOnFile.setFirstName(student.getFirstName());
        studentOnFile.setLastName(student.getLastName());

        Student savedStudent = studentService.save(studentOnFile);
        return "redirect:/students/" + savedStudent.getId();
    }
}
