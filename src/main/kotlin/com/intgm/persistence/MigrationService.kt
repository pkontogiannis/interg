package com.intgm.persistence

import org.flywaydb.core.Flyway
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class MigrationService(
    @Inject
    var flyway: Flyway
) {

    fun flywayMigrate() {
        flyway.clean()
        flyway.migrate()
        println(flyway.info().current().version.toString())
    }

}
