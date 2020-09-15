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

    private String firstLine;

    private String secondLine;

    private String postcode;
}
