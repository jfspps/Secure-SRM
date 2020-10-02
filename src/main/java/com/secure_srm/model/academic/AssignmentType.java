package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AssignmentType extends BaseEntity {
    //this would be uniformly set by the school admin

    @Size(min = 1, max = 255)
    private String description;

    @OneToMany
    private Set<StudentResult> studentResults = new HashSet<>();
}
