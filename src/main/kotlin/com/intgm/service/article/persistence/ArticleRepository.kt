package com.intgm.service.article.persistence

import arrow.core.Either
import com.intgm.domain.ArticleModel
import com.intgm.errors.DatabaseError
import java.util.*

interface ArticleRepository {

    fun createArticle(storeId: Int, articleCreate: ArticleModel.ArticleCreate): Either<DatabaseError, ArticleModel.ArticleStockDTO>
    fun getArticles(storeId: Int): Either<DatabaseError, List<ArticleRepositorySQL.StoreArticleRes>>
    fun getArticle(storeId: Int, articleId: UUID): Either<DatabaseError, ArticleRepositorySQL.StoreArticleRes>

    fun updateArticle(
        storeId: Int,
        articleId: UUID,
        articleUpdate: ArticleModel.ArticleUpdate
    ): Either<DatabaseError, ArticleRepositorySQL.StoreArticleRes>

    fun reserveArticle(
        storeId: Int,
        articleId: Int,
        articleUuid: UUID,
        quantity: Int
    ): Either<DatabaseError, ArticleRepositorySQL.StoreArticleRes>

    fun deleteArticle(storeId: Int, articleId: UUID): Either<DatabaseError, Boolean>
    fun deleteAllArticles()

}