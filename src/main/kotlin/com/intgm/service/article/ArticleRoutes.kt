package com.intgm.service.article

import com.intgm.domain.ArticleModel
import com.intgm.service.Routes
import org.apache.logging.log4j.LogManager
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ArticleRoutes(
    val articleService: ArticleService
) : Routes {

    private val logger = LogManager.getLogger(this::class.java)

    @GET
    @Path("/stores/{storeId}/articles")
    fun getArticles(@PathParam("storeId") storeId: UUID): Response = run {
        completeEither(Response.Status.OK, articleService.getArticles(storeId))
    }

    @GET
    @Path("/stores/{storeId}/articles/{articleId}")
    fun getArticle(
        @PathParam("storeId") storeId: UUID,
        @PathParam("articleId") articleId: UUID
    ) =
        completeEither(Response.Status.OK, articleService.getArticle(storeId, articleId))

    @POST
    @Path("/stores/{storeId}/articles")
    fun createArticle(@PathParam("storeId") storeId: UUID, articleCreate: ArticleModel.ArticleCreate) =
        completeEither(Response.Status.CREATED, articleService.createArticle(storeId, articleCreate))

    @PUT
    @Path("/stores/{storeId}/articles/{articleId}")
    fun updateArticle(
        @PathParam("storeId") storeId: UUID,
        @PathParam("articleId") articleId: UUID,
        articleUpdate: ArticleModel.ArticleUpdate
    ) =
        completeEither(Response.Status.OK, articleService.updateArticle(storeId, articleId, articleUpdate))

    @PATCH
    @Path("/stores/{storeId}/articles/{articleId}")
    fun reserveArticle(
        @PathParam("storeId") storeId: UUID,
        @PathParam("articleId") articleId: UUID,
        articleReserve: ArticleModel.ArticleReservation
    ) =
        completeEither(Response.Status.OK, articleService.reserveArticle(storeId, articleId, articleReserve))

    @DELETE
    @Path("/stores/{storeId}/articles/{articleId}")
    fun deleteArticle(
        @PathParam("storeId") storeId: UUID,
        @PathParam("articleId") articleId: UUID
    ) =
        completeEither(Response.Status.OK, articleService.deleteArticle(storeId, articleId))


}