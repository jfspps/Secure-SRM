package com.secure_srm.config;

import com.secure_srm.exceptions.CustomAuthenticationFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


//use @Secured annotation to enable authorisation
//@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    //must inject UserDetailsService (Spring Core interface) to enable remember-me (H2 in-memory or persistent DB)
    private final UserDetailsService userDetailsService;

    //inject when using persistent DB
    private final PersistentTokenRepository persistentTokenRepository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler customAuthFailureHandler(){
        return new CustomAuthenticationFailureHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //see 'Pro Spring Security' for HTTP header based req-res authentication (formLogin uses body based req-res)

        //the order of the antMatchers is important
        //note that any Spring Boot /webjars or CSS stylesheets from the project's /resources directory may need to be
        //to the antMatchers() list (in addition to "/", "/welcome" etc.) as "/webjars/**" and "/resources/**"

        //note, the ROLE_ is automatically prepended to Role here, under SDjpa
        http.authorizeRequests()
                //set pages which do not require authentication
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/", "/welcome", "/css/**").permitAll()
                //override the default login page (see controller)
                .and().formLogin()
                    .loginPage("/login").permitAll().failureUrl("/login-error")
                .and().httpBasic()
                .and().logout()
                    .logoutSuccessUrl("/welcome").permitAll()
                //enable CSRF protection for all except h2-console (should also be ignored for RESTful APIs, if applicable)
                //see POST Spring MVC mock tests for use of with(csrf())
                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and()
                .rememberMe()

                //database-persistent remember-me
                    .tokenRepository(persistentTokenRepository)
                    .userDetailsService(userDetailsService)
                    .tokenValiditySeconds(3600)

                //maximum of one session per user
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).maximumSessions(1);

        // needed to access H2-console with Spring Security (use /console instead of /h2-console)
        // can be commented out without affecting above requests
        // see also (https://springframework.guru/using-the-h2-database-console-in-spring-boot-with-spring-security/)
        http.headers().frameOptions().sameOrigin();

        //ensures all data streams are HTTPS based (will require certification on deployment)
//        http.requiresChannel().anyRequest().requiresSecure();
    }

    // required for AWS
    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver =
                new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }

    // required for AWS
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());

        // add support for Spring Security dialect
        // https://www.baeldung.com/spring-security-thymeleaf
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }
}
