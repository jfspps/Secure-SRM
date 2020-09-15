package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Threshold extends BaseEntity {
    //maps a numerical score with a A*/B-/D+/MERIT/DISTINCTION etc...

    private int numerical;
    private String alphabetical;

    @ManyToMany(mappedBy = "thresholds")
    private Set<ThresholdList> thresholdLists = new HashSet<>();
}
