package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
//POJO for the framework to a piece of HW, coursework, exams, quizzes, projects etc...
//student specific results are handled by StudentResult
public class StudentTask extends BaseEntity {

    @Size(min = 1, max = 255)
    private String title;

    @Builder.Default
    private String maxScore = "";        //allows for letter grades or no score at all

    @Builder.Default
    private boolean contributor = true;     //purpose is to state whether this contributes to an overall end-of-term/...score

    @OneToOne
    private TeacherUser teacherUploader;

    @OneToOne
    @NotNull
    private Subject subject;

    //allow for extension, offloading responsibility from StudentWork
    @ManyToOne
    private AssignmentType assignmentType;

    @OneToMany(mappedBy = "studentTask")
    @Builder.Default
    private Set<StudentResult> studentResults = new HashSet<>();

    //todo: add ThresholdList property
}
