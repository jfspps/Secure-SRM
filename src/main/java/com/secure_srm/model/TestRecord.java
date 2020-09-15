package com.secure_srm.model;

//this class represents a sample database POJO to test user authentication and authorisation

import com.secure_srm.model.security.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRecord extends BaseEntity {

    @NotEmpty()
    private String recordName;

    @ManyToOne
    private User user;

    public TestRecord(String recordName) {
            this.recordName = recordName;
    }
}
