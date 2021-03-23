[![CircleCI](https://circleci.com/gh/jfspps/Secure-SRM.svg?style=svg)](https://circleci.com/gh/jfspps/Secure-SRM)

# Secure Student Record Management (Secure-SRM) #

## Overview ##

Secure Student-record-management is a Spring based academic database which stores and processes student academic data. Authentication and authorisation is provided by [Web-login](https://github.com/jfspps/Spring_weblogin), a Spring Security frontend . This project is the secured version of Student Record Management [SRM](https://github.com/jfspps/SRM-Spring).

All model entities are saved to an in-memory H2 database or a persistent MySQL database. See [application.properties](/src/main/resources/application.properties) for more info. The localhost port number has been set to 5000 to match that of the EC2 instance on AWS.

## General status of Secure-SRM ##

At present, Secure-SRM can store and retrieve 

+ personnel data (teachers, admin and guardians, each with Web-login credentials, and student records) 
+ academic data (student tasks, results, report, threshold lists and thresholds, to name a few) 
+ class lists (form group lists and subject class lists)
  
Various account settings can be changed by the user (email address, phone number and web-login password). 

Not all aspects of Secure-SRM are available to all users. For example, teachers cannot build new assignment types or web-login user, while school administrators cannot upload student results or reports. Teachers have ownership of certain entities (thresholds, student tasks, reports) for which other teachers have read-only access.

Not all entities can be removed and such an approach is applied to allow schools and colleges to review numerical data coupled to task data. Personal information of past students, their guardians and teachers can be removed.

+ Deletion or anonymity of select entities currently available:
  + Removal of personal details (anonymise) of former teachers. Replaced them with generic fields.
  + Removal of personal details of students and subsequent update of guardian personal details.
  + Complete removal of guardian records and updating of student records
  
## Future work for Secure-SRM ##

+ Implementation of DELETE operations for select entities. Under development:
  + Removal of grade thresholds and threshold lists, without affecting student raw results
  + Removal of student reports

+ Development of school administrators', teachers' and parents' portals which provide streamlined data entry, which includes uploading multiple records at the same time (e.g. student task, thresholds and student results) in way which resembles the often used spreadsheet form. A more efficient interface which allows for the input of multiple entries (e.g. uploading of a whole class worth of results) with established frontends, such as Angular, are also part of the future plan.

+ Export of academic results as a PDF

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
