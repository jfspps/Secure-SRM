package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubjectClassList extends BaseEntity implements Comparable<SubjectClassList> {
    //this model provides academic class/group related properties (groupName must be unique)

    @Size(min = 1, max = 255)
    private String groupName;

    @ManyToMany(mappedBy = "subjectClassLists")
    @Builder.Default
    private Set<Student> studentList = new HashSet<>();

    @OneToOne
    private TeacherUser teacher;

    @OneToOne
    private Subject subject;        //confirms the subject taught since the same teacher may teach different subjects

    //custom comparator (list forms by groupName)
    @Override
    public int compareTo(SubjectClassList input) {
        return this.getGroupName().compareTo(input.groupName);
    }
}
