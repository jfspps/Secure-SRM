package com.secure_srm.model.security;

import com.secure_srm.model.BaseEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class LoginFailure extends BaseEntity {

    @ManyToOne
    private User user;

    @Builder.Default
    private String sourceIP = "";

    @Builder.Default
    private String usernameEntered = "";
}