package com.secure_srm.bootstrap;

import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Address;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.people.FormGroupList;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.AddressService;
import com.secure_srm.services.peopleServices.ContactDetailService;
import com.secure_srm.services.peopleServices.FormGroupListService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.GuardianUserService;
import com.secure_srm.services.securityServices.TeacherUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

//set as a Spring Bean with @Component, and run run()
@Slf4j
//@Profile(value = {"SQL", "SDjpa"})
@RequiredArgsConstructor
//@Component
public class SRM_dataLoader_BAK implements CommandLineRunner {

    //this block and the constructor below ensure that the services persist
    private final GuardianUserService guardianUserService;
    private final StudentService studentService;
    private final TeacherUserService teacherUserService;
    private final SubjectService subjectService;
    private final AddressService addressService;
    private final ContactDetailService contactDetailService;
    private final FormGroupListService formGroupListService;

    @Override
    public void run(String... args) throws Exception {
        //check if there is anything on the DB before reloading (the last one to be saved is studentService so check that)

        log.debug("Starting MySQL initialisation");
        if (studentService.findAll().size() == 0) {
            loadData();
            log.debug("Loaded SRM bootstrap data");
        } else
            log.debug("Database already populated with test data");
    }

    private void loadData() {
        //build a temporary POJO from Student, Teacher and Guardian classes and add (inject) to each respective service
        //three students, two teachers and two guardians

        //contact details
        ContactDetail teacher1Contact = ContactDetail.builder().email("teacher1@school.com").phoneNumber("9847324").build();
        ContactDetail teacher2Contact = ContactDetail.builder().email("teacher2@school.com").phoneNumber("4023307").build();
        contactDetailService.save(teacher1Contact);
        contactDetailService.save(teacher2Contact);

        ContactDetail guardianContactDetail1 = ContactDetail.builder().phoneNumber("3479324732").email("guardian1@email.com").build();
        contactDetailService.save(guardianContactDetail1);
        ContactDetail guardianContactDetail2 = ContactDetail.builder().phoneNumber("02374320427").email("guardian2@email.com").build();
        contactDetailService.save(guardianContactDetail2);
        System.out.println("Contact details loaded to DB");

        //addresses
        Address address1 = Address.builder().firstLine("88 Penine Way").secondLine("Farnborough").postcode("CHG9475JF").build();
        addressService.save(address1);
        Address address2 = Address.builder().firstLine("7B Gossfer Drive").secondLine("Racoon City").postcode("ZJGKF97657DD").build();
        addressService.save(address2);
        System.out.println("Addresses loaded to DB");

        Student student1 = Student.builder().firstName("John").lastName("Smith").build();
        Student student2 = Student.builder().firstName("Elizabeth").lastName("Jones").build();
        Student student3 = Student.builder().firstName("Helen").lastName("Jones").build();

        TeacherUser teacher1 = teacherUserService.findByFirstNameAndLastName("Keith", "Jones");
        teacher1.setContactDetail(teacher1Contact);

        TeacherUser teacher2 = teacherUserService.findByFirstNameAndLastName("Mary", "Manning");
        teacher2.setContactDetail(teacher2Contact);

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
        System.out.println("Guardians re-loaded to DB");

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
        System.out.println("Subjects loaded to DB");

        teacherUserService.save(teacher1);
        teacherUserService.save(teacher2);
        System.out.println("Teachers re-loaded to DB");

        formGroupListService.save(formGroupList1);
        formGroupListService.save(formGroupList2);
        System.out.println("FormGroupList loaded to DB");

        studentService.save(student1);
        studentService.save(student2);
        studentService.save(student3);
        System.out.println("Students loaded to DB");

        System.out.println("================ Finished uploading SRM data to DB ============");
    }
}
