package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.*;
import com.secure_srm.model.people.*;
import com.secure_srm.model.security.*;
import com.secure_srm.services.peopleServices.AddressService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.AdminDelete;
import com.secure_srm.web.permissionAnnot.AdminUpdate;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//AuxiliaryController handles commonly used methods across all controllers
@Slf4j
@RequiredArgsConstructor
@Controller
public class AuxiliaryController {

    private final UserService userService;
    private final AddressService addressService;
    private final ContactDetailService contactDetailService;
    private final StudentService studentService;
    private final GuardianUserService guardianUserService;

    /**
     * Get the username of the User
     * */
    public String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    /**
     * Get the user's ID
     * */
    public String getUserId() {
        User found = userService.findByUsername(getUsername());
        if (found != null) {
            return found.getId().toString();
        } else {
            log.debug("User with given ID not found");
            throw new NotFoundException("User with given ID not found");
        }
    }

    /**
     * Returns false if a user is not a teacher, or, is a teacher but is not assigned a subject
     * */
    public Boolean teachesASubject(){
        if (userService.findByUsername(getUsername()) != null){
            User currentUser = userService.findByUsername(getUsername());
            return currentUser.getTeacherUser() != null && !currentUser.getTeacherUser().getSubjects().isEmpty();
        }
        return false;
    }

    /**
     * Returns the current TeacherUser
     * */
    public TeacherUser getCurrentTeacherUser(){
        TeacherUser found = userService.findByUsername(getUsername()).getTeacherUser();
        if (found != null){
            return found;
        } else {
            log.debug("TeacherUser not found");
            return null;
        }
    }

    /**
     * Returns the current AdminUser
     * */
    public AdminUser getCurrentAdminUser(){
        AdminUser found = userService.findByUsername(getUsername()).getAdminUser();
        if (found != null){
            return found;
        } else {
            log.debug("AdminUser not found");
            return null;
        }
    }

    /**
     * Returns the current GuardianUser
     * */
    public GuardianUser getCurrentGuardianUser(){
        GuardianUser found = userService.findByUsername(getUsername()).getGuardianUser();
        if (found != null){
            return found;
        } else {
            log.debug("GuardianUser not found");
            return null;
        }
    }

    /**
     * Returns the current RootUser
     * */
    public RootUser getCurrentRootUser(){
        RootUser found = userService.findByUsername(getUsername()).getRootUser();
        if (found != null){
            return found;
        } else {
            log.debug("RootUser not found");
            return null;
        }
    }

    /**
     * Returns the current User
     * */
    public User getCurrentUser(){
        User found = userService.findByUsername(getUsername());
        if (found != null){
            return found;
        } else {
            log.debug("User not found");
            return null;
        }
    }

    /**
     * Returns an ArrayList of items, sorted by subject title
     * */
    public List<Subject> sortSetBySubjectName(Set<Subject> subjectSet) {
        List<Subject> listBySubjectName = new ArrayList<>(subjectSet);
        //see Student's model string comparison method, compareTo()
        Collections.sort(listBySubjectName);
        return listBySubjectName;
    }

    /**
     * Returns an ArrayList of items, sorted by teacher's last name
     * */
    public List<TeacherUser> sortTeacherSetByLastName(Set<TeacherUser> teacherUserSet) {
        List<TeacherUser> listByLstName = new ArrayList<>(teacherUserSet);
        //see Teacher's model string comparison method, compareTo()
        Collections.sort(listByLstName);
        return listByLstName;
    }

    /**
     * Returns an ArrayList of items, sorted by Groupname
     */
    public List<SubjectClassList> sortSubjectClassSetByGroupName(Set<SubjectClassList> subjectClassListSet) {
        List<SubjectClassList> listByGroupName = new ArrayList<>(subjectClassListSet);
        //see SubjectClassList's model string comparison method, compareTo()
        Collections.sort(listByGroupName);
        return listByGroupName;
    }

    /**
     * Returns an ArrayList of items, sorted by student's lastName
     */
    public List<Student> sortStudentSetByLastName(Set<Student> studentSet) {
        List<Student> listByLastName = new ArrayList<>(studentSet);
        //see Student's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }

    /**
     * Returns an ArrayList of items, sorted by assignmentType's description
     * */
    public List<AssignmentType> sortAssignmentTypeSetByDescription(Set<AssignmentType> assignmentTypeSet) {
        List<AssignmentType> listByDescription = new ArrayList<>(assignmentTypeSet);
        //see StudentTasks's model string comparison method, compareTo()
        Collections.sort(listByDescription);
        return listByDescription;
    }

    /**
     * Returns an ArrayList of items, sorted by guardian's lastName
     * */
    public List<GuardianUser> sortGuardianSetByLastName(Set<GuardianUser> guardianUserSet) {
        List<GuardianUser> listByLastName = new ArrayList<>(guardianUserSet);
        //see Guardian's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }

    /**
     * Returns an ArrayList of items, sorted by Groupname
     */
    public List<FormGroupList> sortFormGroupSetByGroupName(Set<FormGroupList> formGroupListSet) {
        List<FormGroupList> listByLastName = new ArrayList<>(formGroupListSet);
        //see FormGroupList's model string comparison method, compareTo()
        Collections.sort(listByLastName);
        return listByLastName;
    }

    /**
     * Returns an ArrayList of items, sorted by UniqueID
     */
    public List<Threshold> sortThresholdByUniqueID(Set<Threshold> thresholdSet) {
        List<Threshold> listByUniqueID = new ArrayList<>(thresholdSet);
        //see Threshold's model string comparison method, compareTo()
        Collections.sort(listByUniqueID);
        return listByUniqueID;
    }

    /**
     * Returns an ArrayList of items, sorted by UniqueID
     */
    public List<ThresholdList> sortThresholdListByUniqueID(Set<ThresholdList> thresholdListSet) {
        List<ThresholdList> listByUniqueID = new ArrayList<>(thresholdListSet);
        //see ThresholdList's model string comparison method, compareTo()
        Collections.sort(listByUniqueID);
        return listByUniqueID;
    }

    /**
     * Returns an ArrayList of items, sorted by UniqueID
     */
    public List<StudentTask> sortStudentTaskByTitle(Set<StudentTask> studentTaskSet) {
        List<StudentTask> listByTitle = new ArrayList<>(studentTaskSet);
        //see StudentTask's model string comparison method, compareTo()
        Collections.sort(listByTitle);
        return listByTitle;
    }

    @AdminDelete
    public void deleteGuardianRecord(GuardianUser guardianUser) {
        // remove security credentials
        // Each User has different credentials and a GuardianUser may be granted different privileges
        Set<User> userSet = guardianUser.getUsers();
        userSet.forEach(userService::delete);

        // delete Address (Address would be dangling)
        Address foundAddress = guardianUser.getAddress();
        guardianUser.setAddress(null);
        addressService.delete(foundAddress);

        // retrieve ContactDetails and free Guardian's reference
        ContactDetail foundContacts = guardianUser.getContactDetail();
        guardianUser.setContactDetail(null);

        // update Student records
        Set<Student> studentSet = guardianUser.getStudents();
        guardianUser.setStudents(null);
        studentSet.forEach(student -> {
            if (student.getContactDetail().equals(foundContacts)){
                student.setContactDetail(null);
            }
            student.getGuardians().remove(guardianUser);
            studentService.save(student);
        });

        contactDetailService.delete(foundContacts);
        guardianUserService.delete(guardianUser);
    }
}
