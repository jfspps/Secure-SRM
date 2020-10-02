package com.secure_srm.bootstrap;

import com.secure_srm.model.academic.AssignmentType;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Address;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.*;
import com.secure_srm.services.TestRecordService;
import com.secure_srm.services.academicServices.AssignmentTypeService;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.AddressService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.peopleServices.FormGroupListService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile(value = {"SQL", "SDjpa"})
public class DataLoader_SDjpa implements CommandLineRunner {

    private final UserService userService;
    private final AuthorityService authorityService;
    private final RoleService roleService;
    private final AdminUserService adminUserService;
    private final GuardianUserService guardianUserService;
    private final TeacherUserService teacherUserService;
    private final TestRecordService testRecordService;
    private final PasswordEncoder passwordEncoder;

    //SRM related
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final AddressService addressService;
    private final ContactDetailService contactDetailService;
    private final FormGroupListService formGroupListService;
    private final AssignmentTypeService assignmentTypeService;

    @Override
    public void run(String... args) {
        log.debug("Starting Bootloader...");
        log.debug("Users currently on file: " + userService.findAll().size());
        log.debug("Authorities currently on file: " + authorityService.findAll().size());
        log.debug("Roles currently on file: " + roleService.findAll().size());

        if (userService.findAll().isEmpty()){
            loadSecurityData();
            loadAdminUsers();
            loadTeacherUsers();
            loadGuardianUsers();
        } else
            log.debug("Users database already contains data; no changes made");

        loadTestRecord();
        log.debug("TestRecords loaded: " + testRecordService.findAll().size());
        loadSRM_data();
    }

    private void loadTestRecord() {
        testRecordService.createTestRecord("Test record 1", "paulsmith");
        testRecordService.createTestRecord("Test record 2", "alexsmith");
        testRecordService.createTestRecord("Test record 3", "alexsmith");
    }

    private void loadSecurityData(){
        // Privileges Root > Admin > Teacher > Guardian
        // all permissions below are in relation to TestRecord CRUD ops
        // a separate set of permissions for each object type (in schools, assignment, report, exam score etc.) could be
        // implemented in future (e.g. admin to view exam results would be 'admin.examScore.read' for example

        //root authorities
        Authority createRoot = authorityService.save(Authority.builder().permission("root.create").build());
        Authority updateRoot = authorityService.save(Authority.builder().permission("root.update").build());
        Authority readRoot = authorityService.save(Authority.builder().permission("root.read").build());
        Authority deleteRoot = authorityService.save(Authority.builder().permission("root.delete").build());

        //admin authorities
        Authority createAdmin = authorityService.save(Authority.builder().permission("admin.create").build());
        Authority updateAdmin = authorityService.save(Authority.builder().permission("admin.update").build());
        Authority readAdmin = authorityService.save(Authority.builder().permission("admin.read").build());
        Authority deleteAdmin = authorityService.save(Authority.builder().permission("admin.delete").build());

        //teacher authorities
        Authority createTeacher = authorityService.save(Authority.builder().permission("teacher.create").build());
        Authority updateTeacher = authorityService.save(Authority.builder().permission("teacher.update").build());
        Authority readTeacher = authorityService.save(Authority.builder().permission("teacher.read").build());
        Authority deleteTeacher = authorityService.save(Authority.builder().permission("teacher.delete").build());

        //guardian authorities
        Authority createGuardian = authorityService.save(Authority.builder().permission("guardian.create").build());
        Authority updateGuardian = authorityService.save(Authority.builder().permission("guardian.update").build());
        Authority readGuardian = authorityService.save(Authority.builder().permission("guardian.read").build());
        Authority deleteGuardian = authorityService.save(Authority.builder().permission("guardian.delete").build());

        Role rootRole = roleService.save(Role.builder().roleName("ROOT").build());
        Role adminRole = roleService.save(Role.builder().roleName("ADMIN").build());
        Role teacherRole = roleService.save(Role.builder().roleName("TEACHER").build());
        Role guardianRole = roleService.save(Role.builder().roleName("GUARDIAN").build());

        //Set.Of returns an immutable set, so new HashSet instantiates a mutable Set
        rootRole.setAuthorities(new HashSet<>(Set.of(createRoot, readRoot, updateRoot, deleteRoot,
                createAdmin, updateAdmin, readAdmin, deleteAdmin,
                createTeacher, readTeacher, updateTeacher, deleteTeacher,
                createGuardian, readGuardian, updateGuardian, deleteGuardian)));

        adminRole.setAuthorities(new HashSet<>(Set.of(createAdmin, updateAdmin, readAdmin, deleteAdmin,
                createTeacher, readTeacher, updateTeacher, deleteTeacher,
                createGuardian, readGuardian, updateGuardian, deleteGuardian)));

        teacherRole.setAuthorities(new HashSet<>(Set.of(createTeacher, readTeacher, updateTeacher, deleteTeacher,
                createGuardian, readGuardian, updateGuardian, deleteGuardian)));

        guardianRole.setAuthorities(new HashSet<>(Set.of(createGuardian, readGuardian, updateGuardian, deleteGuardian)));

        roleService.save(rootRole);
        roleService.save(adminRole);
        roleService.save(teacherRole);
        roleService.save(guardianRole);

        log.debug("Roles added: " + roleService.findAll().size());
        log.debug("Authorities added: " + authorityService.findAll().size());
    }

