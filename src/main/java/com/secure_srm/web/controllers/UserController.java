package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.ForbiddenException;
import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.security.*;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.securityServices.*;
import com.secure_srm.web.permissionAnnot.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final TeacherUserService teacherUserService;
    private final RoleService roleService;
    private final GuardianUserService guardianUserService;
    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;
    private final ContactDetailService contactDetailService;
    private final AuxiliaryController auxiliaryController;

    private final String INVALID_ADMIN_NAME = "AdminUser's name length must be >= 8 characters";

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

    @GetMapping({"/", "/welcome"})
    public String welcomePage(Model model) {
        return "/SRM/SRM_home";
    }

    //this overrides the default Spring Security login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    //this overrides the default GET logout page
    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/welcome";
    }

    //sets login-error to true and triggers login.html to display 'wrong user or password'
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @GuardianRead
    @GetMapping("/authenticated")
    public String userLogin(Model model) {
        User user = auxiliaryController.getCurrentUser();
        model.addAttribute("userID", user.getId());
        model.addAttribute("user", auxiliaryController.getUsername());
        if (user.getAdminUser() != null) {
            model.addAttribute("contactDetails", user.getAdminUser().getContactDetail());
        } else if (user.getTeacherUser() != null) {
            model.addAttribute("contactDetails", user.getTeacherUser().getContactDetail());
        } else if (user.getGuardianUser() != null) {
            model.addAttribute("contactDetails", user.getGuardianUser().getContactDetail());
        }
        return "authenticated";
    }

    /**
     * Processes contact detail changes via the 'Account Settings' dropdown
     */
    @GuardianUpdate
    @PostMapping("/editContactDetails/{userID}")
    public String updateContactDetails(@PathVariable("userID") String userID, Model model,
                                       @ModelAttribute("contactDetails") ContactDetail contactDetail) {
        if (userService.findById(Long.valueOf(userID)) == null) {
            log.debug("Cannot update contact details with given ID");
            throw new NotFoundException("User with given ID not found");
        } else {
            User userOnFile = userService.findById(Long.valueOf(userID));
            ContactDetail savedContacts;
            if (userOnFile.getAdminUser() != null) {
                ContactDetail contactsOnFile = userOnFile.getAdminUser().getContactDetail();
                savedContacts = syncAndSaveContacts(contactDetail, contactsOnFile);
                userOnFile.getAdminUser().setContactDetail(savedContacts);
            } else if (userOnFile.getTeacherUser() != null) {
                ContactDetail contactsOnFile = userOnFile.getTeacherUser().getContactDetail();
                savedContacts = syncAndSaveContacts(contactDetail, contactsOnFile);
                userOnFile.getTeacherUser().setContactDetail(savedContacts);
            } else if (userOnFile.getGuardianUser() != null) {
                ContactDetail contactsOnFile = userOnFile.getGuardianUser().getContactDetail();
                savedContacts = syncAndSaveContacts(contactDetail, contactsOnFile);
                userOnFile.getGuardianUser().setContactDetail(savedContacts);
            } else {
                model.addAttribute("contactDetailFeedback", "Contact details not updated");
                model.addAttribute("contactDetails", ContactDetail.builder()
                        .email("(not saved)").phoneNumber("(not saved)").build());
                return "/authenticated";
            }
            userService.save(userOnFile);
            model.addAttribute("contactDetailFeedback", "Contact details updated");
            model.addAttribute("contactDetails", savedContacts);
            return "/authenticated";
        }
    }

    // AdminPages (perform CRUD ops on AdminUsers) =================================================================

    @AdminRead
    @GetMapping("/adminPage")
    public String getAdminPage(Model model) {
        Set<User> AdminUsers = userService.findAll().stream().filter(
                user -> user.getAdminUser() != null
        ).collect(Collectors.toSet());
        model.addAttribute("AdminUsersFound", AdminUsers);

        //current authenticated user details
        model.addAttribute("userID", auxiliaryController.getCurrentUser().getId());
        model.addAttribute("user", auxiliaryController.getUsername());
        return "adminPage";
    }

    // Global password methods and routes =========================================================================

    @AdminUpdate
    @PostMapping("/resetPassword/{userID}")
    public String postResetPassword(@PathVariable String userID, Model model) {
        if (userService.findById(Long.valueOf(userID)) != null) {
            User currentUser = userService.findById(Long.valueOf(userID));
            currentUser.setPassword(passwordEncoder.encode(currentUser.getUsername() + "123"));
            userService.save(currentUser);
            log.debug("Password was reset");
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("confirmReset", "Password has been reset and changes have been saved.");
            if (currentUser.getAdminUser() != null) {
                model.addAttribute("currentAdminUser", currentUser.getAdminUser());
                return "adminUpdate";
            } else if (currentUser.getTeacherUser() != null) {
                model.addAttribute("teacher", currentUser.getTeacherUser());
                model.addAttribute("user", currentUser);
                return "/SRM/teachers/updateTeacher";
            } else {
                model.addAttribute("guardian", currentUser.getGuardianUser());
                model.addAttribute("user", currentUser);
                return "/SRM/guardians/updateGuardian";
            }
        }
        log.debug("User not found");
        throw new NotFoundException("User not found");
    }

    @AdminUpdate
    @PostMapping("/changePassword/{userID}")
    public String postChangeAdminUserPassword(@PathVariable String userID,
                                              @Valid @ModelAttribute("currentUser") User passwordChangeUser,
                                              BindingResult bindingResult, Model model) {
        if (userService.findById(Long.valueOf(userID)) == null) {
            log.debug("User not found");
            throw new NotFoundException("User not found");
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", passwordChangeUser);
            model.addAttribute("currentAdminUser", passwordChangeUser.getAdminUser());
            return "adminUpdate";
        }

        User found = userService.findById(Long.valueOf(userID));
        found.setPassword(passwordEncoder.encode(passwordChangeUser.getPassword()));
        User saved = userService.save(found);
        log.debug("Password change for " + saved.getUsername() + " has been saved");
        return "adminPage";
    }

    @GuardianUpdate
    @GetMapping("/changePassword")
    public String getChangePassword(Model model) {
        if (userService.findByUsername(auxiliaryController.getUsername()) == null) {
            log.debug("Attempt to change password not recognised");
            throw new ForbiddenException("Attempt to change password not recognised");
        }

        model.addAttribute("userID", auxiliaryController.getUserId());
        return "changePassword";
    }

    @GuardianUpdate
    @PostMapping("/changePassword")
    public String getChangePassword(Model model, String newPassword) {
        if (userService.findByUsername(auxiliaryController.getUsername()) == null) {
            log.debug("Attempt to change password not recognised");
            throw new ForbiddenException("Attempt to change password not recognised");
        }

        if (newPassword == null || newPassword.length() < 2 || newPassword.length() > 255) {
            model.addAttribute("pwdFeedback", "Passwords must be between 2 and 255 characters long");
        } else {
            User currentUser = userService.findByUsername(auxiliaryController.getUsername());
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userService.save(currentUser);
            log.debug("New password saved");
            model.addAttribute("pwdFeedback", "Password changed and saved");
        }

        model.addAttribute("userID", auxiliaryController.getUserId());
        return "changePassword";
    }

    // Admin CRUD ops =======================================================================================

    @AdminRead
    @GetMapping("/createAdmin")
    public String getNewAdmin(Model model) {
        User user = User.builder().build();
        model.addAttribute("newUser", user);
        model.addAttribute("user", auxiliaryController.getUsername());
        ContactDetail blankContact = ContactDetail.builder().email("").phoneNumber("").build();
        AdminUser adminUser = AdminUser.builder().contactDetail(blankContact).build();
        model.addAttribute("newAdmin", adminUser);
        return "adminCreate";
    }

    @AdminCreate
    @PostMapping("/createAdmin")
    public String postNewAdmin(@Valid @ModelAttribute("newAdmin") AdminUser newAdminUser,
                               BindingResult adminBindingResult, @Valid @ModelAttribute("newUser") User newUser,
                               BindingResult userBindingResult) {
        if (adminBindingResult.hasErrors() || userBindingResult.hasErrors()) {
            adminBindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            userBindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return "adminCreate";
        }

        // At present, Weblogin saves new AdminUser and User concurrently (treated as one entity) since we require a
        // User password.
        // Different AdminUsers associated with the same User would require more functionality not offered here.
        // We proceed here assuming that different AdminUsers can be associated with the same User.

        // New Users, with given Roles, are instantiated before AdminUsers. One AdminUser is associated with many Users.
        // All User usernames and hence Users are unique. Check that the new AdminUser is not registering with a User
        // it is already associated with
        User userFound = userAlreadyExists(newUser.getUsername());
        AdminUser adminUserFound = adminUserAlreadyExists(newAdminUser.getFirstName(), newAdminUser.getLastName());
        if (userFound != null) {
            //User is already on file
            if (adminUserFound != null) {
                //AdminUser is also on file
                if (adminUserFound.getUsers().stream().anyMatch(user ->
                        user.getUsername().equals(userFound.getUsername()))) {
                    //already registered/associated (only option is to change the AdminUserName
                    log.debug("AdminUser is already registered with the given User");
                    adminBindingResult.rejectValue("adminUserName", "exists",
                            "AdminUser provided is already registered with given User. Please change the AdminUser name.");
                    return "adminCreate";
                }
                //not currently registered with given User (can save current form data)
            }
            //AdminUser not found (can save current form data)
        }
        //User not found (can save current form data)

        //all checks complete
        newAdminUser(newAdminUser, newUser);
        return "redirect:/adminPage";
    }

    @AdminUpdate
    @GetMapping("/updateAdmin/{adminUserID}")
    public String getUpdateAdmin(Model model, @PathVariable String adminUserID) {
        if (userService.findById(Long.valueOf(adminUserID)) == null) {
            log.debug("User not found");
            throw new NotFoundException("User with given ID not found");
        }

        User user = userService.findById(Long.valueOf(adminUserID));
        //guard against wrong adminUser by user ID
        if (user.getAdminUser() == null) {
            log.debug("No adminUser associated with given user");
            return "redirect:/adminPage";
        } else {
            AdminUser adminUser = user.getAdminUser();
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", user);
            model.addAttribute("currentAdminUser", adminUser);
            return "adminUpdate";
        }
    }

    @AdminUpdate
    @PostMapping("/updateAdmin/{adminUserID}")
    public String postUpdateAdminWithID(@PathVariable String adminUserID,
                                        @Valid @ModelAttribute("currentUser") User currentUser,
                                        BindingResult userBindingResult,
                                        @Valid @ModelAttribute("currentAdminUser") AdminUser currentAdminUser,
                                        BindingResult adminBindingResult, Model model) {
        if (userService.findById(Long.valueOf(adminUserID)) == null) {
            log.debug("User not found");
            throw new NotFoundException("User with given ID not found");
        }

        User onFile = userService.findById(Long.valueOf(adminUserID));

        if (userBindingResult.hasErrors() || adminBindingResult.hasErrors()) {
            adminBindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            userBindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentAdminUser", currentAdminUser);
            return "adminUpdate";
        }

        //check for the same username
        if (userService.findByUsername(currentUser.getUsername()) != null) {
            log.debug("Username already in use");
            model.addAttribute("usernameExists", "Username already in use");
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentAdminUser", currentAdminUser);
            return "adminUpdate";
        }

        //sync. account related settings (lockout, enabled, etc.)
        onFile.setAccountNonLocked(currentUser.isAccountNonLocked());
        onFile.setAccountNonExpired(currentUser.isAccountNonExpired());
        onFile.setCredentialsNonExpired(currentUser.isCredentialsNonExpired());
        onFile.setEnabled(currentUser.isEnabled());


        //save changes
        User saved = userService.save(onFile);
        log.debug("Username: " + saved.getUsername() + ", adminUser name: " + saved.getAdminUser().getFirstName() +
                " " + saved.getAdminUser().getLastName() + " saved");
        model.addAttribute("AdminUserSaved", "Updates applied successfully");
        model.addAttribute("currentUser", saved);
        model.addAttribute("currentAdminUser", saved.getAdminUser());
        return "adminUpdate";
    }

    //this was previously generalised for all user types but at present only works on AdminUsers
    @AdminDelete
    @PostMapping("/deleteUser/{userID}")
    public String postDeleteUser(@PathVariable String userID, Model model) {
        if (userService.findById(Long.valueOf(userID)) != null) {
            User currentUser = userService.findById(Long.valueOf(userID));
            if (Long.valueOf(userID).equals(auxiliaryController.getCurrentUser().getId())) {
                log.debug("Cannot delete yourself");
                model.addAttribute("deniedDelete", "You are not permitted to delete your own account");
                model.addAttribute("returnURL", userTypeUpdatePage(currentUser) + "/" + userID);
                model.addAttribute("pageTitle", "previous page");
            } else {
                if (userTypeDeleteSuccess(currentUser, userID)) {
                    model.addAttribute("confirmDelete", "User with username \"" + currentUser.getUsername()
                            + "\" successfully deleted");
                    model.addAttribute("returnURL", "adminPage");
                    model.addAttribute("pageTitle", "Admin page");
                } else {
                    model.addAttribute("deniedDelete", "User with username \"" + currentUser.getUsername()
                            + "\" was not deleted");
                    model.addAttribute("returnURL", "updateAdmin/" + currentUser.getId());
                    model.addAttribute("pageTitle", "previous page");
                }
            }
            return "confirmDelete";
        }
        log.debug("User with ID: " + userID + " not found");
        return "redirect:/adminPage";
    }

    // end of CRUD ops ==========================================================================================
    //the following methods are called by the above controller methods only if the required parameters are verified

    /**
     * Updates contact details
     */
    @GuardianUpdate
    private ContactDetail syncAndSaveContacts(ContactDetail newDetails, ContactDetail contactsOnFile) {
        contactsOnFile.setEmail(newDetails.getEmail());
        contactsOnFile.setPhoneNumber(newDetails.getPhoneNumber());
        return contactDetailService.save(contactsOnFile);
    }

    /**
     * inserts URL path related Strings dependent on the Usertype (AdminUser, TeacherUser or GuardianUser)
     */
    @AdminRead
    private String userTypeUpdatePage(User user) {
        if (user.getAdminUser() != null) {
            return "updateAdmin";
        } else if (user.getTeacherUser() != null) {
            return "updateTeacher";
        } else
            return "updateGuardian";
    }

    /**
     * executes JPA User delete() dependent on the Usertype (AdminUser, TeacherUser or GuardianUser)
     */
    @AdminDelete
    private boolean userTypeDeleteSuccess(User user, String userID) {
        if (user.getAdminUser() != null) {
            deleteAdminUser(user.getId());
        } else if (user.getTeacherUser() != null) {
            deleteTeacherUser(user.getId());
        } else
            deleteGuardianUser(user.getId());
        if (userService.findById(Long.valueOf(userID)) != null) {
            log.debug("User with ID: " + userID + " was not deleted");
            return false;
        } else
            return true;
    }

    @AdminUpdate
    private void newAdminUser(AdminUser newAdminUser, User newUser) {
        Role adminRole = roleService.findByRoleName("ADMIN");
        AdminUser savedAdminUser = adminUserService.save(
                AdminUser.builder().firstName(newAdminUser.getFirstName()).lastName(newAdminUser.getLastName()).build());
        User savedUser = userService.save(User.builder().adminUser(savedAdminUser)
                .username(newUser.getUsername()).password(passwordEncoder.encode(newUser.getPassword()))
                .role(adminRole).build());
        log.debug("New Admin name: " + savedUser.getAdminUser().getFirstName() + " " + savedUser.getAdminUser().getLastName()
                + ", with username " + savedUser.getUsername() + " and ID: " + savedUser.getId() + " added");
    }

    @AdminUpdate
    private User userAlreadyExists(String username) {
        if (username == null || username.isEmpty()) {
            log.debug("Null or empty username passed");
            return null;
        }
        if (userService.findAllByUsername(username) == null) {
            log.debug("User, " + username + " not found");
            return null;
        } else {
            log.debug("User, " + username + " found");
            return userService.findByUsername(username);
        }
    }

    @AdminUpdate
    private AdminUser adminUserAlreadyExists(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            log.debug("AdminUser null firstName and/or lastName passed");
            return null;
        }
        if (adminUserService.findByFirstNameAndLastName(firstName, lastName) == null) {
            log.debug("AdminUser not found");
            return null;
        } else
            return adminUserService.findByFirstNameAndLastName(firstName, lastName);
    }

    @AdminDelete
    private void deleteAdminUser(Long userID) {
        //AdminUser to User is ManyToOne; do not delete AdminUser unless size == 0
        User toBeDeleted = userService.findById(userID);
        AdminUser adminUser = toBeDeleted.getAdminUser();

        //settle the mappings between User and AdminUser
        toBeDeleted.setAdminUser(null);
        adminUser.getUsers().removeIf(user -> user.getUsername().equals(toBeDeleted.getUsername()));
        adminUserService.save(adminUser);

        String adminUserName = adminUser.getFirstName() + " " + adminUser.getLastName();
        Long adminUserId = adminUser.getId();
        if (adminUser.getUsers().isEmpty()) {
            adminUserService.deleteById(adminUserId);
            log.debug("AdminUser, " + adminUserName + ", User set is now empty and has been deleted");
        } else {
            log.debug("AdminUser, " + adminUserName + ", has " + adminUser.getUsers().size() + " remaining Users associated");
        }

        userService.deleteById(userID);
    }

    @AdminDelete
    private void deleteTeacherUser(Long userID) {
        //TeacherUser to User is ManyToOne; do not delete TeacherUser unless size == 0
        User toBeDeleted = userService.findById(userID);
        TeacherUser teacherUser = toBeDeleted.getTeacherUser();

        //settle the mappings between User and TeacherUser
        toBeDeleted.setTeacherUser(null);
        teacherUser.getUsers().removeIf(user -> user.getUsername().equals(toBeDeleted.getUsername()));
        teacherUserService.save(teacherUser);

        String teacherUserName = teacherUser.getFirstName() + " " + teacherUser.getLastName();
        Long teacherUserId = teacherUser.getId();
        if (teacherUser.getUsers().isEmpty()) {
            teacherUserService.deleteById(teacherUserId);
            log.debug("TeacherUser, " + teacherUserName + ", User set is now empty and has been deleted");
        } else {
            log.debug("TeacherUser, " + teacherUserName + ", has " + teacherUser.getUsers().size()
                    + " remaining Users associated");
        }

        userService.deleteById(userID);
    }

    @AdminDelete
    private void deleteGuardianUser(Long userID) {
        //GuardianUser to User is ManyToOne; do not delete AdminUser unless size == 0
        User toBeDeleted = userService.findById(userID);
        GuardianUser guardianUser = toBeDeleted.getGuardianUser();

        //settle the mappings between User and AdminUser
        toBeDeleted.setGuardianUser(null);
        guardianUser.getUsers().removeIf(user -> user.getUsername().equals(toBeDeleted.getUsername()));
        guardianUserService.save(guardianUser);

        String guardianUserName = guardianUser.getFirstName() + " " + guardianUser.getLastName();
        Long guardianUserId = guardianUser.getId();
        if (guardianUser.getUsers().isEmpty()) {
            guardianUserService.deleteById(guardianUserId);
            log.debug("GuardianUser, " + guardianUserName + ", User set is now empty and has been deleted");
        } else {
            log.debug("GuardianUser, " + guardianUserName + ", has " + guardianUser.getUsers().size()
                    + " remaining Users associated");
        }

        userService.deleteById(userID);
    }
}
