package com.secure_srm.model.security;

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//USER <--> ROLE <--> AUTHORITY

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Authority extends BaseEntity {

    @Builder.Default
    private String permission = "";

    @ManyToMany(mappedBy = "authorities")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}