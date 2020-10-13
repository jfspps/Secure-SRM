package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class StudentResult extends BaseEntity {

    @OneToOne
    private Student student;

    @OneToOne
    private TeacherUser teacher;        //teacher markers not setters;
    // allow for different teachers to share the same assignment

    @ManyToOne
    private StudentTask studentTask;

    private String score;           //allow for numerical or letter based results
    private String comments;
}
