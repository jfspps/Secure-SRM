package com.secure_srm.model.academic;

import com.secure_srm.model.BaseEntity;
import com.secure_srm.model.security.TeacherUser;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Threshold extends BaseEntity implements Comparable<Threshold>{
    //maps a numerical score with a A*/B-/D+/MERIT/DISTINCTION etc...

    private int numerical;
    private String alphabetical;

    private String uniqueId;

    @ManyToMany(mappedBy = "thresholds")
    private Set<ThresholdList> thresholdLists = new HashSet<>();

    //custom comparator (list by uniqueId)
    @Override
    public int compareTo(Threshold input) {
        String thisID = this.uniqueId;
        String inputID = input.uniqueId;
        return thisID.compareTo(inputID);
    }

    @OneToOne
    private TeacherUser uploader;
}
