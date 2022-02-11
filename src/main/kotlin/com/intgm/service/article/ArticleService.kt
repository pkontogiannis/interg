package com.intgm.service.article

import arrow.core.Either
import com.intgm.domain.ArticleModel
import com.intgm.domain.ArticleModel.ArticleCreate
import com.intgm.errors.ServiceError
import java.util.*

interface ArticleService {

    fun getArticles(storeId: UUID): Either<ServiceError, List<ArticleModel.ArticleStockDTO>>
    fun getArticle(storeId: UUID, articleId: UUID): Either<ServiceError, ArticleModel.ArticleStockDTO>

    fun createArticle(storeId: UUID, articleCreate: ArticleCreate): Either<ServiceError, ArticleModel.ArticleStockDTO>

    fun reserveArticle(
        storeId: UUID,
        articleId: UUID,
        articleUpdate: ArticleModel.ArticleReservation
    ): Either<ServiceError, ArticleModel.ArticleStockDTO>

    fun updateArticle(
        storeId: UUID,
        articleId: UUID,
        articleUpdate: ArticleModel.ArticleUpdate
    ): Either<ServiceError, ArticleModel.ArticleStockDTO>

    fun deleteArticle(storeId: UUID, articleId: UUID): Either<ServiceError, Unit>
}