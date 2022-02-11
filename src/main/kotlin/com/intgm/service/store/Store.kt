package com.intgm.service.store

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar
import java.util.*


interface Store : Entity<Store> {
    companion object : Entity.Factory<Store>()

    val id: Int
    var uuid: UUID
    var name: String
}

object Stores : Table<Store>(schema = "stock_keeping", tableName = "store") {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = uuid("store_uuid").bindTo { it.uuid }
    val name = varchar("name").bindTo { it.name }
}

val Database.stores get() = this.sequenceOf(Stores)



