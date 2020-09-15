# Student Record Management #

## Overview ##

This project is a combination of two project, Student Record Management [SRM](https://github.com/jfspps/SRM-Spring) and [Web-login](https://github.com/jfspps/Spring_weblogin).

Student record management is a Spring based academic database which stores and processes student academic data. Web-login provides the Spring Security frontend, with authentication and authorisation functionality.

## SRM project overview ##

The model entities are saved to an in-memory H2 database or in a persistent MySQL database. See [application.properties](/src/main/resources/application.properties) for more info.

## SRM web-login overview ##

Here is summary of the main classes pertaining to authentication and authorisation: