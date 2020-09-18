// many thanks to https://newcodingera.com/datatables-server-side-processing-using-nodejs-mysql/

// //required specifically for NodeTable
// const TempMysql = require('mysql');

// // Create temp DB connection
// const NodeTableDB = TempMysql.createConnection({
//     host: "localhost",
//     user: "SRM_admin",
//     password: "admin02passWORD&3",
//     database: "SRM"
// });

// module.exports = NodeTableDB;

const mysql = require('mysql2');
const dbConnection = mysql.createPool({
    host: 'localhost',
    user: 'SRM_admin',
    password: 'admin02passWORD&3',
    database: 'SRM'
}).promise();

module.exports = dbConnection;