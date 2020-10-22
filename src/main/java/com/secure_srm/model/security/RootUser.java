package com.secure_srm.model.security;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.ContactDetail;
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
@Table(name = "SRM_root_users")
public class RootUser extends BaseEntity implements Comparable<RootUser>{

    //Hibernate uses snake case by default so the name argument is somewhat redundant here
    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @OneToMany(mappedBy = "rootUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<User> users = new HashSet<>();

    @OneToOne
    private ContactDetail contactDetail;

    //custom comparator (list rootUsers by lastName and then firstName)
    @Override
    public int compareTo(RootUser input) {
        String bothNames = this.lastName + ' ' + this.firstName;
        String inputBothNames = input.lastName + ' ' + input.firstName;
        return bothNames.compareTo(inputBothNames);
    }
}
