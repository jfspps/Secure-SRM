package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
//POJO for HW, coursework, exams, quizzes, projects etc...
public class StudentWork extends BaseEntity {

    private String title;
    private Integer maxScore;        //boxed Integer can be null; allows for letter grades or no score at all
    private boolean contributor;     //purpose is to state whether this contributes to an overall end-of-term/...score

    @OneToOne
    private TeacherUser teacherUploader;

    @OneToOne
    private Subject subject;

    //allow for extension, offloading responsibility from StudentWork
    @ManyToOne
    private AssignmentType assignmentType;

    @OneToMany(mappedBy = "studentWork")
    private Set<StudentResult> studentResults = new HashSet<>();
}
