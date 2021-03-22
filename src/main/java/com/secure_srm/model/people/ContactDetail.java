package com.secure_srm.model.people;

import com.secure_srm.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contact_detail")
public class ContactDetail extends BaseEntity {

    @Builder.Default
    private String email = "";

    @Builder.Default
    private String phoneNumber = "";

    @Override
    public String toString() {
        return "ContactDetail{" +
                "email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
