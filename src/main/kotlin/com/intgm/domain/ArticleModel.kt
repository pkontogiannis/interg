package com.intgm.domain

import com.intgm.service.article.Article
import com.intgm.service.article.persistence.ArticleRepositorySQL
import java.util.*

object ArticleModel {

    data class ArticleCreate(
        val articleName: String,
        val availableStock: Int
    )

    data class ArticleUpdate(
        val articleName: String,
        val availableStock: Int
    )

    data class ArticleReservation(
        val quantity: Int,
        val articleId: UUID
    )

    data class ArticleDTO(
        val articleId: UUID,
        val articleName: String
    )

    data class ArticleStockDTO(
        val articleId: UUID,
        val articleName: String,
        val availableStock: Int
    )


    fun articleToArticleDTO(article: Article): ArticleDTO =
        ArticleDTO(
            article.uuid,
            article.name
        )

    fun storeArticleResToArticleStockDTO(storeArticleRes: ArticleRepositorySQL.StoreArticleRes): ArticleStockDTO =
        ArticleStockDTO(
            storeArticleRes.articleUuid,
            storeArticleRes.articleName,
            storeArticleRes.availableStock
        )

}