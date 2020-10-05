package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.securityServices.TeacherUserService;
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
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/teachers"})
public class TeacherController {

    private final TeacherUserService teacherUserService;
    private final ContactDetailService contactDetailService;
    private final SubjectService subjectService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listTeachers(Model model, String lastName) {
        if(lastName == null || lastName.isEmpty()){
            model.addAttribute("teachers", sortTeacherSetByLastName(teacherUserService.findAll()));
        } else {
            model.addAttribute("teachers",
                    sortTeacherSetByLastName(teacherUserService.findAllByLastNameContainingIgnoreCase(lastName)));
        }
        return "/SRM/teachers/teacherIndex";
    }

    @TeacherRead
    @GetMapping("/{teacherId}")
    public ModelAndView getTeacherDetails(@PathVariable String teacherId) {
        ModelAndView mav = new ModelAndView("/SRM/teachers/teacherDetails");
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher with ID: " + teacherId + " not found");
            throw new NotFoundException("Teacher not found");
        } else {
            TeacherUser teacher = teacherUserService.findById(Long.valueOf(teacherId));
            Set<Subject> subjectsTaught = new HashSet<>(teacher.getSubjects());
            mav.addObject("subjectsTaught", subjectsTaught);
            mav.addObject("teacher", teacher);
            return mav;
        }
    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewTeacher(Model model) {
        model.addAttribute("teacher", TeacherUser.builder().build());
        return "/SRM/teachers/newTeacher";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewTeacher(@Valid @ModelAttribute("teacher") TeacherUser teacher, BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.info(objectError.toString()));
            return "/SRM/teachers/newTeacher";
        }

        if (teacherUserService.findByFirstNameAndLastName(teacher.getFirstName(), teacher.getLastName()) == null) {
            contactDetailService.save(teacher.getContactDetail());
            TeacherUser savedTeacher = teacherUserService.save(teacher);
            //head straight to the update page to edit other properties
            return "redirect:/teachers/" + savedTeacher.getId() + "/edit";
        } else {
            log.info("Current teacher is already on file");
            TeacherUser found = teacherUserService.findByFirstNameAndLastName(teacher.getFirstName(), teacher.getLastName());
            model.addAttribute("teacher", found);
            model.addAttribute("subjectsTaught", found.getSubjects());
            model.addAttribute("newTeacher", "Teacher already on file, record presented here");
            return "/teachers/updateTeacher";
        }
    }

    @AdminUpdate
    @GetMapping("/{teacherId}/edit")
    public String getUpdateTeacher(@PathVariable String teacherId, Model model) {
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher with ID: " + teacherId + " not found");
            throw new NotFoundException("Teacher not found");
        } else {
            TeacherUser teacherFound = teacherUserService.findById(Long.valueOf(teacherId));
            Set<Subject> subjectList = new HashSet<>(teacherFound.getSubjects());
            model.addAttribute("subjectsTaught", subjectList);
            model.addAttribute("teacher", teacherFound);
            return "/SRM/teachers/updateTeacher";
        }
    }

    @AdminUpdate
    @PostMapping("/{teacherId}/edit")
    public String postUpdateTeacher(@Valid @ModelAttribute("teacher") TeacherUser teacher, BindingResult bindingResult,
                                    @PathVariable String teacherId, Model model) {
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            //ID info would be lost by now
            TeacherUser teacherOnFile = teacherUserService.findById(Long.valueOf(teacherId));
            Set<Subject> subjects = teacherOnFile.getSubjects();
            model.addAttribute("subjectsTaught", subjects);
            teacher.setId(teacherOnFile.getId());
            teacher.setSubjects(subjects);
            teacher.setContactDetail(teacherOnFile.getContactDetail());
            model.addAttribute("teacher", teacher);
            return "/SRM/teachers/updateTeacher";
        }

        //recall all other variables and pass to the DB (Subject properties handled elsewhere)
        TeacherUser teacherOnFile = teacherUserService.findById(Long.valueOf(teacherId));

        teacherOnFile.setFirstName(teacher.getFirstName());
        teacherOnFile.setLastName(teacher.getLastName());
        teacherOnFile.setDepartment(teacher.getDepartment());

        teacherOnFile.setContactDetail(contactDetailService.save(teacher.getContactDetail()));

        TeacherUser savedTeacher = teacherUserService.save(teacherOnFile);
        model.addAttribute("teacher", savedTeacher);
        model.addAttribute("subjectsTaught", savedTeacher.getSubjects());
        model.addAttribute("newTeacher", "Teacher details updated");
        return "/SRM/teachers/teacherDetails";
    }

    @AdminUpdate
    @GetMapping("/{teacherId}/subjects")
    public String getUpdateSubject(Model model, @PathVariable String teacherId){
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher with ID: " + teacherId + " not found");
            throw new NotFoundException("Teacher not found");
        } else {
            model.addAttribute("subjectSet", subjectService.findAll());
            model.addAttribute("teacher", teacherUserService.findById(Long.valueOf(teacherId)));
            return "/SRM/subjects/subjectSet";
        }
    }

    @AdminUpdate
    @PostMapping("/{teacherId}/subjects")
    public String postUpdateSubject(Model model, @PathVariable String teacherId,
                                    @ModelAttribute("teacher") TeacherUser teacher){
        TeacherUser teacherOnFile = teacherUserService.findById(Long.valueOf(teacherId));

        teacherOnFile.setSubjects(teacher.getSubjects());
        teacher.getSubjects().stream().forEach(subject -> {
            subject.getTeachers().add(teacherOnFile);
            subjectService.save(subject);
        });

        TeacherUser saved = teacherUserService.save(teacherOnFile);
        model.addAttribute("teacher", saved);
        model.addAttribute("subjectsTaught", saved.getSubjects());
        model.addAttribute("newTeacher", "Teacher subject details updated");
        return "/SRM/teachers/teacherDetails";
    }

    /**
     * Returns an ArrayList of items, sorted by teachers's lastName
     * */
    @TeacherRead
    private List<TeacherUser> sortTeacherSetByLastName(Set<TeacherUser> teacherUserSet) {
        List<TeacherUser> listByLastName = new ArrayList<>(teacherUserSet);
        //see TeacherUser's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
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
