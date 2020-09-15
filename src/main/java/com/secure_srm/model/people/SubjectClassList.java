package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubjectClassList extends BaseEntity {

    private String groupName;

    @ManyToMany(mappedBy = "subjectClassLists")
    private Set<Student> studentList = new HashSet<>();

    @OneToOne
    private TeacherUser teacher;

    @OneToOne
    private Subject subject;        //possible for a teacher to teach one student different subjects (Math / Phys)
}
