package com.secure_srm.model.academic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ThresholdList extends BaseEntity implements Comparable<ThresholdList>{

    @JoinTable(name = "thresholdList_threshold",
            joinColumns = @JoinColumn(name = "thresholdlist_id"), inverseJoinColumns = @JoinColumn(name = "threshold_id"))
    @ManyToMany
    @Builder.Default
    private Set<Threshold> thresholds = new HashSet<>();

    @Builder.Default
    private String uniqueID = "";

    //custom comparator (list by uniqueId)
    @Override
    public int compareTo(ThresholdList input) {
        String thisID = this.uniqueID;
        String inputID = input.uniqueID;
        return thisID.compareTo(inputID);
    }

    @OneToOne
    private TeacherUser uploader;

    @JsonIgnore
    @JoinTable(name = "studentTask_thresholdList",
            joinColumns = @JoinColumn(name = "thresholdList_id"), inverseJoinColumns = @JoinColumn(name = "studentTask_id"))
    @ManyToMany
    @Builder.Default
    private Set<StudentTask> studentTaskSet = new HashSet<>();
}
