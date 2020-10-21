package com.secure_srm.bootstrap;

import com.secure_srm.model.academic.*;
import com.secure_srm.model.people.*;
import com.secure_srm.model.security.*;
import com.secure_srm.services.TestRecordService;
import com.secure_srm.services.academicServices.*;
import com.secure_srm.services.peopleServices.*;
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
    private final SubjectClassListService subjectClassListService;
    private final StudentTaskService studentTaskService;
    private final StudentResultService studentResultService;
    private final ThresholdService thresholdService;
    private final ThresholdListService thresholdListService;

    @Override
    public void run(String... args) {
        log.debug("Starting Bootloader...");

        if (authorityService.findAll().isEmpty() || roleService.findAll().isEmpty()){
            loadSecurityData();
        } else {
            log.debug("Authority and Roles already found. No changes made.");
        }

        if (adminUserService.findAll().isEmpty()){
            loadAdminUsers();
        } else {
            log.debug("Admin users already found. No changes made.");
        }

        if (teacherUserService.findAll().isEmpty()){
            loadTeacherUsers();
        } else {
            log.debug("Teacher users already found. No changes made.");
        }

        if (guardianUserService.findAll().isEmpty()){
            loadGuardianUsers();
        } else {
            log.debug("Guardian users already found. No changes made.");
        }

        if (testRecordService.findAll().isEmpty()){
            loadTestRecord();
            log.debug("TestRecords loaded: " + testRecordService.findAll().size());
        } else {
            log.debug("TestRecords already found. No changes made.");
        }

        loadSRM_personnel_data();
        loadSRM_academic_data();
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

    public void loadSRM_personnel_data(){
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

        TeacherUser keithJonesTeacher = teacherUserService.findByFirstNameAndLastName("Keith", "Jones");
        keithJonesTeacher.setContactDetail(teacher1Contact);

        TeacherUser maryManningTeacher = teacherUserService.findByFirstNameAndLastName("Mary", "Manning");
        maryManningTeacher.setContactDetail(teacher2Contact);

        //academic details
        Subject mathematics = Subject.builder().subjectName("Mathematics").build();
        Set<TeacherUser> teachers1 = new HashSet<>();
        teachers1.add(keithJonesTeacher);
        mathematics.setTeachers(teachers1);
        Set<Subject> teacher1subjects = new HashSet<>();
        teacher1subjects.add(mathematics);
        keithJonesTeacher.setSubjects(teacher1subjects);

        Subject english = Subject.builder().subjectName("English").build();
        Set<TeacherUser> teachers2 = new HashSet<>();
        teachers2.add(maryManningTeacher);
        english.setTeachers(teachers2);
        Set<Subject> teacher2subjects = new HashSet<>();
        teacher2subjects.add(english);
        maryManningTeacher.setSubjects(teacher2subjects);

        subjectService.save(mathematics);
        subjectService.save(english);
        log.debug("Subjects loaded to DB");

        teacherUserService.save(keithJonesTeacher);
        teacherUserService.save(maryManningTeacher);
        log.debug("Teachers re-loaded to DB");

        GuardianUser guardian1 = guardianUserService.findByFirstNameAndLastName("Paul", "Smith");
        guardian1.setAddress(address1);
        guardian1.setContactDetail(guardianContactDetail1);

        GuardianUser guardian2 = guardianUserService.findByFirstNameAndLastName("Alex", "Smith");
        guardian2.setAddress(address2);
        guardian2.setContactDetail(guardianContactDetail2);

        //set students' tutors
        student1.setTeacher(keithJonesTeacher);
        student2.setTeacher(maryManningTeacher);
        student3.setTeacher(maryManningTeacher);

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
        FormGroupList formGroupList1 = FormGroupList.builder().studentList(studentGroup1).groupName("Group 1").teacher(keithJonesTeacher).build();
        FormGroupList formGroupList2 = FormGroupList.builder().studentList(studentGroup2).groupName("Group 2").teacher(maryManningTeacher).build();
        student1.setFormGroupList(formGroupList1);
        student2.setFormGroupList(formGroupList2);
        student3.setFormGroupList(formGroupList2);

        formGroupListService.save(formGroupList1);
        formGroupListService.save(formGroupList2);
        log.debug("FormGroupList loaded to DB");

        //Subject class lists
        SubjectClassList subjectClassList_Math = SubjectClassList.builder().subject(mathematics).teacher(keithJonesTeacher).groupName("Math_101").build();
        SubjectClassList subjectClassList_Eng = SubjectClassList.builder().subject(english).teacher(maryManningTeacher).groupName("English_101").build();

        //add Students to class lists
        Set<SubjectClassList> studentSubjectsList = new HashSet<>(Set.of(subjectClassList_Eng, subjectClassList_Math));
        student1.setSubjectClassLists(studentSubjectsList);
        student2.setSubjectClassLists(studentSubjectsList);
        student3.setSubjectClassLists(studentSubjectsList);
        subjectClassList_Eng.setStudentList(Set.of(student1, student2, student3));
        subjectClassList_Math.setStudentList(Set.of(student1, student2, student3));

        subjectClassListService.save(subjectClassList_Eng);
        subjectClassListService.save(subjectClassList_Math);
        log.debug("SubjectClassList loaded to DB");

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

        log.debug("================ Finished uploading SRM personnel data to DB ============");
    }

    private void loadSRM_academic_data() {
        TeacherUser keithjones = teacherUserService.findByFirstNameAndLastName("Keith", "Jones");
        TeacherUser marymanning = teacherUserService.findByFirstNameAndLastName("Mary", "Manning");

        AssignmentType coursework = assignmentTypeService.findByDescription("Coursework");
        AssignmentType interview = assignmentTypeService.findByDescription("Interview");
        AssignmentType quiz = assignmentTypeService.findByDescription("Quiz");

        //Build studentTasks
        StudentTask EnglishEssayTask = StudentTask.builder()
                .assignmentType(coursework)
                .contributor(true)
                .maxScore("60")
                .studentResults(new HashSet<>())
                .subject(subjectService.findBySubjectName("English"))
                .teacherUploader(marymanning)
                .title("William Wordsworth")
                .build();

        StudentTask EnglishEssayPlay = StudentTask.builder()
                .assignmentType(interview)
                .contributor(true)
                .maxScore("40")
                .studentResults(new HashSet<>())
                .subject(subjectService.findBySubjectName("English"))
                .teacherUploader(marymanning)
                .title("Elizabethan times")
                .build();

        StudentTask Calculus1Quiz = StudentTask.builder()
                .assignmentType(quiz)
                .contributor(true)
                .maxScore("100")
                .studentResults(new HashSet<>())
                .subject(subjectService.findBySubjectName("Mathematics"))
                .teacherUploader(keithjones)
                .title("Single variable calc. 1")
                .build();

        StudentTask Statistics3Quiz = StudentTask.builder()
                .assignmentType(quiz)
                .contributor(true)
                .maxScore("100")
                .studentResults(new HashSet<>())
                .subject(subjectService.findBySubjectName("Mathematics"))
                .teacherUploader(keithjones)
                .title("Intro Stats 3")
                .build();

        //build StudentResults: EnglishEssayTask /60
        StudentResult student1EnglishEssayTask = StudentResult.builder()
                .studentTask(EnglishEssayTask)
                .student(studentService.findById(1L))
                .score("38")
                .comments("Good!")
                .teacher(marymanning)
                .build();

        StudentResult student2EnglishEssayTask = StudentResult.builder()
                .studentTask(EnglishEssayTask)
                .student(studentService.findById(2L))
                .score("30")
                .comments("Well written!")
                .teacher(marymanning)
                .build();

        StudentResult student3EnglishEssayTask = StudentResult.builder()
                .studentTask(EnglishEssayTask)
                .student(studentService.findById(3L))
                .score("48")
                .comments("Great work!")
                .teacher(marymanning)
                .build();
        EnglishEssayTask.setStudentResults(Set.of(student1EnglishEssayTask, student2EnglishEssayTask, student3EnglishEssayTask));
        coursework.setStudentResults(Set.of(student1EnglishEssayTask, student2EnglishEssayTask, student3EnglishEssayTask));

        //build StudentResults: EnglishEssayPlay /40
        StudentResult student1EnglishEssayPlay = StudentResult.builder()
                .studentTask(EnglishEssayPlay)
                .student(studentService.findById(1L))
                .score("25")
                .comments("Good stuff!")
                .teacher(keithjones)
                .build();

        StudentResult student2EnglishEssayPlay = StudentResult.builder()
                .studentTask(EnglishEssayPlay)
                .student(studentService.findById(2L))
                .comments("Absent")
                .teacher(marymanning)
                .build();

        StudentResult student3EnglishEssayPlay = StudentResult.builder()
                .studentTask(EnglishEssayPlay)
                .student(studentService.findById(3L))
                .score("35")
                .comments("Did very well!")
                .teacher(marymanning)
                .build();
        EnglishEssayPlay.setStudentResults(Set.of(student1EnglishEssayPlay, student2EnglishEssayPlay, student3EnglishEssayPlay));
        interview.setStudentResults(Set.of(student1EnglishEssayPlay, student2EnglishEssayPlay, student3EnglishEssayPlay));

        //build StudentResults: Calculus1Quiz /100
        StudentResult student1Calculus1Quiz = StudentResult.builder()
                .studentTask(Calculus1Quiz)
                .student(studentService.findById(1L))
                .score("83")
                .comments("Excellent")
                .teacher(keithjones)
                .build();

        StudentResult student2Calculus1Quiz = StudentResult.builder()
                .studentTask(Calculus1Quiz)
                .student(studentService.findById(2L))
                .score("61")
                .comments("Good effort")
                .teacher(keithjones)
                .build();

        StudentResult student3Calculus1Quiz = StudentResult.builder()
                .studentTask(Calculus1Quiz)
                .student(studentService.findById(3L))
                .score("65")
                .comments("Did well")
                .teacher(keithjones)
                .build();
        Calculus1Quiz.setStudentResults(Set.of(student1Calculus1Quiz, student2Calculus1Quiz, student3Calculus1Quiz));

        //build StudentResults: Statistics3Quiz /100
        StudentResult student1Statistics3Quiz = StudentResult.builder()
                .studentTask(Statistics3Quiz)
                .student(studentService.findById(1L))
                .score("88")
                .comments("Excellent")
                .teacher(keithjones)
                .build();

        StudentResult student2Statistics3Quiz = StudentResult.builder()
                .studentTask(Statistics3Quiz)
                .student(studentService.findById(2L))
                .score("70")
                .comments("Good work")
                .teacher(keithjones)
                .build();

        StudentResult student3Statistics3Quiz = StudentResult.builder()
                .studentTask(Statistics3Quiz)
                .student(studentService.findById(3L))
                .score("71")
                .comments("Did very well")
                .teacher(keithjones)
                .build();

        quiz.setStudentResults(Set.of(student1Calculus1Quiz, student2Calculus1Quiz, student3Calculus1Quiz,
                student1Statistics3Quiz, student2Statistics3Quiz, student3Statistics3Quiz));

        studentTaskService.save(EnglishEssayPlay);
        studentTaskService.save(EnglishEssayTask);
        studentTaskService.save(Calculus1Quiz);
        studentTaskService.save(Statistics3Quiz);
        log.debug("Student tasks saved");

        studentResultService.save(student1Calculus1Quiz);
        studentResultService.save(student1Statistics3Quiz);
        studentResultService.save(student1EnglishEssayPlay);
        studentResultService.save(student1EnglishEssayTask);

        studentResultService.save(student2Calculus1Quiz);
        studentResultService.save(student2Statistics3Quiz);
        studentResultService.save(student2EnglishEssayPlay);
        studentResultService.save(student2EnglishEssayTask);

        studentResultService.save(student3Calculus1Quiz);
        studentResultService.save(student3Statistics3Quiz);
        studentResultService.save(student3EnglishEssayPlay);
        studentResultService.save(student3EnglishEssayTask);
        log.debug("Student results saved");

        assignmentTypeService.save(quiz);
        assignmentTypeService.save(coursework);
        assignmentTypeService.save(interview);
        log.debug("Assignment types updated");

        //English thresholds, EnglishEssayTask /60, EnglishEssayPlay /40
        Threshold EnglishEssayPassThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(25)
                .alphabetical("PASS")
                .uniqueId("EnglishEssayPassThreshold")
                .uploader(marymanning)
                .build();

        Threshold EnglishEssayMeritThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(35)
                .alphabetical("MERIT")
                .uniqueId("EnglishEssayMeritThreshold")
                .uploader(marymanning)
                .build();

        Threshold EnglishEssayDistinctionThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(45)
                .alphabetical("DISTINCTION")
                .uniqueId("EnglishEssayDistinctionThreshold")
                .uploader(marymanning)
                .build();

        Threshold EnglishPlayPassThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(15)
                .alphabetical("PASS")
                .uniqueId("EnglishPlayPassThreshold")
                .uploader(marymanning)
                .build();

        Threshold EnglishPlayMeritThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(25)
                .alphabetical("MERIT")
                .uniqueId("EnglishPlayMeritThreshold")
                .uploader(marymanning)
                .build();

        Threshold EnglishPlayDistinctionThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(33)
                .alphabetical("DISTINCTION")
                .uniqueId("EnglishPlayDistinctionThreshold")
                .uploader(marymanning)
                .build();

        //Math thresholds, /100
        Threshold MathPassThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(40)
                .alphabetical("PASS")
                .uniqueId("MathPassThreshold")
                .uploader(keithjones)
                .build();

        Threshold MathDThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(50)
                .alphabetical("D")
                .uniqueId("MathDThreshold")
                .uploader(keithjones)
                .build();

        Threshold MathCThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(60)
                .alphabetical("C")
                .uniqueId("MathCThreshold")
                .uploader(keithjones)
                .build();

        Threshold MathBThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(70)
                .alphabetical("B")
                .uniqueId("MathBThreshold")
                .uploader(keithjones)
                .build();

        Threshold MathAThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(80)
                .alphabetical("A")
                .uniqueId("MathAThreshold")
                .uploader(keithjones)
                .build();

        Threshold MathAPlusThreshold = Threshold.builder().thresholdLists(new HashSet<>())
                .numerical(90)
                .alphabetical("A+")
                .uniqueId("MathAPlusThreshold")
                .uploader(keithjones)
                .build();

        //threshold lists
        ThresholdList EnglishEssayThresholds = ThresholdList.builder()
                .thresholds(Set.of(EnglishEssayDistinctionThreshold, EnglishEssayMeritThreshold, EnglishEssayPassThreshold))
                .uniqueID("English essay grades")
                .uploader(marymanning).build();

        ThresholdList EnglishPlayThresholds = ThresholdList.builder()
                .thresholds(Set.of(EnglishPlayDistinctionThreshold, EnglishPlayMeritThreshold, EnglishPlayPassThreshold))
                .uniqueID("English play grades")
                .uploader(marymanning).build();

        ThresholdList MathThresholds = ThresholdList.builder()
                .thresholds(Set.of(MathPassThreshold, MathDThreshold, MathCThreshold, MathBThreshold, MathAThreshold, MathAPlusThreshold))
                .uniqueID("Math thresholds")
                .uploader(keithjones).build();

        EnglishEssayDistinctionThreshold.setThresholdLists(Set.of(EnglishEssayThresholds));
        EnglishEssayMeritThreshold.setThresholdLists(Set.of(EnglishEssayThresholds));
        EnglishEssayPassThreshold.setThresholdLists(Set.of(EnglishEssayThresholds));

        EnglishPlayDistinctionThreshold.setThresholdLists(Set.of(EnglishPlayThresholds));
        EnglishPlayMeritThreshold.setThresholdLists(Set.of(EnglishPlayThresholds));
        EnglishPlayPassThreshold.setThresholdLists(Set.of(EnglishPlayThresholds));

        MathPassThreshold.setThresholdLists(Set.of(MathThresholds));
        MathDThreshold.setThresholdLists(Set.of(MathThresholds));
        MathCThreshold.setThresholdLists(Set.of(MathThresholds));
        MathBThreshold.setThresholdLists(Set.of(MathThresholds));
        MathAThreshold.setThresholdLists(Set.of(MathThresholds));
        MathAPlusThreshold.setThresholdLists(Set.of(MathThresholds));

        thresholdService.save(EnglishEssayDistinctionThreshold);
        thresholdService.save(EnglishEssayMeritThreshold);
        thresholdService.save(EnglishEssayPassThreshold);

        thresholdService.save(EnglishPlayDistinctionThreshold);
        thresholdService.save(EnglishPlayMeritThreshold);
        thresholdService.save(EnglishPlayPassThreshold);

        thresholdService.save(MathPassThreshold);
        thresholdService.save(MathDThreshold);
        thresholdService.save(MathCThreshold);
        thresholdService.save(MathBThreshold);
        thresholdService.save(MathAThreshold);
        thresholdService.save(MathAPlusThreshold);
        log.debug("Thresholds saved");

        //save threshold-lists AFTER thresholds

        thresholdListService.save(EnglishEssayThresholds);
        thresholdListService.save(EnglishPlayThresholds);
        thresholdListService.save(MathThresholds);
        log.debug("Threshold lists saved");

        log.debug("================ Finished uploading SRM academic data to DB ============");
    }
}
