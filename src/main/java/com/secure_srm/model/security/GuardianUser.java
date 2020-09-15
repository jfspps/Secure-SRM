package com.secure_srm.model.security;

//Multi-tenancy: each AdminUser is part of a group of AdminUsers, all of whom access one User account (i.e. one User object,
//in which the credentials and account status are stored)

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.Address;
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
public class GuardianUser extends BaseEntity {

    @Size(min = 1, max = 255)
    private String guardianUserName;

    @OneToMany(mappedBy = "guardianUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<User> users;

    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    @ManyToOne
    private Address address;

    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    //"guardians" refers to the Set<Guardian> fro Student
    @ManyToMany(mappedBy = "guardians")
    private Set<Student> students = new HashSet<>();
}
