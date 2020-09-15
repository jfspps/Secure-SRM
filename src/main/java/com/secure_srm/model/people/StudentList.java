package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

//generic class list

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentList extends BaseEntity {

    private String groupName;

    Set<Student> students = new HashSet<>();
}
