package org.db.migration.utils

object MySQLTemplate {
    val CREATE_TABLE_TEMPLATE = """
        CREATE TABLE {table_name} (
            {columns}
            {primary_keys}
        );
    """.trimIndent()

    const val COLUMN_TEMPLATE = "{column_name} {data_type}{nullable} {comment}"

    const val PRIMARY_KEY_TEMPLATE = "PRIMARY KEY ({primary_keys})"

    val COMMENT_TEMPLATE = """
        COMMENT ON TABLE {table_name} IS '{table_comment}';
        {column_comments}
    """.trimIndent()

    const val COLUMN_COMMENT_TEMPLATE = "COMMENT '{comment}'"
}