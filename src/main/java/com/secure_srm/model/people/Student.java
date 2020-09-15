package com.secure_srm.model.people;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.GuardianUser;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Student extends BaseEntity {

    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    @JoinTable(name = "student_guardian",
            joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "guardian_id"))
    @ManyToMany
    private Set<GuardianUser> guardians = new HashSet<>();

    //personal tutor; no need for cascading
    @OneToOne
    private TeacherUser teacher;


    @JsonIgnore
    @JoinTable(name = "student_subjectlist",
            joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "subjectclasslist_id"))
    @ManyToMany
    private Set<SubjectClassList> subjectClassLists = new HashSet<>();


    @JsonIgnore
    @ManyToOne
    private FormGroupList formGroupList;
}
