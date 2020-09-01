package com.springsecurity.weblogin.model.security;

import com.springsecurity.weblogin.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

//USER <--> ROLE <--> AUTHORITY

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class User extends BaseEntity {

    @Size(min = 1, max = 255)
    private String username;

    @Size(min = 8, max = 255)
    private String password;

    //Transient are non-persistent
    @Transient
    private Set<Authority> authorities;

    //Singular (Lombok) builds a singular Set with one Authority in authorities, called "authority"
    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private Set<Role> roles;

    public Set<Authority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    //adding other Spring's UserDetails interface properties
    //override the null value with true
    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;
}
