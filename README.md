# Student Record Management #

## Overview ##

This project is a combination of two project, Student Record Management [SRM](https://github.com/jfspps/SRM-Spring) and [Web-login](https://github.com/jfspps/Spring_weblogin).

Student record management is a Spring based academic database which stores and processes student academic data. Web-login provides the Spring Security frontend, with authentication and authorisation functionality.

## SRM project overview ##

The model entities are saved to an in-memory H2 database or in a persistent MySQL database. See [application.properties](/src/main/resources/application.properties) for more info.

## SRM web-login overview ##

Here is summary of the main classes pertaining to authentication and authorisation:

[/com/secure_srm/bootstrap](/src/main/java/com/secure_srm/bootstrap/security)

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

[/com/secure_srm/services/springDataJPA/TestRecordAuthenticationManager](/src/main/java/com/secure_srm/services/springDataJPA/TestRecordAuthenticationManager.java)

Provides userIdIsMatched() which is used in custom annotations to grant access to entities based on the User's ID. For example, a guardian is granted access to a student's data if they have the valid ID (same family etc.).

[/com/secure_srm/services/springDataJPA/TestRecordSDjpaService](/src/main/java/com/secure_srm/services/springDataJPA/TestRecordSDjpaService.java)

Defines (overrides) Spring Data JPA methods pertaining to the [TestRecord service](/src/main/java/com/secure_srm/services/TestRecordService.java) (currently, CRUD ops).

[/com/secure_srm/web](/src/main/java/com/secure_srm/web)

This package contains controller classes which handle all client queries (GET and POST). Custom annotations are also defined here.

## The /resources directory ##

[/scripts](/src/main/resources/scripts)

Contains SQL scripts for MySQL credentials and Secure-SRM entities.

[/static](/src/main/resources/static) and [/templates](/src/main/resources/templates)

Contains the CSS, HTTP custom response Thymeleaf templates, and remaining front-end for Secure-SRM.

The file [messages.properties](/src/main/resources/messages.properties) handles front-end user feedback messages related to form feedback. the file [schema.sql](/src/main/resources/schema.sql) is recognised by Spring Boot and establishes persistent, database storage of user sessions.


