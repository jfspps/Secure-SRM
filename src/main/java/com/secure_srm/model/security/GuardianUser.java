package com.secure_srm.model.security;

//Multi-tenancy: each AdminUser is part of a group of AdminUsers, all of whom access one User account (i.e. one User object,
//in which the credentials and account status are stored)

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.Address;
import com.secure_srm.model.people.ContactDetail;
import com.secure_srm.model.people.Student;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Guardians")
public class GuardianUser extends BaseEntity implements Comparable<GuardianUser>{

    //Hibernate uses snake case by default so the name argument is somewhat redundant here
    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @OneToMany(mappedBy = "guardianUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    Set<User> users = new HashSet<>();

    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    @ManyToOne
    private Address address;

    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    //"guardians" refers to the Set<Guardian> from Student
    @ManyToMany(mappedBy = "guardians")
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    @OneToOne
    private ContactDetail contactDetail;

    //custom comparator (list guardians by lastName and then firstName)
    @Override
    public int compareTo(GuardianUser input) {
        String bothNames = this.lastName + ' ' + this.firstName;
        String inputBothNames = input.lastName + ' ' + input.firstName;
        return bothNames.compareTo(inputBothNames);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        GuardianUser guardianUser = (GuardianUser) obj;
        String guardianDetails = guardianUser.firstName + guardianUser.lastName + guardianUser.contactDetail.toString();
        String thisGuardianDetails = this.firstName + this.lastName + this.contactDetail.toString();

        return thisGuardianDetails.equals(guardianDetails);
    }
}
