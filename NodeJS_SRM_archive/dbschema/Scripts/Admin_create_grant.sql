-- this may need removing on first run since an error is given if DROP fails
DROP USER 'SRM_admin'@'localhost';

USE SRM;
-- CREATE USER username IDENTIFIED BY password
-- MEDIUM policy adds the conditions that passwords must contain at least 1 numeric character, 1 lowercase character, 1 uppercase character, and 1 special (nonalphanumeric) character.

-- add the SRM-server admin (change the password as desired)
-- it is strongly advised to specifiy the host, either locally with @localhost or remotely, with @ip_address_of_server

CREATE USER 'SRM_admin'@'localhost' IDENTIFIED BY 'admin02passWORD&3';

-- grant all privileges to the administrator, including the creation of new users
-- when using Nodejs, ALTER is needed because caching_sha2_password from MySQL 8.0 may not be implemented yet
-- this script should be run as 'root'

GRANT ALL PRIVILEGES ON SRM.* TO 'SRM_admin'@'localhost' WITH GRANT OPTION;
ALTER USER 'SRM_admin'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin02passWORD&3';
FLUSH PRIVILEGES;