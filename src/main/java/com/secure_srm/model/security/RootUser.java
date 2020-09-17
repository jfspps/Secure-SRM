package com.secure_srm.model.security;

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
public class RootUser extends BaseEntity {

    //Hibernate uses snake case by default so the name argument is somewhat redundant here
    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @OneToMany(mappedBy = "rootUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<User> users;
}
