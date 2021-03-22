package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.Role;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.model.security.User;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.securityServices.RoleService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.AdminCreate;
import com.secure_srm.web.permissionAnnot.AdminUpdate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/teachers"})
public class TeacherController {

    private final TeacherUserService teacherUserService;
    private final ContactDetailService contactDetailService;
    private final SubjectService subjectService;
    private final AuxiliaryController auxiliaryController;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("hasSubject")
    public Boolean teachesSubjects() {
        //determines if a User is a teacher and then if they teach anything (blocks New Student Task/Report/Result as appropriate)
        return auxiliaryController.teachesASubject();
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listTeachers(Model model, String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            model.addAttribute("teachers", auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAll()));
        } else {
            model.addAttribute("teachers",
                    auxiliaryController.sortTeacherSetByLastName(teacherUserService.findAllByLastNameContainingIgnoreCase(lastName)));
        }
        return "/SRM/teachers/teacherIndex";
    }

    //note that User username and account properties are only accessible via updateTeacher
    @TeacherRead
    @GetMapping("/{teacherId}")
    public ModelAndView getTeacherDetails(@PathVariable String teacherId) {
        ModelAndView mav = new ModelAndView("/SRM/teachers/teacherDetails");
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher not found");
            throw new NotFoundException("Teacher not found");
        }

        TeacherUser teacher = teacherUserService.findById(Long.valueOf(teacherId));
        Set<Subject> subjectsTaught = new HashSet<>(teacher.getSubjects());
        mav.addObject("subjectsTaught", subjectsTaught);
        mav.addObject("teacher", teacher);
        return mav;
    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewTeacher(Model model) {
        User user = User.builder().build();
        TeacherUser teacherUser = TeacherUser.builder().contactDetail(ContactDetail.builder().build()).build();
        user.setTeacherUser(teacherUser);
        teacherUser.setUsers(Set.of(user));
        model.addAttribute("teacher", teacherUser);
        model.addAttribute("user", user);
        return "/SRM/teachers/newTeacher";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewTeacher(@Valid @ModelAttribute("teacher") TeacherUser teacher, BindingResult teacherUserResults,
                                 @Valid @ModelAttribute("user") User user, BindingResult userResults, Model model) {
        if (teacherUserResults.hasErrors() || userResults.hasErrors()) {
            teacherUserResults.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            userResults.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            model.addAttribute("teacher", teacher);
            model.addAttribute("user", user);
            return "/SRM/teachers/newTeacher";
        }

        //check username
        if (userService.findByUsername(user.getUsername()) != null) {
            userResults.rejectValue("username", "duplicate", "Already in use");
            model.addAttribute("teacher", teacher);
            model.addAttribute("user", user);
            return "/SRM/teachers/newTeacher";
        }

        //no default value for contactDetails (save before saving teacherUser)
        if (teacher.getContactDetail() == null) {
            teacher.setContactDetail(ContactDetail.builder().build());
        }
        contactDetailService.save(teacher.getContactDetail());

        TeacherUser savedTeacherUser = teacherUserService.save(teacher);

        user.setRoles(Set.of(roleService.findByRoleName("TEACHER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setTeacherUser(savedTeacherUser);
        User savedUser = userService.save(user);

        log.debug("User with id: " + savedUser.getId() + " and teacher with id: " + savedTeacherUser.getId() + " saved");
        model.addAttribute("userFeedback", "Teacher details saved");
        model.addAttribute("teacher", savedTeacherUser);
        return "/SRM/teachers/teacherDetails";
    }

    @AdminUpdate
    @GetMapping("/{teacherId}/edit")
    public String getUpdateTeacher(@PathVariable String teacherId, Model model) {
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher not found");
            throw new NotFoundException("Teacher not found");
        }

        TeacherUser teacherFound = teacherUserService.findById(Long.valueOf(teacherId));
        model.addAttribute("teacher", teacherFound);
        model.addAttribute("user", teacherFound.getUsers().stream().findFirst().get());
        model.addAttribute("subjectsOnFile", auxiliaryController.sortSetBySubjectName(subjectService.findAll()));
        return "/SRM/teachers/updateTeacher";
    }

    @AdminUpdate
    @PostMapping("/{teacherId}/edit")
    public String postUpdateTeacher(@Valid @ModelAttribute("teacher") TeacherUser teacher, BindingResult teacherResult,
                                    @PathVariable String teacherId, Model model,
                                    @Valid @ModelAttribute("user") User user, BindingResult userResult) {
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher not found");
            throw new NotFoundException("Teacher not found");
        }
        TeacherUser teacherOnFile = teacherUserService.findById(Long.valueOf(teacherId));

        //check form data
        if (teacherResult.hasErrors() || userResult.hasErrors()) {
            teacherResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            userResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            teacher.setId(Long.valueOf(teacherId));     //ID lost here; prevent "teachers//edit" passed to the header
            model.addAttribute("teacher", teacher);
            model.addAttribute("user", user);
            model.addAttribute("subjectsOnFile", auxiliaryController.sortSetBySubjectName(subjectService.findAll()));
            return "/SRM/teachers/updateTeacher";
        }

        //recall all other variables and pass to the DB (Subject properties handled elsewhere)
        User userFound = teacherOnFile.getUsers().stream().findFirst().get();

        //process User username
        if (!user.getUsername().equals(userFound.getUsername())) {
            if (userService.findByUsername(user.getUsername()) != null) {
                log.debug("Username already exists");
                userResult.rejectValue("username", "duplicate", "Already in use");
                model.addAttribute("teacher", teacher);
                model.addAttribute("user", user);
                model.addAttribute("subjectsOnFile", auxiliaryController.sortSetBySubjectName(subjectService.findAll()));
                return "/SRM/teachers/updateTeacher";
            }
        }

        teacherOnFile.setFirstName(teacher.getFirstName());
        teacherOnFile.setLastName(teacher.getLastName());
        teacherOnFile.setDepartment(teacher.getDepartment());
        teacherOnFile.setSubjects(teacher.getSubjects());

        ContactDetail contactDetailFound;
        if (teacher.getContactDetail() != null) {
            contactDetailFound = teacher.getContactDetail();
        } else {
            contactDetailFound = ContactDetail.builder().build();
        }
        teacherOnFile.setContactDetail(contactDetailService.save(contactDetailFound));
        TeacherUser savedTeacher = teacherUserService.save(teacherOnFile);

        //update User
        userFound.setAccountNonLocked(user.isAccountNonLocked());
        userFound.setAccountNonExpired(user.isAccountNonExpired());
        userFound.setCredentialsNonExpired(user.isCredentialsNonExpired());
        userFound.setEnabled(user.isEnabled());
        User savedUser = userService.save(userFound);

        log.debug("User with id: " + savedUser.getId() + " and teacher with id: " + savedTeacher.getId() + " updated");
        model.addAttribute("userFeedback", "Teacher details updated");
        model.addAttribute("teacher", savedTeacher);
        return "/SRM/teachers/teacherDetails";
    }

    //note that User username and account properties are only accessible via updateTeacher
    @AdminUpdate
    @GetMapping("/{teacherId}/anon")
    public ModelAndView getAnonTeacher(@PathVariable String teacherId) {
        ModelAndView mav = new ModelAndView("/SRM/teachers/confirmAnon");
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher not found");
            throw new NotFoundException("Teacher not found");
        }

        TeacherUser teacher = teacherUserService.findById(Long.valueOf(teacherId));
        Set<Subject> subjectsTaught = new HashSet<>(teacher.getSubjects());
        mav.addObject("subjectsTaught", subjectsTaught);
        mav.addObject("teacher", teacher);
        return mav;
    }

    @AdminUpdate
    @PostMapping("/{teacherId}/anon")
    public String postAnonTeacher(@PathVariable String teacherId, Model model) {
        if (teacherUserService.findById(Long.valueOf(teacherId)) == null) {
            log.debug("Teacher not found");
            throw new NotFoundException("Teacher not found");
        }
        TeacherUser teacherOnFile = teacherUserService.findById(Long.valueOf(teacherId));
        String reply = "Teacher, " + teacherOnFile.getFirstName() + " " + teacherOnFile.getLastName() + ", anonymised.";

        anonymizeTeacher(teacherOnFile);

        model.addAttribute("reply", reply);
        return "/SRM/deleteConfirmed";
    }

    /**
     * Assigns the user name fields of a teacher with generic, non-identifying strings
     */
    @AdminUpdate
    public void anonymizeTeacher(TeacherUser teacher){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd-hhmm:ss");
        teacher.setFirstName("Anon_" + dateFormat.format(date));
        teacher.setLastName("Anon_" + dateFormat.format(date));

        // relinquish security credentials
        Set<User> userSet = teacher.getUsers();
        userSet.forEach(userService::delete);

        // update Contact Details
        ContactDetail teacherContacts = teacher.getContactDetail();
        teacher.setContactDetail(null);
        contactDetailService.delete(teacherContacts);

        teacherUserService.save(teacher);
    }
}
