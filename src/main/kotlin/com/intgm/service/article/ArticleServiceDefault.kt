package com.intgm.service.article

import arrow.core.Either
import arrow.core.computations.either
import com.intgm.domain.ArticleModel
import com.intgm.domain.ArticleModel.storeArticleResToArticleStockDTO
import com.intgm.errors.ServiceError
import com.intgm.service.article.persistence.ArticleRepository
import com.intgm.service.store.persistence.StoreRepository
import org.apache.logging.log4j.LogManager
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ArticleServiceDefault(
    val storeRepository: StoreRepository,
    val articleRepository: ArticleRepository
) : ArticleService {

    private val logger = LogManager.getLogger(this::class.java)

    override fun createArticle(
        storeId: UUID,
        articleCreate: ArticleModel.ArticleCreate
    ): Either<ServiceError, ArticleModel.ArticleStockDTO> {

        return either.eager {
            val store = storeRepository.getStore(storeId).bind()
            val articleStockDTO = articleRepository.createArticle(store.id, articleCreate).bind()
            articleStockDTO
        }
    }

    override fun getArticles(storeId: UUID): Either<ServiceError, List<ArticleModel.ArticleStockDTO>> {
        return either.eager {
            val store = storeRepository.getStore(storeId).bind()
            val articleStockDTOs = articleRepository.getArticles(store.id).bind()
            articleStockDTOs.map { article ->
                storeArticleResToArticleStockDTO(article)
            }
        }
    }

    override fun getArticle(storeId: UUID, articleId: UUID): Either<ServiceError, ArticleModel.ArticleStockDTO> {
        return either.eager {
            val store = storeRepository.getStore(storeId).bind()
            val articleStockDTO = articleRepository.getArticle(store.id, articleId).bind()
            storeArticleResToArticleStockDTO(articleStockDTO)
        }
    }

    override fun reserveArticle(
        storeId: UUID,
        articleId: UUID,
        articleUpdate: ArticleModel.ArticleReservation
    ): Either<ServiceError, ArticleModel.ArticleStockDTO> {
        return either.eager {
            val store = storeRepository.getStore(storeId).bind()
            val article = articleRepository.getArticle(store.id, articleId).bind()
            val articleStockDTO = articleRepository.reserveArticle(store.id, article.articleId, articleId, articleUpdate.quantity).bind()
            storeArticleResToArticleStockDTO(articleStockDTO)
        }
    }

    override fun updateArticle(
        storeId: UUID,
        articleId: UUID,
        articleUpdate: ArticleModel.ArticleUpdate
    ): Either<ServiceError, ArticleModel.ArticleStockDTO> {
        return either.eager {
            val store = storeRepository.getStore(storeId).bind()
            val articleStockDTO = articleRepository.updateArticle(store.id, articleId, articleUpdate).bind()
            storeArticleResToArticleStockDTO(articleStockDTO)
        }
    }

    override fun deleteArticle(storeId: UUID, articleId: UUID): Either<ServiceError, Unit> {
        return either.eager {
            val store = storeRepository.getStore(storeId).bind()
            articleRepository.deleteArticle(store.id, articleId).bind()
        }
    }

}