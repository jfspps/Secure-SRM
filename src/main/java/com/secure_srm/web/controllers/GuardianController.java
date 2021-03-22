package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.Address;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.model.security.User;
import com.secure_srm.services.peopleServices.AddressService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.services.securityServices.RoleService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.AdminCreate;
import com.secure_srm.web.permissionAnnot.AdminDelete;
import com.secure_srm.web.permissionAnnot.AdminUpdate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/guardians"})
public class GuardianController {

    private final GuardianUserService guardianUserService;
    private final StudentService studentService;
    private final ContactDetailService contactDetailService;
    private final AddressService addressService;
    private final UserService userService;
    private final AuxiliaryController auxiliaryController;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

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
    public String listGuardians(Model model, String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            model.addAttribute("guardians", auxiliaryController.sortGuardianSetByLastName(guardianUserService.findAll()));
        } else {
            model.addAttribute("guardians",
                    auxiliaryController.sortGuardianSetByLastName(guardianUserService.findAllByLastNameContainingIgnoreCase(lastName)));
        }
        return "/SRM/guardians/guardianIndex";
    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewGuardian(Model model) {
        User user = User.builder().build();
        GuardianUser guardianUser = GuardianUser.builder().contactDetail(ContactDetail.builder().build()).build();
        user.setGuardianUser(guardianUser);
        guardianUser.setUsers(Set.of(user));
        model.addAttribute("guardian", guardianUser);
        model.addAttribute("user", user);
        return "/SRM/guardians/newGuardian";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewGuardian(@Valid @ModelAttribute("guardian") GuardianUser guardian, BindingResult guardianUserResults,
                                  @Valid @ModelAttribute("user") User user, BindingResult userResults, Model model) {
        if (guardianUserResults.hasErrors() || userResults.hasErrors()) {
            guardianUserResults.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            userResults.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            model.addAttribute("guardian", guardian);
            model.addAttribute("user", user);
            return "/SRM/guardians/newGuardian";
        }

        //check username
        if (userService.findByUsername(user.getUsername()) != null) {
            userResults.rejectValue("username", "duplicate", "Already in use");
            model.addAttribute("guardian", guardian);
            model.addAttribute("user", user);
            return "/SRM/guardians/newGuardian";
        }

        //no default value for contactDetails (save before saving GuardianUser)
        if (guardian.getContactDetail() == null) {
            guardian.setContactDetail(ContactDetail.builder().build());
        }
        contactDetailService.save(guardian.getContactDetail());

        //no default value for address (save before saving GuardianUser)
        if (guardian.getAddress() == null) {
            guardian.setAddress(Address.builder().build());
        }
        addressService.save(guardian.getAddress());

        GuardianUser savedGuardianUser = guardianUserService.save(guardian);

        user.setRoles(Set.of(roleService.findByRoleName("GUARDIAN")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setGuardianUser(savedGuardianUser);
        User savedUser = userService.save(user);

        log.debug("User with id: " + savedUser.getId() + " and guardian with id: " + savedGuardianUser.getId() + " saved");
        model.addAttribute("userFeedback", "Guardian details saved");
        model.addAttribute("guardian", savedGuardianUser);
        return "/SRM/guardians/guardianDetails";
    }

    @TeacherRead
    @GetMapping("/{guardianId}")
    public ModelAndView getGuardianDetails(@PathVariable String guardianId) {
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian not found");
            throw new NotFoundException("Guardian not found");
        }

        ModelAndView mav = new ModelAndView("/SRM/guardians/guardianDetails");
        GuardianUser guardian = guardianUserService.findById(Long.valueOf(guardianId));
        Set<Student> studentSet = guardian.getStudents();
        mav.addObject("students", auxiliaryController.sortStudentSetByLastName(studentSet));
        mav.addObject("guardian", guardian);
        return mav;
    }

    @AdminUpdate
    @GetMapping("/{guardianId}/edit")
    public String getGuardianEdit(@PathVariable String guardianId, Model model) {
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian not found");
            throw new NotFoundException("Guardian not found");
        }

        GuardianUser guardianFound = guardianUserService.findById(Long.valueOf(guardianId));
        model.addAttribute("guardian", guardianFound);
        model.addAttribute("user", guardianFound.getUsers().stream().findFirst().get());
        return "/SRM/guardians/updateGuardian";
    }

    @AdminUpdate
    @PostMapping("/{guardianId}/edit")
    public String postGuardianUpdate(@Valid @ModelAttribute("guardian") GuardianUser guardian, BindingResult guardianResult,
                                     @PathVariable String guardianId, Model model,
                                     @Valid @ModelAttribute("user") User user, BindingResult userResult) {
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian not found");
            throw new NotFoundException("Guardian not found");
        }
        GuardianUser guardianOnFile = guardianUserService.findById(Long.valueOf(guardianId));

        //check form data
        if (guardianResult.hasErrors() || userResult.hasErrors()) {
            guardianResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            userResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            guardian.setId(Long.valueOf(guardianId));     //ID lost here; prevent "guardians//edit" passed to the header
            model.addAttribute("guardian", guardian);
            model.addAttribute("user", user);
            return "/SRM/guardians/updateGuardian";
        }

        //recall all other variables and pass to the DB (Subject properties handled elsewhere)
        User userFound = guardianOnFile.getUsers().stream().findFirst().get();

        //process User username
        if (!user.getUsername().equals(userFound.getUsername())) {
            if (userService.findByUsername(user.getUsername()) != null) {
                log.debug("Username already exists");
                userResult.rejectValue("username", "duplicate", "Already in use");
                model.addAttribute("guardian", guardian);
                model.addAttribute("user", user);
                return "/SRM/guardians/updateGuardian";
            }
        }

        guardianOnFile.setFirstName(guardian.getFirstName());
        guardianOnFile.setLastName(guardian.getLastName());

        ContactDetail contactDetailFound;
        if (guardian.getContactDetail() != null) {
            contactDetailFound = guardian.getContactDetail();
        } else {
            contactDetailFound = ContactDetail.builder().build();
        }
        guardianOnFile.setContactDetail(contactDetailService.save(contactDetailFound));

        GuardianUser savedGuardian = guardianUserService.save(guardianOnFile);

        //update User
        userFound.setAccountNonLocked(user.isAccountNonLocked());
        userFound.setAccountNonExpired(user.isAccountNonExpired());
        userFound.setCredentialsNonExpired(user.isCredentialsNonExpired());
        userFound.setEnabled(user.isEnabled());
        User savedUser = userService.save(userFound);

        log.debug("User with id: " + savedUser.getId() + " and guardian with id: " + savedGuardian.getId() + " updated");
        model.addAttribute("userFeedback", "Guardian details updated");
        model.addAttribute("guardian", savedGuardian);
        return "/SRM/guardians/guardianDetails";
    }

