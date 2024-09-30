package org.db.migration

import org.db.migration.utils.MySQLTemplate

object DB2ToMySQLConverter {
    fun convertDB2ToMySQL(ddlStatements: List<DB2DDL>): List<String> {
        return ddlStatements.map { ddl ->
            val columns = ddl.columns.joinToString(",\n\t") {
                val dataTypeWithLength = convertDataType(it.type, it.length)
                MySQLTemplate.COLUMN_TEMPLATE
                    .replace("{column_name}", it.name)
                    .replace("{data_type}", dataTypeWithLength)
                    .replace("{nullable}", if (it.isNullable) "" else " NOT NULL")
                    .replace("{comment}", getComment(ddl.columnComments[it.name] ?: ""))
            }


            val primaryKeys = if (ddl.primaryKeys.isNotEmpty()) {
                MySQLTemplate.PRIMARY_KEY_TEMPLATE.replace("{primary_keys}", ddl.primaryKeys.joinToString(", "))
            } else {
                ""
            }

            val createTable = MySQLTemplate.CREATE_TABLE_TEMPLATE
                .replace("{table_name}", ddl.tableName)
                .replace("{columns}", columns)
                .replace("{primary_keys}", if (primaryKeys.isNotEmpty()) ",\n$primaryKeys" else "")

            listOf(createTable).joinToString("\n")
        }
    }

    private fun convertDataType(db2Type: String, length: Int?): String {
        return when (db2Type) {
            "VARCHAR", "CHAR" -> "$db2Type($length)"
            "INTEGER" -> "INT"
            "CLOB" -> "$db2Type($length)"
            else -> db2Type
        }
    }

    private fun getComment(comment: String?): String {
        val result = if (!comment.isNullOrEmpty()) {
            MySQLTemplate.COLUMN_COMMENT_TEMPLATE.replace("{comment}", comment)
        } else {
            ""
        }
        return result
    }

}
