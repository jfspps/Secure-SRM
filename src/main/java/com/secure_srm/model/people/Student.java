package com.secure_srm.model.people;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Student extends BaseEntity implements Comparable<Student>{

    //Hibernate uses snake case by default so the name argument is somewhat redundant here
    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @Builder.Default
    private String middleNames = "";

    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    @JoinTable(name = "student_guardian",
            joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "guardian_id"))
    @ManyToMany
    @Builder.Default
    private Set<GuardianUser> guardians = new HashSet<>();

    //personal tutor; no need for cascading
    @OneToOne
    private TeacherUser teacher;


    @JsonIgnore
    @JoinTable(name = "student_subjectlist",
            joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "subjectclasslist_id"))
    @ManyToMany
    @Builder.Default
    private Set<SubjectClassList> subjectClassLists = new HashSet<>();

    @JsonIgnore
    @ManyToOne
    private FormGroupList formGroupList;

    @OneToOne
    private ContactDetail contactDetail;

    private boolean anonymised = false;

    //custom comparator (list students by lastName and then firstName)
    @Override
    public int compareTo(Student input) {
        String bothNames = this.lastName + ' ' + this.firstName;
        String inputBothNames = input.lastName + ' ' + input.firstName;
        return bothNames.compareTo(inputBothNames);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (obj instanceof Student){
            Student passed = (Student) obj;
            String passedProps = passed.firstName + passed.middleNames + passed.lastName;

            String thisStudent = this.firstName + this.middleNames + this.lastName;
            return (thisStudent.equals(passedProps));
        }
        return false;
    }
}
