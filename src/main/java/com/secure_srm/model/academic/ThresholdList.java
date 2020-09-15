package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ThresholdList extends BaseEntity {

    @JoinTable(name = "thresholdList_threshold",
            joinColumns = @JoinColumn(name = "thresholdlist_id"), inverseJoinColumns = @JoinColumn(name = "threshold_id"))
    @ManyToMany
    private Set<Threshold> thresholds = new HashSet<>();
}
