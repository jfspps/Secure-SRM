package com.secure_srm.model.security;

//Multi-tenancy: each AdminUser is part of a group of AdminUsers, all of whom access one User account (i.e. one User object,
//in which the credentials and account status are stored)

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
@Table(name = "Administrators")
public class AdminUser extends BaseEntity implements Comparable<AdminUser>{

    //Hibernate uses snake case by default so the name argument is somewhat redundant here
    @Size(min = 1, max = 255)
    private String firstName;

    @Size(min = 1, max = 255)
    private String lastName;

    @OneToMany(mappedBy = "adminUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    Set<User> users = new HashSet<>();

    @OneToOne
    private ContactDetail contactDetail;

    //custom comparator (list adminUsers by lastName and then firstName)
    @Override
    public int compareTo(AdminUser input) {
        String bothNames = this.lastName + ' ' + this.firstName;
        String inputBothNames = input.lastName + ' ' + input.firstName;
        return bothNames.compareTo(inputBothNames);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (obj instanceof AdminUser){
            AdminUser adminUser = (AdminUser) obj;
            String adminDetails = adminUser.firstName + adminUser.lastName + adminUser.contactDetail.toString();
            String thisAdminDetails = this.firstName + this.lastName + this.contactDetail.toString();

            return thisAdminDetails.equals(adminDetails);
        }
        return false;
    }
}
