package com.secure_srm.model.academic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subjects")
public class Subject extends BaseEntity {

    @Size(min = 2, max = 255)
    @Column(name = "subject_name")
    private String subjectName;

    //"subjects" refers to Teacher.subjects local variable (Set)
    @ManyToMany(mappedBy = "subjects")
    //the @JsonIgnore added to prevent Spring from creating infinitely long JSONs
    //(https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed)
    @JsonIgnore
    private Set<TeacherUser> teachers = new HashSet<>();
}