    public void loadAdminUsers(){
        Role adminRole = roleService.findByRoleName("ADMIN");

        // Instantiating the admin users (this must be done after Users)
        // AdminUsers can store non-Security related fields (department, academic year etc.)
        // Note, UserName is not Username
        AdminUser johnSmith = AdminUser.builder().firstName("John").lastName("Smith").build();
        AdminUser amySmith = AdminUser.builder().firstName("Amy").lastName("Smith").build();

        //set contact details to Admin
        ContactDetail johnContacts = contactDetailService.save(
                ContactDetail.builder().email("jsAdmin@school.com").phoneNumber("92874328").build());
        ContactDetail amyContacts = contactDetailService.save(
                ContactDetail.builder().email("asAdmin@school.com").phoneNumber("46364345").build());
        johnSmith.setContactDetail(johnContacts);
        amySmith.setContactDetail(amyContacts);

        //save Adminuser's
        adminUserService.save(johnSmith);
        adminUserService.save(amySmith);

        //passwords are not displayed on the schema...?
        User johnSmithUser = userService.save(User.builder().username("johnsmith")
                .password(passwordEncoder.encode("johnsmith123"))
                .adminUser(johnSmith)
                .role(adminRole).build());

        User amySmithUser = userService.save(User.builder().username("amysmith")
                .password(passwordEncoder.encode("amysmith123"))
                .adminUser(amySmith)
                .role(adminRole).build());

        log.debug("AdminUsers added: " + johnSmithUser.getUsername() + " " + amySmithUser.getUsername());
    }

    public void loadGuardianUsers(){
        Role guardianRole = roleService.findByRoleName("GUARDIAN");

        // Instantiating the admin users (this must be done after Users)
        GuardianUser paulSmith = guardianUserService.save(GuardianUser.builder().firstName("Paul").lastName("Smith").build());
        GuardianUser alexSmith = guardianUserService.save(GuardianUser.builder().firstName("Alex").lastName("Smith").build());

        //passwords are not displayed on the schema...?
        User paulSmithUser = userService.save(User.builder().username("paulsmith")
                .password(passwordEncoder.encode("paulsmith123"))
                .guardianUser(paulSmith)
                .role(guardianRole).build());
        //other GuardianUsers can be assigned to paulsmith, with the same roles

        User alexSmithUser = userService.save(User.builder().username("alexsmith")
                .password(passwordEncoder.encode("alexsmith123"))
                .guardianUser(alexSmith)
                .role(guardianRole).build());

        log.debug("GuardianUsers added: " + paulSmithUser.getUsername() + " " + alexSmithUser.getUsername());
    }

    public void loadTeacherUsers(){
        Role teacherRole = roleService.findByRoleName("TEACHER");

        // Instantiating the admin users (this must be done after Users)
        TeacherUser keithJones = teacherUserService.save(TeacherUser.builder().firstName("Keith").lastName("Jones")
                .department("Mathematics dept.").build());
        TeacherUser maryManning = teacherUserService.save(TeacherUser.builder().firstName("Mary").lastName("Manning")
                .department("English dept.").build());

        //passwords are not displayed on the schema...?
        User keithJonesUser = userService.save(User.builder().username("keithjones")
                .password(passwordEncoder.encode("keithjones123"))
                .teacherUser(keithJones)
                .role(teacherRole).build());

        User maryManningUser = userService.save(User.builder().username("marymanning")
                .password(passwordEncoder.encode("marymanning123"))
                .teacherUser(maryManning)
                .role(teacherRole).build());

        log.debug("TeacherUsers added: " + keithJonesUser.getUsername() + " " + maryManningUser.getUsername());
    }

