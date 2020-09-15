package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class FormGroupList extends BaseEntity {

    private String groupName;

    @OneToMany(mappedBy = "formGroupList")
    private Set<Student> studentList = new HashSet<>();

    @OneToOne
    private TeacherUser teacher;
}
