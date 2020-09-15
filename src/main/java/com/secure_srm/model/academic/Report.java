package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Report extends BaseEntity {

    @OneToOne
    private Student student;

    @OneToOne
    private TeacherUser teacher;

    @OneToOne
    private Subject subject;

    private String comments;
}