    public void loadSRM_data(){
        //build a temporary POJO from Student, Teacher and Guardian classes and add (inject) to each respective service
        //three students, two teachers and two guardians

        //contact details
        ContactDetail teacher1Contact = ContactDetail.builder().email("teacher1@school.com").phoneNumber("9847324").build();
        ContactDetail teacher2Contact = ContactDetail.builder().email("teacher2@school.com").phoneNumber("4023307").build();
        contactDetailService.save(teacher1Contact);
        contactDetailService.save(teacher2Contact);

        ContactDetail guardianContactDetail1 = ContactDetail.builder().phoneNumber("3479324732").email("guardian1@email.com").build();
        ContactDetail guardianContactDetail2 = ContactDetail.builder().phoneNumber("02374320427").email("guardian2@email.com").build();
        contactDetailService.save(guardianContactDetail1);
        contactDetailService.save(guardianContactDetail2);
        log.debug("Contact details loaded to DB");

        //addresses
        Address address1 = Address.builder().firstLine("88 Penine Way").secondLine("Farnborough").postcode("CHG9475JF").build();
        Address address2 = Address.builder().firstLine("7B Gossfer Drive").secondLine("Racoon City").postcode("ZJGKF97657DD").build();
        addressService.save(address1);
        addressService.save(address2);
        log.debug("Addresses loaded to DB");

        Student student1 = Student.builder().firstName("John").middleNames("").lastName("Smith").build();
        Student student2 = Student.builder().firstName("Elizabeth").middleNames("").lastName("Jones").build();
        Student student3 = Student.builder().firstName("Helen").middleNames("").lastName("Jones").build();

        TeacherUser teacher1 = teacherUserService.findByFirstNameAndLastName("Keith", "Jones");
        teacher1.setContactDetail(teacher1Contact);

        TeacherUser teacher2 = teacherUserService.findByFirstNameAndLastName("Mary", "Manning");
        teacher2.setContactDetail(teacher2Contact);

        //academic details
        Subject subject1 = Subject.builder().subjectName("Mathematics").build();
        Set<TeacherUser> teachers1 = new HashSet<>();
        teachers1.add(teacher1);
        subject1.setTeachers(teachers1);
        Set<Subject> teacher1subjects = new HashSet<>();
        teacher1subjects.add(subject1);
        teacher1.setSubjects(teacher1subjects);

        Subject subject2 = Subject.builder().subjectName("English").build();
        Set<TeacherUser> teachers2 = new HashSet<>();
        teachers2.add(teacher2);
        subject2.setTeachers(teachers2);
        Set<Subject> teacher2subjects = new HashSet<>();
        teacher2subjects.add(subject2);
        teacher2.setSubjects(teacher2subjects);

        subjectService.save(subject1);
        subjectService.save(subject2);
        log.debug("Subjects loaded to DB");

        teacherUserService.save(teacher1);
        teacherUserService.save(teacher2);
        log.debug("Teachers re-loaded to DB");

        GuardianUser guardian1 = guardianUserService.findByFirstNameAndLastName("Paul", "Smith");
        guardian1.setAddress(address1);
        guardian1.setContactDetail(guardianContactDetail1);

        GuardianUser guardian2 = guardianUserService.findByFirstNameAndLastName("Alex", "Smith");
        guardian2.setAddress(address2);
        guardian2.setContactDetail(guardianContactDetail2);

        //set students' tutors
        student1.setTeacher(teacher1);
        student2.setTeacher(teacher2);
        student3.setTeacher(teacher2);

        //set students' contact details and guardians
        student1.setContactDetail(guardianContactDetail1);
        student2.setContactDetail(guardianContactDetail2);
        student3.setContactDetail(guardianContactDetail2);

        //set Guardian 1 and student 1 relationships
        Set<GuardianUser> student1Guardians = new HashSet<>();
        student1Guardians.add(guardian1);
        student1.setGuardians(student1Guardians);

        Set<Student> guardian1Students = new HashSet<>();
        guardian1Students.add(student1);
        guardian1.setStudents(guardian1Students);

        //set Guardian 2 and students 2&3 relationships
        Set<GuardianUser> student2Guardians = new HashSet<>();
        Set<Student> guardian2Students = new HashSet<>();
        student2Guardians.add(guardian2);
        student2.setGuardians(student2Guardians);
        student3.setGuardians(student2Guardians);

        guardian2Students.add(student2);
        guardian2Students.add(student3);
        guardian2.setStudents(guardian2Students);

        guardianUserService.save(guardian1);
        guardianUserService.save(guardian2);
        log.debug("Guardians re-loaded to DB");

        //Pastoral (form) groups
        Set<Student> studentGroup1 = new HashSet<>();
        studentGroup1.add(student1);
        Set<Student> studentGroup2 = new HashSet<>();
        studentGroup1.add(student2);
        studentGroup2.add(student3);
        FormGroupList formGroupList1 = FormGroupList.builder().studentList(studentGroup1).groupName("Group 1").teacher(teacher1).build();
        FormGroupList formGroupList2 = FormGroupList.builder().studentList(studentGroup2).groupName("Group 2").teacher(teacher2).build();
        student1.setFormGroupList(formGroupList1);
        student2.setFormGroupList(formGroupList2);
        student3.setFormGroupList(formGroupList2);

        formGroupListService.save(formGroupList1);
        formGroupListService.save(formGroupList2);
        log.debug("FormGroupList loaded to DB");

        studentService.save(student1);
        studentService.save(student2);
        studentService.save(student3);
        log.debug("Students loaded to DB");

        assignmentTypeService.save(AssignmentType.builder().description("Mock exam").build());
        assignmentTypeService.save(AssignmentType.builder().description("Coursework").build());
        assignmentTypeService.save(AssignmentType.builder().description("Quiz").build());
        assignmentTypeService.save(AssignmentType.builder().description("Interview").build());
        assignmentTypeService.save(AssignmentType.builder().description("Service").build());
        log.debug("Assignment types loaded to DB");

        log.debug("================ Finished uploading SRM data to DB ============");
    }
}
