package com.secure_srm.model.security;

//Multi-tenancy: each AdminUser is part of a group of AdminUsers, all of whom access one User account (i.e. one User object,
//in which the credentials and account status are stored)

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class AdminUser extends BaseEntity {

    //Hibernate uses snake case by default so the name argument is somewhat redundant here
    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @OneToMany(mappedBy = "adminUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<User> users;
}
