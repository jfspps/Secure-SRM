package com.secure_srm.model.security;

//Role is intermediate of Users and Authorities: USER <--> ROLE <--> AUTHORITY

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

//USER <--> ROLE <--> AUTHORITY

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class Role extends BaseEntity {

    @Size(min = 1, max = 255)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "role_authority",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private Set<Authority> authorities = new HashSet<>();
}
