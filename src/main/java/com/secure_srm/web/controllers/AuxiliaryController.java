package com.secure_srm.web.controllers;

import com.secure_srm.model.security.User;
import com.secure_srm.services.securityServices.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

//AuxiliaryController handles commonly used methods across all controllers
@Slf4j
@RequiredArgsConstructor
@Controller
public class AuxiliaryController {

    private final UserService userService;

    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public Boolean teachesASubject(){
        if (userService.findByUsername(getUsername()) != null){
            User currentUser = userService.findByUsername(getUsername());
            return currentUser.getTeacherUser() != null && !currentUser.getTeacherUser().getSubjects().isEmpty();
        }
        return false;
    }

}
