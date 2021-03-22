package com.secure_srm.model.security;

//Multi-tenancy: each AdminUser is part of a group of AdminUsers, all of whom access one User account (i.e. one User object,
//in which the credentials and account status are stored)

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.ContactDetail;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Teachers")
public class TeacherUser extends BaseEntity implements Comparable<TeacherUser>{

    //Hibernate uses snake case by default so the name argument is somewhat redundant here
    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @OneToMany(mappedBy = "teacherUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    Set<User> users = new HashSet<>();

    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    @JoinTable(name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "subject_id"))
    @ManyToMany
    @Builder.Default
    private Set<Subject> subjects = new HashSet<>();

    @Builder.Default
    private String department = "";

    @OneToOne
    private ContactDetail contactDetail;

    //custom comparator (list teachers by lastName and then firstName)
    @Override
    public int compareTo(TeacherUser input) {
        String bothNames = this.lastName + ' ' + this.firstName;
        String inputBothNames = input.lastName + ' ' + input.firstName;
        return bothNames.compareTo(inputBothNames);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (obj instanceof TeacherUser){
            TeacherUser teacherUser = (TeacherUser) obj;
            String teacherDetails = teacherUser.firstName + teacherUser.lastName + teacherUser.contactDetail.toString();
            String thisTeacherDetails = this.firstName + this.lastName + this.contactDetail.toString();

            return thisTeacherDetails.equals(teacherDetails);
        }
        return false;
    }
}
