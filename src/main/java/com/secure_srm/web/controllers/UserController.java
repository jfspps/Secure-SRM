package com.secure_srm.web.controllers;

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
import java.util.HashSet;
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

    private final String INVALID_USERNAME = "User's username length must be >= 8 characters";
    private final String INVALID_ADMIN_NAME = "AdminUser's name length must be >= 8 characters";
    private final String INVALID_TEACHER_NAME = "TeacherUser's name length must be >= 8 characters";
    private final String INVALID_GUARDIAN_NAME = "GuardianUser's name length must be >= 8 characters";

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("hasSubject")
    public Boolean teachesSubjects(){
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
        if (user.getAdminUser() != null){
            model.addAttribute("contactDetails", user.getAdminUser().getContactDetail());
        } else if (user.getTeacherUser() != null){
            model.addAttribute("contactDetails", user.getTeacherUser().getContactDetail());
        } else if (user.getGuardianUser() != null){
            model.addAttribute("contactDetails", user.getGuardianUser().getContactDetail());
        }
        return "authenticated";
    }

    /**
     * Processes contact detail changes via the 'Account Settings' dropdown
     * */
    @GuardianUpdate
    @PostMapping("/editContactDetails/{userID}")
    public String updateContactDetails(@PathVariable("userID") String userID, Model model,
                                       @ModelAttribute("contactDetails") ContactDetail contactDetail){
        if (userService.findById(Long.valueOf(userID)) == null){
            log.debug("Cannot update contact details with given ID");
            throw new NotFoundException("User with given ID not found");
        } else {
            User userOnFile = userService.findById(Long.valueOf(userID));
            ContactDetail savedContacts;
            if (userOnFile.getAdminUser() != null){
                ContactDetail contactsOnFile = userOnFile.getAdminUser().getContactDetail();
                savedContacts = syncAndSaveContacts(contactDetail, contactsOnFile);
                userOnFile.getAdminUser().setContactDetail(savedContacts);
            } else if (userOnFile.getTeacherUser() != null){
                ContactDetail contactsOnFile = userOnFile.getTeacherUser().getContactDetail();
                savedContacts = syncAndSaveContacts(contactDetail, contactsOnFile);
                userOnFile.getTeacherUser().setContactDetail(savedContacts);
            } else if (userOnFile.getGuardianUser() != null){
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

    @GuardianRead
    @GetMapping("/userPage")
    public String userPage(Model model) {
        model.addAttribute("userID", auxiliaryController.getCurrentUser().getId());
        model.addAttribute("user", auxiliaryController.getUsername());
        return "userPage";
    }

    @AdminRead
    @GetMapping("/adminPage")
    public String adminPage(Model model) {
        Set<User> AdminUsers = userService.findAll().stream().filter(
                user -> user.getAdminUser() != null
        ).collect(Collectors.toSet());
        model.addAttribute("AdminUsersFound", AdminUsers);

        Set<User> TeacherUsers = userService.findAll().stream().filter(
                user -> user.getTeacherUser() != null
        ).collect(Collectors.toSet());
        model.addAttribute("TeacherUsersFound", TeacherUsers);

        Set<User> GuardianUsers = userService.findAll().stream().filter(
                user -> user.getGuardianUser() != null
        ).collect(Collectors.toSet());
        model.addAttribute("GuardianUsersFound", GuardianUsers);

        //current authenticated user details
        model.addAttribute("userID", auxiliaryController.getCurrentUser().getId());
        model.addAttribute("user", auxiliaryController.getUsername());
        return "adminPage";
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

    //lists all users on userPage
    @TeacherRead
    @GetMapping("/listUsers")
    public String listUsers(Model model) {
        //userSet is never null if user has one of the above roles
        Set<User> userSet = new HashSet<>(userService.findAll());
        model.addAttribute("usersFound", userSet);
        model.addAttribute("userID", auxiliaryController.getCurrentUser().getId());
        return "userPage";
    }

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
            model.addAttribute("confirmReset", "Password has been reset");
            if (currentUser.getAdminUser() != null){
                model.addAttribute("currentAdminUser", currentUser.getAdminUser());
                return "adminUpdate";
            } else if (currentUser.getTeacherUser() != null){
                model.addAttribute("currentTeacherUser", currentUser.getTeacherUser());
                return "teacherUpdate";
            } else {
                model.addAttribute("currentGuardianUser", currentUser.getGuardianUser());
                return "guardianUpdate";
            }
        }
        log.debug("User with ID: " + userID + " not found");
        return "redirect:/adminPage";
    }

    @AdminUpdate
    @PostMapping("/changePassword/{userID}")
    public String postChangePassword(@PathVariable String userID,
                                     @Valid @ModelAttribute("currentUser") User passwordChangeUser,
                                     BindingResult bindingResult) {
        if (userService.findById(Long.valueOf(userID)) != null) {
            if (passwordIsOK(bindingResult, true, passwordChangeUser.getPassword())) {
                User saved = changeUserPassword(Long.valueOf(userID), passwordChangeUser);
                return "redirect:/" + userTypeUpdatePage(saved) + "/" + saved.getId();
            } else {
                User found = userService.findById(Long.valueOf(userID));
                return "redirect:/" + userTypeUpdatePage(found) + "/" + found.getId();
            }
        }
        log.debug("User with ID: " + userID + " not found");
        return "redirect:/adminPage";
    }

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

    // Admin CRUD ops =======================================================================================

    @AdminRead
    @GetMapping("/createAdmin")
    public String getNewAdmin(Model model) {
        User user = User.builder().build();
        model.addAttribute("newUser", user);
        model.addAttribute("user", auxiliaryController.getUsername());
        AdminUser adminUser = AdminUser.builder().build();
        model.addAttribute("newAdmin", adminUser);
        return "adminCreate";
    }

    @AdminCreate
    @PostMapping("/createAdmin")
    public String postNewAdmin(@Valid @ModelAttribute("newAdmin") AdminUser newAdminUser,
                               BindingResult adminBindingResult, @Valid @ModelAttribute("newUser") User newUser,
                               BindingResult userBindingResult) {
        boolean checksOut = true;

        checksOut = passwordIsOK(userBindingResult, checksOut, newUser.getPassword());
        checksOut = newUser_usernameIsOK(userBindingResult, checksOut, newUser.getUsername());
        checksOut = firstName_lastName_isOK(adminBindingResult, checksOut, newAdminUser.getFirstName(),
                newAdminUser.getLastName());

        if (!checksOut) {
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
        checkUserId(adminUserID);
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
        checkUserId(adminUserID);

        User userToBeUpdated = userService.findById(Long.valueOf(adminUserID));
        //either the username is empty or is already on file
        boolean allGood = true;
        allGood = checkUsername(currentUser, userBindingResult, model, userToBeUpdated, allGood,
                userAlreadyExists(currentUser.getUsername()));

        //either the adminUser name field is empty or is already on file
        if (adminBindingResult.hasErrors()) {
            model.addAttribute("adminUserNameError", INVALID_ADMIN_NAME);
            allGood = false;
        } else if (adminUserAlreadyExists(currentAdminUser.getFirstName(), currentAdminUser.getLastName()) == null
                || (userToBeUpdated.getAdminUser().getFirstName().equals(currentAdminUser.getFirstName())) &&
                (userToBeUpdated.getAdminUser().getLastName().equals(currentAdminUser.getLastName()))){

            //AdminUser not found or AdminUser firstName and lastName not changed
            userToBeUpdated.getAdminUser().setFirstName(currentAdminUser.getFirstName());
            userToBeUpdated.getAdminUser().setLastName(currentAdminUser.getLastName());
        } else {
            model.addAttribute("adminUserExists", "AdminUser with given name already exists");
            allGood = false;
        }

        //send submitted form data back
        if (!allGood) {
            userToBeUpdated.setUsername(currentUser.getUsername());
            userToBeUpdated.getAdminUser().setFirstName(currentAdminUser.getFirstName());
            userToBeUpdated.getAdminUser().setLastName(currentAdminUser.getLastName());
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", userToBeUpdated);
            model.addAttribute("currentAdminUser", userToBeUpdated.getAdminUser());
            return "adminUpdate";
        }

        //sync. account related settings (lockout, enabled, etc.)
        syncAccountSettings(currentUser, userToBeUpdated);

        //save changes
        User saved = userService.save(userToBeUpdated);
        log.debug("Username: " + saved.getUsername() + ", adminUser name: " + saved.getAdminUser().getFirstName() +
                " " + saved.getAdminUser().getLastName() + " saved");
        model.addAttribute("AdminUserSaved", "Updates applied successfully");
        model.addAttribute("currentUser", saved);
        model.addAttribute("currentAdminUser", saved.getAdminUser());
        return "adminUpdate";
    }

    // Teacher CRUD ops =======================================================================================

    @AdminRead
    @GetMapping("/createTeacher")
    public String getNewTeacher(Model model) {
        User user = User.builder().build();
        model.addAttribute("newUser", user);
        model.addAttribute("user", auxiliaryController.getUsername());
        TeacherUser teacherUser = TeacherUser.builder().build();
        model.addAttribute("newTeacher", teacherUser);
        return "teacherCreate";
    }

    //see postNewAdmin for comments
    @AdminCreate
    @PostMapping("/createTeacher")
    public String postNewTeacher(@Valid @ModelAttribute("newTeacher") TeacherUser newTeacherUser,
                                 BindingResult teacherBindingResult, @Valid @ModelAttribute("newUser") User newUser,
                                 BindingResult userBindingResult) {
        boolean checksOut = true;

        checksOut = passwordIsOK(userBindingResult, checksOut, newUser.getPassword());
        checksOut = newUser_usernameIsOK(userBindingResult, checksOut, newUser.getUsername());
        checksOut = firstName_lastName_isOK(teacherBindingResult, checksOut, newTeacherUser.getFirstName(),
                newTeacherUser.getLastName());

        if (!checksOut) {
            return "teacherCreate";
        }

        User userFound = userAlreadyExists(newUser.getUsername());
        TeacherUser teacherUserFound = teacherUserAlreadyExists(newTeacherUser.getFirstName(), newTeacherUser.getLastName());
        if (userFound != null) {
            if (teacherUserFound != null) {
                if (teacherUserFound.getUsers().stream().anyMatch(user ->
                        user.getUsername().equals(userFound.getUsername()))) {
                    log.debug("TeacherUser is already registered with the given User");
                    teacherBindingResult.rejectValue("teacherUserName", "exists",
                            "TeacherUser provided is already registered with given User. Please change the" +
                                    " TeacherUser name.");
                    return "teacherCreate";
                }
            }
        }

        newTeacherUser(newTeacherUser, newUser);
        return "redirect:/adminPage";
    }

    @AdminUpdate
    @GetMapping("/updateTeacher/{teacherUserID}")
    public String getUpdateTeacher(Model model, @PathVariable String teacherUserID) {
        checkUserId(teacherUserID);
        User user = userService.findById(Long.valueOf(teacherUserID));
        if (user.getTeacherUser() == null) {
            log.debug("No teacherUser associated with given user");
            return "redirect:/adminPage";
        } else {
            TeacherUser teacherUser = user.getTeacherUser();
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", user);
            model.addAttribute("currentTeacherUser", teacherUser);
            return "teacherUpdate";
        }
    }

    @AdminUpdate
    @PostMapping("/updateTeacher/{teacherUserID}")
    public String postUpdateTeacherWithID(@PathVariable String teacherUserID,
                                    @Valid @ModelAttribute("currentUser") User currentUser, BindingResult userBindingResult,
                                    @Valid @ModelAttribute("currentTeacherUser") TeacherUser currentTeacherUser,
                                    BindingResult teacherBindingResult, Model model) {
        checkUserId(teacherUserID);

        User userToBeUpdated = userService.findById(Long.valueOf(teacherUserID));
        //either the username is empty or is already on file
        boolean allGood = true;
        allGood = checkUsername(currentUser, userBindingResult, model, userToBeUpdated, allGood,
                userAlreadyExists(currentUser.getUsername()));

        //either the teacherUser name field is empty or is already on file
        if (teacherBindingResult.hasErrors()) {
            model.addAttribute("teacherUserNameError", INVALID_TEACHER_NAME);
            allGood = false;
        } else if (teacherUserAlreadyExists(currentTeacherUser.getFirstName(), currentTeacherUser.getLastName()) == null
                || ((userToBeUpdated.getTeacherUser().getFirstName().equals(currentTeacherUser.getFirstName())) &&
                (userToBeUpdated.getTeacherUser().getLastName().equals(currentTeacherUser.getLastName())))) {

            //TeacherUser not found or TeacherUser firstName and lastName not changed
            userToBeUpdated.getTeacherUser().setFirstName(currentTeacherUser.getFirstName());
            userToBeUpdated.getTeacherUser().setLastName(currentTeacherUser.getLastName());
        } else {
            model.addAttribute("teacherUserExists", "TeacherUser with given name already exists");
            allGood = false;
        }

        //set department name to a default in all cases, if none entered
        if(currentTeacherUser.getDepartment() == null || currentTeacherUser.getDepartment().isEmpty()){
            currentTeacherUser.setDepartment("(no department name submitted)");
        }

        //send submitted form data back
        if (!allGood) {
            userToBeUpdated.setUsername(currentUser.getUsername());
            userToBeUpdated.getTeacherUser().setFirstName(currentTeacherUser.getFirstName());
            userToBeUpdated.getTeacherUser().setLastName(currentTeacherUser.getLastName());
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", userToBeUpdated);
            model.addAttribute("currentAdminUser", userToBeUpdated.getTeacherUser());
            return "teacherUpdate";
        }

        syncAccountSettings(currentUser, userToBeUpdated);

        //save changes
        User saved = userService.save(userToBeUpdated);
        log.debug("Username: " + saved.getUsername() + ", teacherUser name: " + saved.getTeacherUser().getFirstName() +
                " " + saved.getTeacherUser().getLastName() + " saved");
        model.addAttribute("TeacherUserSaved", "Updates applied successfully");
        model.addAttribute("currentUser", saved);
        model.addAttribute("currentTeacherUser", saved.getTeacherUser());
        return "teacherUpdate";
    }

    // Guardian CRUD ops =======================================================================================

    @AdminRead
    @GetMapping("/createGuardian")
    public String getNewGuardian(Model model) {
        User user = User.builder().build();
        model.addAttribute("newUser", user);
        model.addAttribute("user", auxiliaryController.getUsername());
        GuardianUser guardianUser = GuardianUser.builder().build();
        model.addAttribute("newGuardian", guardianUser);
        return "guardianCreate";
    }

    //see postNewAdmin for comments
    @AdminCreate
    @PostMapping("/createGuardian")
    public String postNewGuardian(@Valid @ModelAttribute("newGuardian") GuardianUser newGuardianUser,
                                  BindingResult guardianBindingResult, @Valid @ModelAttribute("newUser") User newUser,
                                  BindingResult userBindingResult) {
        boolean checksOut = true;

        checksOut = passwordIsOK(userBindingResult, checksOut, newUser.getPassword());
        checksOut = newUser_usernameIsOK(userBindingResult, checksOut, newUser.getUsername());
        checksOut = firstName_lastName_isOK(guardianBindingResult, checksOut, newGuardianUser.getFirstName(),
                newGuardianUser.getLastName());

        if (!checksOut) {
            return "guardianCreate";
        }

        User userFound = userAlreadyExists(newUser.getUsername());
        GuardianUser guardianUserFound = guardianUserAlreadyExists(newGuardianUser.getFirstName(),
                newGuardianUser.getLastName());
        if (userFound != null) {
            if (guardianUserFound != null) {
                if (guardianUserFound.getUsers().stream().anyMatch(user ->
                        user.getUsername().equals(userFound.getUsername()))) {
                    log.debug("GuardianUser is already registered with the given User");
                    guardianBindingResult.rejectValue("guardianUserName", "exists",
                            "GuardianUser provided is already registered with given User. Please change" +
                                    " the GuardianUser name.");
                    return "guardianCreate";
                }
            }
        }

        newGuardianUser(newGuardianUser, newUser);
        return "redirect:/adminPage";
    }

    @AdminUpdate
    @GetMapping("/updateGuardian/{guardianUserID}")
    public String getUpdateGuardian(Model model, @PathVariable String guardianUserID) {
        checkUserId(guardianUserID);
        User user = userService.findById(Long.valueOf(guardianUserID));
        if (user.getGuardianUser() == null) {
            log.debug("No guardianUser associated with given user");
            return "redirect:/adminPage";
        } else {
            GuardianUser guardianUser = user.getGuardianUser();
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", user);
            model.addAttribute("currentGuardianUser", guardianUser);
            return "guardianUpdate";
        }
    }

    @AdminUpdate
    @PostMapping("/updateGuardian/{guardianUserID}")
    public String postUpdateGuardianWithID(@PathVariable String guardianUserID,
                                    @Valid @ModelAttribute("currentUser") User currentUser, BindingResult userBindingResult,
                                    @Valid @ModelAttribute("currentGuardianUser") GuardianUser currentGuardianUser,
                                    BindingResult guardianBindingResult, Model model) {
        checkUserId(guardianUserID);

        User userToBeUpdated = userService.findById(Long.valueOf(guardianUserID));
        //either the username is empty or is already on file
        boolean allGood = true;
        allGood = checkUsername(currentUser, userBindingResult, model, userToBeUpdated, allGood,
                userService.findByUsername(currentUser.getUsername()));

        //either the adminUser name field is empty or is already on file
        if (guardianBindingResult.hasErrors()) {
            model.addAttribute("guardianUserNameError", INVALID_GUARDIAN_NAME);
            allGood = false;
        } else if (guardianUserAlreadyExists(currentGuardianUser.getFirstName(), currentGuardianUser.getLastName()) == null
                || (userToBeUpdated.getGuardianUser().getFirstName().equals(currentGuardianUser.getFirstName())) &&
                (userToBeUpdated.getGuardianUser().getLastName().equals(currentGuardianUser.getLastName()))){

            //GuardianUser not found or GuardianUser firstName and lastName not changed
            userToBeUpdated.getGuardianUser().setFirstName(currentGuardianUser.getFirstName());
            userToBeUpdated.getGuardianUser().setLastName(currentGuardianUser.getLastName());
        } else {
            model.addAttribute("guardianUserExists", "GuardianUser with given name already exists");
            allGood = false;
        }

        //send submitted form data back
        if (!allGood) {
            userToBeUpdated.setUsername(currentUser.getUsername());
            userToBeUpdated.getGuardianUser().setFirstName(currentGuardianUser.getFirstName());
            userToBeUpdated.getGuardianUser().setLastName(currentGuardianUser.getLastName());
            model.addAttribute("user", auxiliaryController.getUsername());
            model.addAttribute("currentUser", userToBeUpdated);
            model.addAttribute("currentGuardianUser", userToBeUpdated.getGuardianUser());
            return "guardianUpdate";
        }

        syncAccountSettings(currentUser, userToBeUpdated);

        //save changes
        User saved = userService.save(userToBeUpdated);
        log.debug("Username: " + saved.getUsername() + ", guardianUser name: " + saved.getGuardianUser().getFirstName() +
                " " + saved.getGuardianUser().getLastName() + " saved");
        model.addAttribute("GuardianUserSaved", "Updates applied successfully");
        model.addAttribute("currentUser", saved);
        model.addAttribute("currentGuardianUser", saved.getGuardianUser());
        return "guardianUpdate";
    }

    // end of CRUD ops ==========================================================================================
    //the following methods are called by the above controller methods only if the required parameters are verified

    /**
     * Validates a submitted username and checks if it already exists on the DB
     * */
    @AdminUpdate
    private boolean checkUsername(User currentUser, BindingResult userBindingResult, Model model,
                                  User userToBeUpdated, boolean allGood, User user) {
        if (userBindingResult.hasErrors()) {
            model.addAttribute("usernameError", INVALID_USERNAME);
            allGood = false;
        } else if (user == null
                || userToBeUpdated.getUsername().equals(currentUser.getUsername())) {
            //User not on file or username was not changed
            userToBeUpdated.setUsername(currentUser.getUsername());
        } else {
            model.addAttribute("usernameExists", "Username already taken");
            allGood = false;
        }
        return allGood;
    }

    /**
     * Checks a user ID prior to a GET request
     * */
    @TeacherUpdate
    private void checkUserId(String userID) {
        if (userService.findById(Long.valueOf(userID)) == null) {
            log.debug("User with ID " + userID + " not found");
            throw new NotFoundException("User with given ID not found");
        }
    }

    /**Updates contact details*/
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
    private User changeUserPassword(Long userID, User userOnFile) {
        User found = userService.findById(userID);
        found.setPassword(passwordEncoder.encode(userOnFile.getPassword()));
        User saved = userService.save(found);
        log.debug("Password change for " + saved.getUsername() + " has been saved");
        return saved;
    }

    /**
     * Checks if a AdminUser/TeacherUser/GuardianUser name property is valid
     */
    @AdminCreate
    private boolean firstName_lastName_isOK(BindingResult result, boolean checksOut, String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            log.debug("Null firstName and lastName passed");
            checksOut = false;
        } else if (result.hasErrors()) {
                result.getAllErrors().forEach(objectError -> log.debug(objectError.getDefaultMessage()));
                checksOut = false;
        }
        return checksOut;
    }

    /**
     * Checks if a User username property is valid
     */
    @AdminCreate
    private boolean newUser_usernameIsOK(BindingResult result, boolean checksOut, String username) {
        if (username == null){
            log.debug("Null username passed");
            checksOut = false;
        } else if (result.hasErrors()) {
            result.getAllErrors().forEach(objectError -> log.debug(objectError.getDefaultMessage()));
            checksOut = false;
        }
        return checksOut;
    }

    @AdminUpdate
    private boolean passwordIsOK(BindingResult result, boolean checksOut, String password) {
        if (password == null){
            log.debug("Null password passed");
            checksOut = false;
        } else if (result.hasErrors()) {
            result.getAllErrors().forEach(objectError -> log.debug(objectError.getDefaultMessage()));
            checksOut = false;
        }
        return checksOut;
    }

    @AdminUpdate
    private void syncAccountSettings(User currentUser, User userToBeUpdated) {
        userToBeUpdated.setAccountNonLocked(currentUser.isAccountNonLocked());
        userToBeUpdated.setAccountNonExpired(currentUser.isAccountNonExpired());
        userToBeUpdated.setCredentialsNonExpired(currentUser.isCredentialsNonExpired());
        userToBeUpdated.setEnabled(currentUser.isEnabled());
    }

    //assume here that all parameters are not null and not already on the DB
    @AdminUpdate
    private void newTeacherUser(TeacherUser newTeacherUser, User newUser) {
        Role teacherRole = roleService.findByRoleName("TEACHER");
        TeacherUser savedTeacherUser = teacherUserService.save(
                TeacherUser.builder()
                        .firstName(newTeacherUser.getFirstName()).lastName(newTeacherUser.getLastName()).build());
        User savedUser = userService.save(User.builder().teacherUser(savedTeacherUser)
                .username(newUser.getUsername()).password(passwordEncoder.encode(newUser.getPassword()))
                .role(teacherRole).build());
        log.debug("New Teacher name: " + savedUser.getTeacherUser().getFirstName()
                + " " + savedUser.getTeacherUser().getLastName()
                + ", with username " + savedUser.getUsername() + " and ID: " + savedUser.getId() + " added");
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
    private void newGuardianUser(GuardianUser newGuardianUser, User newUser) {
        Role guardianRole = roleService.findByRoleName("GUARDIAN");
        GuardianUser savedGuardianUser = guardianUserService.save(
                GuardianUser.builder()
                        .firstName(newGuardianUser.getFirstName()).lastName(newGuardianUser.getLastName()).build());
        User savedUser = userService.save(User.builder().guardianUser(savedGuardianUser)
                .username(newUser.getUsername()).password(passwordEncoder.encode(newUser.getPassword()))
                .role(guardianRole).build());
        log.debug("New Guardian name: " + savedUser.getGuardianUser().getFirstName()
                + " " + savedUser.getGuardianUser().getLastName()
                + ", with username " + savedUser.getUsername() + " and ID: " + savedUser.getId() + " added");
    }

    @AdminUpdate
    private User userAlreadyExists(String username){
        if (username == null || username.isEmpty()){
            log.debug("Null or empty username passed");
            return null;
        }
        if (userService.findAllByUsername(username) == null){
            log.debug("User, " + username + " not found");
            return null;
        } else {
            log.debug("User, " + username + " found");
            return userService.findByUsername(username);
        }
    }

    @AdminUpdate
    private AdminUser adminUserAlreadyExists(String firstName, String lastName){
        if (firstName == null || lastName == null){
            log.debug("AdminUser null firstName and/or lastName passed");
            return null;
        }
        if (adminUserService.findByFirstNameAndLastName(firstName, lastName) == null){
            log.debug("AdminUser not found");
            return null;
        } else
            return adminUserService.findByFirstNameAndLastName(firstName, lastName);
    }

    @AdminUpdate
    private TeacherUser teacherUserAlreadyExists(String firstName, String lastName){
        if (firstName == null || lastName == null){
            log.debug("TeacherUser null firstName and/or lastName passed");
            return null;
        }
        if (teacherUserService.findByFirstNameAndLastName(firstName, lastName) == null){
            log.debug("TeacherUser not found");
            return null;
        } else
            return teacherUserService.findByFirstNameAndLastName(firstName, lastName);
    }

    @AdminUpdate
    private GuardianUser guardianUserAlreadyExists(String firstName, String lastName){
        if (firstName == null || lastName == null){
            log.debug("GuardianUser null firstName and/or lastName passed");
            return null;
        }
        if (guardianUserService.findByFirstNameAndLastName(firstName, lastName) == null){
            log.debug("GuardianUser not found");
            return null;
        } else
            return guardianUserService.findByFirstNameAndLastName(firstName, lastName);
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
