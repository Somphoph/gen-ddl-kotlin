package org.db.migration.utils

import org.db.migration.DB2DDL
import org.db.migration.DB2DDLRetriever

object DB2Utils {
    fun getDB2DDL(connStr: String, schema: String): List<DB2DDL> {
        return DB2DDLRetriever.getDB2DDL(connStr, schema)
    }
}
