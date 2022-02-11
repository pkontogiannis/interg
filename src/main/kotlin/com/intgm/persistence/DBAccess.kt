package com.intgm.persistence

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.ktorm.database.Database
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class DBAccess(
    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    val jdbcURL: String,
    @ConfigProperty(name = "quarkus.datasource.password")
    val jdbcPassword: String,
    @ConfigProperty(name = "quarkus.datasource.username")
    val jdbcUsername: String
) {


    val database = Database.connect(
        url = jdbcURL,
        user = jdbcUsername,
        password = jdbcPassword,
    )

}