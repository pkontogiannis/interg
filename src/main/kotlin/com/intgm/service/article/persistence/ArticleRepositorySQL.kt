package com.intgm.service.article.persistence

import arrow.core.Either
import arrow.core.computations.either
import com.intgm.domain.ArticleModel
import com.intgm.errors.DatabaseError
import com.intgm.errors.GenericDatabaseError
import com.intgm.errors.NotEnoughStock
import com.intgm.errors.RecordNotFound
import com.intgm.persistence.DBAccess
import com.intgm.service.article.*
import com.intgm.service.store.Stores
import org.apache.logging.log4j.LogManager
import org.ktorm.dsl.*
import org.ktorm.entity.find
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ArticleRepositorySQL(
    dbAccess: DBAccess,
) : ArticleRepository {

    private val logger = LogManager.getLogger(this::class.java)

    val db = dbAccess.database

    data class StoreArticleRes(
        val articleId: Int,
        val articleUuid: UUID,
        val articleName: String,
        val availableStock: Int
    )

    override fun createArticle(
        storeId: Int,
        articleCreate: ArticleModel.ArticleCreate
    ): Either<DatabaseError, ArticleModel.ArticleStockDTO> {
        val uuid: UUID = UUID.randomUUID()

        try {
            val res = db.useTransaction {
                val id: Int = db.insertAndGenerateKey(Articles) {
                    set(it.name, articleCreate.articleName)
                    set(it.uuid, uuid)
                } as Int
                db.insert(StoreArticles) {
                    set(it.storeId, storeId)
                    set(it.articleId, id)
                    set(it.availableStock, articleCreate.availableStock)
                }
                id
            }

            return Either.Right(
                ArticleModel.ArticleStockDTO(
                    uuid,
                    articleCreate.articleName,
                    articleCreate.availableStock
                )
            )
        } catch (e: Exception) {
            logger.info("[${this::class.java.simpleName}] failed to store article with id: $uuid")
            return Either.Left(GenericDatabaseError(e.localizedMessage))
        }

    }

    fun removeExpiredReservations() {
        db.useConnection { conn ->
            val sql = """
                select remove_expired_reservations()
            """
            conn.prepareStatement(sql)
        }
    }

    override fun getArticles(storeId: Int): Either<DatabaseError, List<StoreArticleRes>> {

        removeExpiredReservations()

        val results = db
            .from(StoreArticles)
            .leftJoin(Articles, on = StoreArticles.articleId eq Articles.id)
            .select(Articles.id, Articles.uuid, Articles.name, StoreArticles.availableStock)
            .orderBy(Articles.id.asc())
            .map { row ->
                StoreArticleRes(
                    articleId = row[Articles.id]!!,
                    articleUuid = row[Articles.uuid]!!,
                    articleName = row[Articles.name]!!,
                    availableStock = row[StoreArticles.availableStock]!!
                )
            }

        return Either.Right(results.toList())

    }

    override fun getArticle(storeId: Int, articleId: UUID): Either<DatabaseError, StoreArticleRes> {
        removeExpiredReservations()

        val results: StoreArticleRes? = db
            .from(StoreArticles)
            .leftJoin(Articles, on = StoreArticles.articleId eq Articles.id)
            .select(Articles.id, Articles.uuid, Articles.name, StoreArticles.availableStock)
            .where(Articles.uuid eq articleId)
            .map { row ->
                StoreArticleRes(
                    articleId = row[Articles.id]!!,
                    articleUuid = row[Articles.uuid]!!,
                    articleName = row[Articles.name]!!,
                    availableStock = row[StoreArticles.availableStock]!!
                )
            }.firstOrNull()

        return if (results == null) {
            Either.Left(RecordNotFound("errorMessage"))
        } else {
            Either.Right(results)
        }
    }


    fun findArticle(articleId: UUID): Either<DatabaseError, Article> {
        val article = db.articles.find { it.uuid eq articleId }
        return if (article == null) {
            Either.Left(RecordNotFound("errorMessage"))
        } else {
            Either.Right(article)
        }
    }

    override fun updateArticle(
        storeId: Int,
        articleId: UUID,
        articleUpdate: ArticleModel.ArticleUpdate
    ): Either<DatabaseError, StoreArticleRes> {

        return db.useTransaction {
            db.from(StoreArticles)
                .leftJoin(Articles, on = StoreArticles.articleId eq Articles.id)
                .select(Articles.id, Articles.uuid, Articles.name, StoreArticles.availableStock)
                .where(Articles.uuid eq articleId)
                .map { row ->
                    StoreArticleRes(
                        articleId = row[Articles.id]!!,
                        articleUuid = row[Articles.uuid]!!,
                        articleName = row[Articles.name]!!,
                        availableStock = row[StoreArticles.availableStock]!!
                    )
                }.firstOrNull()

            val articleOpt: Either<DatabaseError, Article> = findArticle(articleId)

            articleOpt.fold({ value ->
                Either.Left(value)
            }, { article ->
                db.update(Articles) {
                    set(it.name, articleUpdate.articleName)
                    where {
                        it.uuid eq articleId
                    }
                }
                db.update(StoreArticles) {
                    set(it.availableStock, articleUpdate.availableStock)
                    where {
                        (it.articleId eq article.id) and (it.storeId eq storeId)
                    }
                }
                Either.Right(
                    StoreArticleRes(
                        article.id,
                        articleId,
                        articleUpdate.articleName,
                        articleUpdate.availableStock
                    )
                )
            })
        }

    }

    fun getAvailableStock(articleId: Int, storeId: Int): Either<DatabaseError, StoreArticleRes> {
        removeExpiredReservations()

        val storeArticle: StoreArticleRes? = db
            .from(StoreArticles)
            .leftJoin(Articles, on = StoreArticles.articleId eq Articles.id)
            .select(Articles.id, Articles.uuid, Articles.name, StoreArticles.availableStock)
            .where((Articles.id eq articleId) and (StoreArticles.storeId eq storeId))
            .map { row ->
                StoreArticleRes(
                    articleId = row[Articles.id]!!,
                    articleUuid = row[Articles.uuid]!!,
                    articleName = row[Articles.name]!!,
                    availableStock = row[StoreArticles.availableStock]!!
                )
            }.firstOrNull()

        return if (storeArticle == null) {
            Either.Left(RecordNotFound("errorMessage"))
        } else {
            Either.Right(storeArticle)
        }
    }

    override fun reserveArticle(
        storeId: Int,
        articleId: Int,
        articleUuid: UUID,
        quantity: Int
    ): Either<DatabaseError, StoreArticleRes> {
        db.useTransaction {
            return either.eager {
                val availableStock = getAvailableStock(articleId, storeId).bind()
                if (availableStock.availableStock > quantity) {
                    db.insert(ReservedArticles) {
                        set(it.storeId, storeId)
                        set(it.articleId, articleId)
                        set(it.reservedStock, quantity)
                    }
                    db.update(StoreArticles) {
                        set(it.availableStock, availableStock.availableStock - quantity)
                        where {
                            (it.articleId eq articleId) and (it.storeId eq storeId)
                        }
                    }
                    getAvailableStock(articleId, storeId)
                } else {
                    Either.Left(NotEnoughStock("Not enough stock for this article"))
                }.bind()
            }
        }
    }

    override fun deleteArticle(storeId: Int, articleId: UUID): Either<DatabaseError, Boolean> {
        db.delete(Articles) { it.uuid eq articleId }
        return Either.Right(true)
    }

    override fun deleteAllArticles() {
        db.deleteAll(Articles)
    }
}

