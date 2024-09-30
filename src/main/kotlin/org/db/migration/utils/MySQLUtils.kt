package org.db.migration.utils

import org.db.migration.DB2DDL
import org.db.migration.DB2ToMySQLConverter

object MySQLUtils {
    fun convertDB2ToMySQL(ddlStatements: List<DB2DDL>): List<String> {
        return DB2ToMySQLConverter.convertDB2ToMySQL(ddlStatements)
    }
}
