package org.db.migration
import java.sql.Connection
import java.sql.DriverManager

data class DB2DDL(
    val tableName: String,
    val columns: List<Column>,
    val primaryKeys: List<String>,
    val tableComment: String?,
    val columnComments: Map<String, String>
)

data class Column(
    val name: String,
    val type: String,
    val length: Int?,
    val isNullable: Boolean
)

object DB2DDLRetriever {
    fun getDB2DDL(connStr: String, schema: String): List<DB2DDL> {
        val ddlStatements = mutableListOf<DB2DDL>()

        val db2Connection = DriverManager.getConnection(connStr)
        val tablesQuery = "SELECT TABNAME FROM SYSCAT.TABLES WHERE TYPE='T' AND TABSCHEMA=UPPER('$schema')"
        val statement = db2Connection.createStatement()
        val resultSet = statement.executeQuery(tablesQuery)

        while (resultSet.next()) {
            val tableName = resultSet.getString("TABNAME")
            val columns = getTableColumns(db2Connection, tableName)
            val primaryKeys = getTablePrimaryKeys(db2Connection, tableName)
            val tableComment = getTableComment(db2Connection, tableName)
            val columnComments = getColumnComments(db2Connection, tableName)
            ddlStatements.add(DB2DDL(tableName, columns, primaryKeys, tableComment, columnComments))
        }

        resultSet.close()
        statement.close()
        db2Connection.close()

        return ddlStatements
    }

    private fun getTableColumns(connection: Connection, tableName: String): List<Column> {
        val query = """
            SELECT COLNAME, TYPENAME, LENGTH, NULLS 
            FROM SYSCAT.COLUMNS 
            WHERE TABNAME = ?
        """.trimIndent()
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, tableName)
        val resultSet = preparedStatement.executeQuery()
        val columns = mutableListOf<Column>()
        while (resultSet.next()) {
            val name = resultSet.getString("COLNAME")
            val type = resultSet.getString("TYPENAME")
            val length = resultSet.getInt("LENGTH").takeIf { it > 0 }
            val isNullable = resultSet.getString("NULLS") == "Y"
            columns.add(Column(name, type, length, isNullable))
        }
        resultSet.close()
        preparedStatement.close()
        return columns
    }

    private fun getTablePrimaryKeys(connection: Connection, tableName: String): List<String> {
        val query = """
            SELECT COLNAME 
            FROM SYSCAT.KEYCOLUSE 
            WHERE TABNAME = ?
        """.trimIndent()
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, tableName)
        val resultSet = preparedStatement.executeQuery()
        val primaryKeys = mutableListOf<String>()
        while (resultSet.next()) {
            primaryKeys.add(resultSet.getString("COLNAME"))
        }
        resultSet.close()
        preparedStatement.close()
        return primaryKeys
    }

    private fun getTableComment(connection: Connection, tableName: String): String? {
        val query = """
            SELECT REMARKS 
            FROM SYSCAT.TABLES 
            WHERE TABNAME = ? 
              AND REMARKS IS NOT NULL
        """.trimIndent()
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, tableName)
        val resultSet = preparedStatement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getString("REMARKS")
        } else {
            null
        }.also {
            resultSet.close()
            preparedStatement.close()
        }
    }

    private fun getColumnComments(connection: Connection, tableName: String): Map<String, String> {
        val query = """
            SELECT COLNAME, REMARKS 
            FROM SYSCAT.COLUMNS 
            WHERE TABNAME = ? 
              AND REMARKS IS NOT NULL
        """.trimIndent()
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, tableName)
        val resultSet = preparedStatement.executeQuery()
        val columnComments = mutableMapOf<String, String>()
        while (resultSet.next()) {
            columnComments[resultSet.getString("COLNAME")] = resultSet.getString("REMARKS")
        }
        resultSet.close()
        preparedStatement.close()
        return columnComments
    }
}
