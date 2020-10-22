package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class FormGroupList extends BaseEntity implements Comparable<FormGroupList> {
    //this model provides pastoral group related properties (groupName must be unique)

    @Size(min = 1, max = 255)
    private String groupName;

    @OneToMany(mappedBy = "formGroupList")
    @Builder.Default
    private Set<Student> studentList = new HashSet<>();

    @OneToOne
    private TeacherUser teacher;

    //custom comparator (list forms by groupName)
    @Override
    public int compareTo(FormGroupList input) {
        return this.getGroupName().compareTo(input.groupName);
    }
}
