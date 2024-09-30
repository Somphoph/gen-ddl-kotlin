package org.db.migration

import org.db.migration.utils.DB2Utils
import org.db.migration.utils.MySQLUtils
import java.io.File

fun main() {
    val db2ConnStr = "jdbc:db2://10.9.225.239:50018/cbcd:user=ewstadba;password=ewstadba;"
    val schema = "ewstadba";
    // Retrieve DB2 DDL
    val db2DDLStatements = DB2Utils.getDB2DDL(db2ConnStr, schema)
    if (db2DDLStatements.isEmpty()) {
        println("Error retrieving DB2 DDL")
        return
    }

    // Convert DB2 DDL to MySQL DDL
    val mysqlDDLStatements = MySQLUtils.convertDB2ToMySQL(db2DDLStatements)

    // Write MySQL DDL statements to file
    val outputFile = File("mysql_ddl.sql")
    outputFile.bufferedWriter().use { out ->
        mysqlDDLStatements.forEach { statement ->
            out.write(statement)
            out.newLine()
            out.newLine()
        }
    }

    println("MySQL DDL statements written to ${outputFile.absolutePath}")
}
