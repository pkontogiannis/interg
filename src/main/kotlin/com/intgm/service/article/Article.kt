package com.intgm.service.article

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Instant
import java.util.*


interface Article : Entity<Article> {
    companion object : Entity.Factory<Article>()

    val id: Int
    var uuid: UUID
    var name: String
}

object Articles : Table<Article>(schema = "stock_keeping", tableName = "article") {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = uuid("article_uuid").bindTo { it.uuid }
    val name = varchar("name").bindTo { it.name }
}

val Database.articles get() = this.sequenceOf(Articles)

interface StoreArticle : Entity<StoreArticle> {
    companion object : Entity.Factory<StoreArticle>()

    val articleId: Int
    val storeId: Int
    val availableStock: Int
}

object StoreArticles : Table<StoreArticle>(schema = "stock_keeping", tableName = "store_article") {
    val articleId: Column<Int> = int("article_id").primaryKey().bindTo { it.articleId }
    val storeId: Column<Int> = int("store_id").primaryKey().bindTo { it.storeId }
    val availableStock = int("available_stock").bindTo { it.availableStock }
}

interface ReservedArticle : Entity<ReservedArticle> {
    companion object : Entity.Factory<ReservedArticle>()

    val articleId: Int
    val storeId: Int
    val reservedStock: Int
    val reservationTime: Instant
}

object ReservedArticles : Table<ReservedArticle>(schema = "stock_keeping", tableName = "reserved_article") {
    val articleId: Column<Int> = int("article_id").primaryKey().bindTo { it.articleId }
    val storeId: Column<Int> = int("store_id").primaryKey().bindTo { it.storeId }
    val reservedStock = int("reserved_stock").bindTo { it.reservedStock }
    val reservationTime = timestamp("reservation_time").bindTo { it.reservationTime }
}