    @AdminUpdate
    @GetMapping("/{guardianId}/addRemoveStudents")
    public String getGuardian_studentSet(Model model, @PathVariable String guardianId) {
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian not found");
            throw new NotFoundException("Guardian not found");
        }

        model.addAttribute("studentSet", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));
        model.addAttribute("guardian", guardianUserService.findById(Long.valueOf(guardianId)));
        return "/SRM/guardians/studentSet";
    }

    //search function to refine list of Students registered to Guardian
    @AdminUpdate
    @GetMapping("/{guardianId}/addRemoveStudents/search")
    public String getRefineStudentList(Model model, @PathVariable String guardianId, String StudentLastName) {
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian not found");
            throw new NotFoundException("Guardian not found");
        }
        GuardianUser onFile = guardianUserService.findById(Long.valueOf(guardianId));

        if (StudentLastName == null || StudentLastName.isEmpty()) {
            model.addAttribute("studentSet", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));
        } else {
            Set<Student> studentsSet = new HashSet<>(studentService.findAllByLastNameContainingIgnoreCase(StudentLastName));
            studentsSet.addAll(onFile.getStudents());
            model.addAttribute("studentSet",
                    auxiliaryController.sortStudentSetByLastName(studentsSet));
        }
        model.addAttribute("guardian", onFile);
        return "/SRM/guardians/studentSet";
    }

    @AdminUpdate
    @PostMapping("/{guardianId}/addRemoveStudents")
    public String postGuardian_studentSet(@ModelAttribute("guardian") GuardianUser formGuardian, @PathVariable String guardianId,
                                          Model model) {

        GuardianUser guardianUserOnFile = guardianUserService.findById(Long.valueOf(guardianId));

        //build set of removed students
        Set<Student> removedStudents = new HashSet<>(guardianUserOnFile.getStudents());
        removedStudents.removeIf(formGuardian.getStudents()::contains);

        //update removed students' GuardianSet property and guardianUserOnFile StudentSet
        removedStudents.forEach(student -> {
            student.getGuardians().remove(guardianUserOnFile);
            guardianUserOnFile.getStudents().remove(student);
            studentService.save(student);
        });

        //assign added students to Guardian and vice versa
        formGuardian.getStudents().stream().forEach(student -> {
            student.getGuardians().add(guardianUserOnFile);
            formGuardian.getStudents().add(student);
            studentService.save(student);
        });

        guardianUserOnFile.setStudents(formGuardian.getStudents());
        GuardianUser saved = guardianUserService.save(guardianUserOnFile);

        model.addAttribute("guardian", saved);
        model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(saved.getStudents()));
        model.addAttribute("userFeedback", "Registered students updated");
        return "/SRM/guardians/guardianDetails";
    }

    @AdminDelete
    @GetMapping("/{guardianID}/delete")
    public String getDeleteGuardian(Model model, @PathVariable Long guardianID) {

        // get personal details
        GuardianUser found = guardianUserService.findById(guardianID);

        model.addAttribute("guardian", found);
        return "/SRM/guardians/confirmDelete";
    }

    @AdminDelete
    @PostMapping("/{guardianID}/delete")
    public String postDeleteGuardian(@ModelAttribute("guardian") GuardianUser guardianUser, Model model, @PathVariable Long guardianID) {

        // get personal details
        GuardianUser found = guardianUserService.findById(guardianID);

        model.addAttribute("reply", "Guardian, " + found.getFirstName() + " " + found.getLastName() + " removed.");
        return "/SRM/deleteConfirmed";
    }
}
