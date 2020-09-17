package com.secure_srm;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    //no code required
}

/*
    The purpose of SpringSecurityInitializer is to initialise Spring Security and load springSecurityFilterChain:

    Key filters in the chain are (in the order)

    SecurityContextPersistenceFilter (restores Authentication from JSESSIONID)
    UsernamePasswordAuthenticationFilter (performs authentication)
    ExceptionTranslationFilter (catch security exceptions from FilterSecurityInterceptor)
    FilterSecurityInterceptor (may throw authentication and authorization exceptions)

    https://stackoverflow.com/questions/41480102/how-spring-security-filter-chain-works
*/