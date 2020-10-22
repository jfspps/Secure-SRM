package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Address extends BaseEntity {

    @Builder.Default
    private String firstLine = "";

    @Builder.Default
    private String secondLine = "";

    @Builder.Default
    private String postcode = "";
}
