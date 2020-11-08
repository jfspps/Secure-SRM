# Secure Student Record Management (Secure-SRM) #

## Overview ##

This project is a combination of two projects, Student Record Management [SRM](https://github.com/jfspps/SRM-Spring) and [Web-login](https://github.com/jfspps/Spring_weblogin).

Student record management is a Spring based academic database which stores and processes student academic data. Web-login provides the Spring Security frontend, with authentication and authorisation functionality.

The model entities are saved to an in-memory H2 database or a persistent MySQL database. See [application.properties](/src/main/resources/application.properties) for more info.

## Current (Oct 2020) and future work of Secure-SRM ##

At present, Secure-SRM can store and retrieve personnel data (teachers, admin and guardians, each with Web-login credentials, and student records), academic data (student tasks, results, report, threshold lists and thresholds, to name a few) and finally class lists (form group lists and subject class lists). Various account settings can be changed by the user (email address, phone number and web-login password). Not all aspects of secure-SRM are available to all users. For example, teachers cannot build new assignment types or web-login user, while school administrators cannot upload student results or reports. Teachers have ownership of certain entities (thresholds, student tasks, reports) for which other teachers have read-only access.

The next stage is the implementation of DELTE operations for many entities (handling cascading properties) and the development of school administrators', teachers' and parents' portals which provide streamlined data entry, which includes uploading multiple records at the same time (e.g. student task, thresholds and student results) in way which resembles the often used spreadsheet form. Currently, I am investigating established frontends and interfaces to frontends, including jhipster and angular.

## Web-login specific overview ##

Here is summary of the main classes pertaining to authentication and authorisation:

[/com/secure_srm/SpringSecurityInitializer](/src/main/java/com/secure_srm/SpringSecurityInitializer.java)

The purpose of `SpringSecurityInitializer` is to load springSecurityFilterChain:

__Key filters in the chain are (in order)__
- SecurityContextPersistenceFilter (restores Authentication from JSESSIONID)
- UsernamePasswordAuthenticationFilter (performs authentication)
- ExceptionTranslationFilter (catch security exceptions from FilterSecurityInterceptor)
- FilterSecurityInterceptor (may throw authentication and authorization exceptions)

(source: https://stackoverflow.com/questions/41480102/how-spring-security-filter-chain-works)

[/com/secure_srm/bootstrap](/src/main/java/com/secure_srm/bootstrap/DataLoader_SDjpa.java)

+ `DataLoader_SDjpa` Initialises Roles, Authorities (permissions) and user accounts if the current database is void of user accounts

[/com/secure_srm/config](/src/main/java/com/secure_srm/config)

+ `SecurityConfiguration` Defines password encryption, login, logout, Remember-Me, and session policies
+ `SecurityBeans` Defines other Spring Beans to supplement that provided in `SecurityConfiguration` (currently Login listeners and Remember-Me persistence)

[/com/secure_srm/exceptions](/src/main/java/com/secure_srm/exceptions)

+ `CustomAuthenticationFailureHandler` Previously used to handle invalid login attempts (currently superseded by `SecurityConfiguration`)
+ `NotFoundException` Facilitates custom HTTP 404 not found responses

[/com/secure_srm/listeners](/src/main/java/com/secure_srm/listeners)

Both classes store all login attempts

+ `AuthenticationFailureListener` Defines authentication failure behaviour and user lockouts
+ `AuthenticationSuccessListener` Defines authentication success behaviour

[/com/secure_srm/model/security](/src/main/java/com/secure_srm/model/security)

Spring Security specific entity package (users, authorities, roles, loginSuccess and login failure). All models in Secure-SRM derive from `BaseEntity` and are date-stamped (creation and modification).

For testing/debugging purposes, a testRecord entity (composed of one property: recordName) is provided.

[/com/secure_srm/repositories/security](/src/main/java/com/secure_srm/repositories/security)

Spring Security specific Spring Data JPA interfaces which define what JPA methods are available. Each interface extends `JpaRepository`. Other database daemons can be incorporated but must adhere (minimally) and implement the service interfaces (in [/com/secure_srm/services/securityServices](/src/main/java/com/secure_srm/services/securityServices)) each of which lists the methods required.

[/com/secure_srm/services/securityServices](/src/main/java/com/secure_srm/services/securityServices)

These interfaces are instantiated and then sent to a constructor of a bean, whenever they are required. Generally, one does not call the JPA repository methods directly and instead calls the respective service (which can then return the JPA method, see next point)

[/com/secure_srm/services/springDataJPA/security](/src/main/java/com/secure_srm/services/springDataJPA/security)

Classes which provide `securityServices` with access to the JPA methods. If other databases are required, then one would need to build other classes which implement a `securityServices` class and return other database queries.

[/com/secure_srm/web](/src/main/java/com/secure_srm/web)

This package contains controller classes which handle all client queries (GET and POST). Custom annotations are also defined here.

## The /resources directory ##

[/scripts](/src/main/resources/scripts)

Contains SQL scripts for MySQL credentials and Secure-SRM entities.

[/static](/src/main/resources/static) and [/templates](/src/main/resources/templates)

Contains the CSS, HTTP custom response Thymeleaf templates, and remaining front-end for Secure-SRM.

The file [messages.properties](/src/main/resources/messages.properties) handles front-end user feedback messages related to form feedback. the file [schema.sql](/src/main/resources/schema.sql) is recognised by Spring Boot and establishes persistent, database storage of user sessions.
