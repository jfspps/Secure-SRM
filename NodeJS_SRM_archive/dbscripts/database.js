const mysql = require('mysql2');
const dbConnection = mysql.createPool({
    host: 'localhost',
    user: 'SRM_admin',
    password: 'admin02passWORD&3',
    database: 'SRM'
}).promise();

module.exports = dbConnection;

//MySQL script for the table SRM.users
/*
CREATE TABLE `users` (
 `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
 `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
 `email` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
 `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/

//A collation is a set of rules that defines how to compare and sort character strings (in this case passwords and usernames)
//different UTF standards assume different character matching (from different languages)
// utf8mb4_general_ci is less comprehensive albeit faster than utf8mb4_unicode_ci