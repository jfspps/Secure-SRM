package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

//generic class list

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentList extends BaseEntity {

    @Builder.Default
    private String groupName = "";

    @Builder.Default
    private Set<Student> students = new HashSet<>();
}
