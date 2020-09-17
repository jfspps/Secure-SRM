package com.secure_srm.services.springDataJPA.security;

import com.secure_srm.model.security.Authority;
import com.secure_srm.model.security.Role;
import com.secure_srm.model.security.RootUser;
import com.secure_srm.model.security.User;
import com.secure_srm.repositories.security.RootUserRepository;
import com.secure_srm.repositories.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RootUserSDjpaServiceTest {

    @Mock
    RootUserRepository rootUserRepositoryTEST;

    @Mock
    UserRepository userRepositoryTEST;

    @InjectMocks
    RootUserSDjpaService rootUserSDjpaService;

    //set User and RootUser credentials
    private final String username = "root";
    private final String password = "rootPassword";

    //set Authority and Role
    Authority authority = Authority.builder().permission("allGame").build();
    Role role = Role.builder().roleName("ROOT").authority(authority).build();

    //instantiate User and their associated RootUsers
    User user;
    RootUser rootUser;

    private final String firstName = "Jim";
    private final String lastName = "Bob";

    Set<User> rootUser_userSet = new HashSet<>();
    List<User> users = new ArrayList<>();
    List<RootUser> rootUsers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        //instantiate and establish mapping between User and RootUser
        user = User.builder().username(username).password(password).role(role).build();
        rootUser_userSet.add(user);
        rootUser = RootUser.builder().firstName(firstName).lastName(lastName).users(rootUser_userSet).build();
        user.setRootUser(rootUser);

        //for SDjpa
        users.add(user);
        rootUsers.add(rootUser);
    }

    @Test
    void save() {
        when(rootUserRepositoryTEST.save(any())).thenReturn(rootUser);

        RootUser saved = rootUserRepositoryTEST.save(RootUser.builder().build());
        assertNotNull(saved);

        verify(rootUserRepositoryTEST, times(1)).save(any());
    }

    @Test
    void findById() {
        when(rootUserRepositoryTEST.findById(anyLong())).thenReturn(Optional.of(rootUser));

        RootUser found = rootUserRepositoryTEST.findById(12L).orElse(null);
        assertNotNull(found);

        verify(rootUserRepositoryTEST, times(1)).findById(anyLong());
    }

    @Test
    void findAll(){
        when(rootUserRepositoryTEST.findAll()).thenReturn(rootUsers);

        assertEquals(1, rootUserRepositoryTEST.findAll().size());

        verify(rootUserRepositoryTEST, times(1)).findAll();
    }
}