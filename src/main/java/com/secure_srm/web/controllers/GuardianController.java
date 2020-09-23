package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.Address;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.services.peopleServices.AddressService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.web.permissionAnnot.AdminCreate;
import com.secure_srm.web.permissionAnnot.AdminUpdate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"", "/", "/index"})
    public String listGuardians(Model model, String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            model.addAttribute("guardians", guardianUserService.findAll());
        } else {
            model.addAttribute("guardians", guardianUserService.findAllByLastNameContainingIgnoreCase(lastName));
        }
        return "/SRM/guardians/guardianIndex";
    }

    @AdminCreate
    @GetMapping("/new")
    public String getNewGuardian(Model model) {
        model.addAttribute("guardian", GuardianUser.builder().build());
        return "/SRM/guardians/newGuardian";
    }

    @AdminCreate
    @PostMapping("/new")
    public String postNewGuardian(@Valid @ModelAttribute("guardian") GuardianUser guardian,
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> log.info(objectError.toString()));
            return "/SRM/guardians/newGuardian";
        }

        if (guardianUserService.findByFirstNameAndLastName(guardian.getFirstName(), guardian.getLastName()) == null) {
            addressService.save(guardian.getAddress());
            contactDetailService.save(guardian.getContactDetail());

            GuardianUser savedGuardian = guardianUserService.save(guardian);
            //head straight to the update page to edit other properties
            return "redirect:/guardians/" + savedGuardian.getId() + "/edit";
        } else {
            log.info("Current object already exists");
            GuardianUser found = guardianUserService.findByFirstNameAndLastName(guardian.getFirstName(), guardian.getLastName());
            model.addAttribute("guardian", found);
            model.addAttribute("students", found.getStudents());
            model.addAttribute("newGuardian", "Guardian already on file, record presented here");
            return "/SRM/guardians/guardianDetails";
        }
    }

    @TeacherRead
    @GetMapping("/{guardianId}")
    public ModelAndView getGuardianDetails(@PathVariable String guardianId) {
        ModelAndView mav = new ModelAndView("/SRM/guardians/guardianDetails");
        GuardianUser guardian = guardianUserService.findById(Long.valueOf(guardianId));
        Set<Student> studentSet = guardian.getStudents();
        mav.addObject("students", studentSet);
        mav.addObject("guardian", guardian);
        return mav;
    }

    @AdminUpdate
    @GetMapping("/{guardianId}/edit")
    public String getGuardianEdit(@PathVariable String guardianId, Model model) {
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian with ID: " + guardianId + " not found");
            throw new NotFoundException("Guardian not found");
        } else {
            GuardianUser guardianFound = guardianUserService.findById(Long.valueOf(guardianId));
            model.addAttribute("guardian", guardianFound);
            return "/SRM/guardians/updateGuardian";
        }
    }

    @AdminUpdate
    @PostMapping("/{guardianId}/edit")
    public String postGuardianUpdate(@Valid @ModelAttribute("guardian") GuardianUser guardian, BindingResult bindingResult,
                                     @PathVariable String guardianId, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            GuardianUser guardianOnFile = guardianUserService.findById(Long.valueOf(guardianId));
            guardian.setAddress(guardianOnFile.getAddress());
            guardian.setContactDetail(guardianOnFile.getContactDetail());
            guardian.setStudents(guardianOnFile.getStudents());
            model.addAttribute("guardian", guardian);
            return "/SRM/guardians/updateGuardian";
        }

        GuardianUser guardianOnFile = guardianUserService.findById(Long.valueOf(guardianId));
        guardianOnFile.setFirstName(guardian.getFirstName());
        guardianOnFile.setLastName(guardian.getLastName());

        guardianOnFile.setContactDetail(contactDetailService.save(guardian.getContactDetail()));

        GuardianUser savedGuardian = guardianUserService.save(guardianOnFile);
        model.addAttribute("guardian", savedGuardian);
        model.addAttribute("students", savedGuardian.getStudents());
        model.addAttribute("newGuardian", "Changes saved to the database");
        return "/SRM/guardians/guardianDetails";
    }

    @AdminUpdate
    @GetMapping("/{guardianId}/addRemoveStudents")
    public String getGuardian_studentSet(Model model, @PathVariable String guardianId){
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian with ID: " + guardianId + " not found");
            throw new NotFoundException("Guardian not found");
        } else {
            model.addAttribute("studentSet", studentService.findAll());
            model.addAttribute("guardian", guardianUserService.findById(Long.valueOf(guardianId)));
            return "/SRM/guardians/studentSet";
        }
    }

    //search function to refine list of Students registered to Guardian
    @AdminUpdate
    @GetMapping("/{guardianId}/addRemoveStudents/search")
    public String getRefineStudentList(Model model, @PathVariable String guardianId, String StudentLastName){
        if (guardianUserService.findById(Long.valueOf(guardianId)) == null) {
            log.debug("Guardian with ID: " + guardianId + " not found");
            throw new NotFoundException("Guardian not found");
        } else {
            if (StudentLastName == null || StudentLastName.isEmpty()) {
                model.addAttribute("studentSet", studentService.findAll());
            } else {
                model.addAttribute("studentSet", studentService.findAllByLastNameContainingIgnoreCase(StudentLastName));
            }
            model.addAttribute("guardian", guardianUserService.findById(Long.valueOf(guardianId)));
            return "/SRM/guardians/studentSet";
        }
    }

    @AdminUpdate
    @PostMapping("/{guardianId}/addRemoveStudents")
    public String postGuardian_studentSet(@ModelAttribute("guardian") GuardianUser formGuardian, @PathVariable String guardianId,
                                          Model model) {

        log.debug("Current students registered with guardian passed:" + formGuardian.getStudents().size());

        GuardianUser guardianUserOnFile = guardianUserService.findById(Long.valueOf(guardianId));
        log.debug("Current students registered with guardian on file:"
                + guardianUserOnFile.getStudents().size());

        //removed is the guardianUserOnFile - guardian passed
        Set<Student> removed = new HashSet<>(guardianUserOnFile.getStudents());
        removed.removeIf(formGuardian.getStudents()::contains);

        //update students' (removed) Guardians Set
        removed.forEach(student -> {
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
        model.addAttribute("students", saved.getStudents());
        model.addAttribute("newGuardian", "Registered students updated");
        return "/SRM/guardians/guardianDetails";
    }
}
