package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.people.SubjectClassList;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.model.security.User;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.peopleServices.FormGroupListService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.peopleServices.SubjectClassListService;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/students"})
public class StudentController {

    private final StudentService studentService;
    private final GuardianUserService guardianUserService;
    private final TeacherUserService teacherUserService;
    private final SubjectClassListService subjectClassListService;
    private final FormGroupListService formGroupListService;

    private final ContactDetailService contactDetailService;
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
    public String listStudents(Model model, String lastName) {
        if(lastName == null || lastName.isEmpty()){
            model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));
        } else {
            model.addAttribute("students",
                    auxiliaryController.sortStudentSetByLastName(studentService.findAllByLastNameContainingIgnoreCase(lastName)));
        }
        return "/SRM/students/studentIndex";
    }

    @TeacherRead
    @GetMapping("/{studentID}")
    public String getStudentDetails(@PathVariable String studentID, Model model){
        checkStudentID(studentID);

        Student found = studentService.findById(Long.valueOf(studentID));
        updateStudentModel(model, found);
        return "/SRM/students/studentDetails";

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
            contactDetailService.save(student.getContactDetail());
            Student savedStudent = studentService.save(student);
            //head straight to the update page to edit other properties
            return "redirect:/students/" + savedStudent.getId() + "/edit";
        } else {
            log.info("Current student is already on file");
            model.addAttribute("newStudent", "Student already on file, record presented here");
            Student found = studentService.findByFirstLastAndMiddleNames(
                            student.getFirstName(), student.getLastName(), student.getMiddleNames());
            updateStudentModel(model, found);
            return "/SRM/students/studentDetails";
        }
    }

    @AdminUpdate
    @GetMapping("/{studentID}/edit")
    public String getUpdateStudent(@PathVariable String studentID, Model model) {
        checkStudentID(studentID);

        model.addAttribute("student", studentService.findById(Long.valueOf(studentID)));
        return "/SRM/students/updateStudent";
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

        studentOnFile.setContactDetail(contactDetailService.save(student.getContactDetail()));

        Student savedStudent = studentService.save(studentOnFile);

        updateStudentModel(model, savedStudent);
        model.addAttribute("newStudent", "Changes saved to the database");
        return "/SRM/students/studentDetails";
    }

    @AdminUpdate
    @GetMapping("/{studentId}/addRemoveGuardians")
    public String getStudent_guardianSet(Model model, @PathVariable String studentId){
        checkStudentID(studentId);

        model.addAttribute("guardianSet", auxiliaryController.sortGuardianSetByLastName(guardianUserService.findAll()));
        model.addAttribute("student", studentService.findById(Long.valueOf(studentId)));
        return "/SRM/students/guardianSet";
    }

    //search function to refine list of Guardians registered to Student
    @AdminUpdate
    @GetMapping("/{studentId}/addRemoveGuardians/search")
    public String getRefineGuardianList(Model model, @PathVariable String studentId, String GuardianLastName){
        checkStudentID(studentId);

        if (GuardianLastName == null || GuardianLastName.isEmpty()) {
            model.addAttribute("guardianSet", auxiliaryController.sortGuardianSetByLastName(guardianUserService.findAll()));
        } else {
            model.addAttribute("guardianSet",
                    auxiliaryController.sortGuardianSetByLastName(guardianUserService.findAllByLastNameContainingIgnoreCase(GuardianLastName)));
        }

        model.addAttribute("student", studentService.findById(Long.valueOf(studentId)));
        return "/SRM/students/guardianSet";
    }

    @AdminUpdate
    @PostMapping("/{studentId}/addRemoveGuardians")
    public String postStudent_guardianSet(@ModelAttribute("student") Student student, @PathVariable String studentId,
                                          Model model) {

        Student studentOnFile = studentService.findById(Long.valueOf(studentId));

        //new Set of guardians removed
        Set<GuardianUser> removedGuardians = new HashSet<>(studentOnFile.getGuardians());
        removedGuardians.removeIf(student.getGuardians()::contains);

        //remove this student from removedGuardians' guardians and save
        removedGuardians.forEach(guardianUser -> {
            guardianUser.getStudents().remove(studentOnFile);
            guardianUserService.save(guardianUser);
        });

        //overwrite studentOnFile's guardian set
        studentOnFile.setGuardians(student.getGuardians());

        //sync Guardians with new set of
        student.getGuardians().stream().forEach(guardianUser -> {
            guardianUser.getStudents().add(studentOnFile);
            guardianUserService.save(guardianUser);
        });

        Student saved = studentService.save(studentOnFile);

        updateStudentModel(model, saved);
        model.addAttribute("newStudent", "Registered guardians updated");
        return "/SRM/students/studentDetails";
    }

    @AdminUpdate
    @GetMapping("/{studentId}/addRemoveTutor")
    public String getChangeTutor(Model model, @PathVariable String studentId){
        checkStudentID(studentId);

        model.addAttribute("teacherSet", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        model.addAttribute("student", studentService.findById(Long.valueOf(studentId)));
        return "/SRM/students/personalTutor";
    }

    //search function to refine list of Teeachers who can be assigned as the tutor
    @AdminUpdate
    @GetMapping("/{studentId}/addRemoveTutor/search")
    public String getRefineTutorList(Model model, @PathVariable String studentId, String TutorLastName){
        checkStudentID(studentId);

        if (TutorLastName == null || TutorLastName.isEmpty()) {
            model.addAttribute("teacherSet", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        } else {
            model.addAttribute("teacherSet",
                    auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAllByLastNameContainingIgnoreCase(TutorLastName)));
        }

        model.addAttribute("student", studentService.findById(Long.valueOf(studentId)));
        return "/SRM/students/personalTutor";
    }

    @AdminUpdate
    @PostMapping("/{studentId}/addRemoveTutor")
    public String postChangeTutor(@ModelAttribute("student") Student student, @PathVariable String studentId,
                                          Model model) {
        Student studentOnFile = studentService.findById(Long.valueOf(studentId));

        //assign teacher to Student (currently, TeacherUser is not mapped to Student)
        studentOnFile.setTeacher(student.getTeacher());

        Student saved = studentService.save(studentOnFile);

        updateStudentModel(model, saved);
        model.addAttribute("newStudent", "Tutor assignment updated");
        return "/SRM/students/studentDetails";
    }

    @AdminUpdate
    @PostMapping("/{studentId}/removeTutor")
    public String postRemoveTutor(@PathVariable String studentId, Model model) {
        Student studentOnFile = studentService.findById(Long.valueOf(studentId));

        studentOnFile.setTeacher(null);
        Student saved = studentService.save(studentOnFile);

        updateStudentModel(model, saved);
        model.addAttribute("newStudent", "Tutor assignment removed");
        return "/SRM/students/studentDetails";
    }

    @AdminUpdate
    @GetMapping("/{studentId}/anon")
    public String getAnonStudent(@PathVariable String studentId, Model model){
        Student studentOnFile = studentService.findById(Long.valueOf(studentId));

        updateStudentModel(model, studentOnFile);

        model.addAttribute("student", studentOnFile);
        return "/SRM/students/confirmAnon";
    }

    @AdminDelete
    @PostMapping("/{studentId}/anon")
    public String postAnonStudent(@PathVariable String studentId, Model model){
        Student studentOnFile = studentService.findById(Long.valueOf(studentId));
        String reply = "Student, " + studentOnFile.getFirstName() + " " + studentOnFile.getLastName() + ", anonymised.";

        anonymizeStudent(studentOnFile);

        model.addAttribute("reply", reply);
        return "/SRM/deleteConfirmed";
    }

    @TeacherRead
    private void checkStudentID(String studentID) {
        try{
            if (studentService.findById(Long.valueOf(studentID)) == null) {
                log.debug("Student with ID: " + studentID + " not found");
                throw new NotFoundException("Student not found");
            }
        } catch (NumberFormatException err){
            log.debug("Long number format not submitted");
            throw new NotFoundException("Student not found");
        }
    }

    @TeacherRead
    private void updateStudentModel(Model model, Student found) {
        model.addAttribute("student", found);
        model.addAttribute("guardians", found.getGuardians());
        model.addAttribute("contactDetail", found.getContactDetail());
        model.addAttribute("teacher", found.getTeacher());
        model.addAttribute("formGroupList", found.getFormGroupList());
        model.addAttribute("subjectClassLists", found.getSubjectClassLists());
    }

    /**
     * Assigns the user name fields of a student with generic, non-identifying strings
     */
    @AdminDelete
    public Student anonymizeStudent(Student student){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd-hhmm:ss");
        student.setFirstName("Anon_" + dateFormat.format(date));
        student.setMiddleNames(" ");
        student.setLastName("Anon_" + dateFormat.format(date));

        Set<GuardianUser> guardianUserSet = student.getGuardians();
        student.setContactDetail(null);
        student.setGuardians(null);

        // remove guardian references and all traces of contact details
        guardianUserSet.forEach(guardianUser -> {
            guardianUser.getStudents().remove(student);

            // remove guardian if dangling
            if (guardianUser.getStudents().isEmpty()){
                // this also deletes matching contact details
                auxiliaryController.deleteGuardianRecord(guardianUser);
            } else {
                guardianUserService.save(guardianUser);
            }
        });

        //update tutor, subject and class lists (refresh all class lists separately)
        student.setTeacher(null);

        Set<SubjectClassList> subjectClassLists = student.getSubjectClassLists();
        student.setSubjectClassLists(null);
        subjectClassLists.forEach(subjectClassList -> {
            subjectClassList.getStudentList().remove(student);
            subjectClassListService.save(subjectClassList);
        });

        FormGroupList formGroupList = student.getFormGroupList();
        student.setFormGroupList(null);
        formGroupList.getStudentList().remove(student);
        formGroupListService.save(formGroupList);

        student.setAnonymised(true);

        return studentService.save(student);
    }
}
