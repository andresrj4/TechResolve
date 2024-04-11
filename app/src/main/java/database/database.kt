package com.example.sistema_de_tickets

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

object Database {
    private const val DATABASE_URL = "jdbc:postgresql://roundhouse.proxy.rlwy.net:31120/railway"
    private const val DATABASE_USER = "postgres"
    private const val DATABASE_PASSWORD = "JpdQNErJdnjakQbNbaQNxacKdrjfzvxD"

    fun getConnection(): Connection {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)
    }

    fun createUsersTable() {
        try {
            val connection = getConnection()
            val statement = connection.createStatement()

            val sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL
                )
            """.trimIndent()

            statement.execute(sql)

            statement.close()
            connection.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}
