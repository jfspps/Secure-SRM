package com.secure_srm.model.security;

//Multi-tenancy: each AdminUser is part of a group of AdminUsers, all of whom access one User account (i.e. one User object,
//in which the credentials and account status are stored)

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class TeacherUser extends BaseEntity {

    @Size(min = 1, max = 255)
    private String teacherUserName;

    @OneToMany(mappedBy = "teacherUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<User> users;
}
