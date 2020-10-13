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
public class AssignmentType extends BaseEntity implements Comparable<AssignmentType>{
    //this would be uniformly set by the school admin

    @Size(min = 1, max = 255)
    private String description;

    @OneToMany
    private Set<StudentResult> studentResults = new HashSet<>();

    //custom comparator (list assignment types alphabetically)
    @Override
    public int compareTo(AssignmentType input) {
        String thisType = this.getDescription();
        String inputType = input.getDescription();
        return thisType.compareTo(inputType);
    }
}
