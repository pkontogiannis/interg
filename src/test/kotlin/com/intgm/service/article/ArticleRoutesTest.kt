package com.intgm.service.article

import com.intgm.domain.ArticleModel
import com.intgm.domain.StoreModel
import com.intgm.persistence.MigrationService
import com.intgm.service.article.persistence.ArticleRepository
import com.intgm.service.store.persistence.StoreRepository
import helpers.TestData
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject


@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArticleRoutesTest(

) {

    @Inject
    lateinit var migrationService: MigrationService

    @Inject
    lateinit var storeRepository: StoreRepository

    @Inject
    lateinit var articleRepository: ArticleRepository

    @Inject
    lateinit var articleService: ArticleService

    @BeforeAll
    fun beforeAll() {
        migrationService.flywayMigrate()

        storeRepository.deleteAllStores()

        val storeId = TestData.store1.storeId
        Mockito.mockStatic(UUID::class.java).use { utilities ->
            utilities.`when`<Any>(UUID::randomUUID).thenReturn(storeId)
            storeRepository.createStore(StoreModel.StoreCreate("Dummy Name"))
        }
    }


    @BeforeEach
    fun beforeEach() {
        articleRepository.deleteAllArticles()
    }

    @Test
    fun `should create a new article`() {

        val articleStockDTO: ArticleModel.ArticleStockDTO =
            given()
                .contentType("application/json")
                .body(TestData.createArticle1)
                .post("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(201)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.createArticle1.articleName, articleStockDTO.articleName)
        Assertions.assertEquals(TestData.createArticle1.availableStock, articleStockDTO.availableStock)
    }

    @Test
    fun `should return all articles of a store`() {
        articleService.createArticle(TestData.storeUUID, TestData.createArticle1)
        articleService.createArticle(TestData.storeUUID, TestData.createArticle2)

        val articleStockDTOs: Array<ArticleModel.ArticleStockDTO> =
            given()
                .`when`()
                .get("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<ArticleModel.ArticleStockDTO>::class.java)

        Assertions.assertEquals(2, articleStockDTOs.size)
    }

    @Test
    fun `should return a 404 status code where a store is not exist`() {
        given()
            .`when`()
            .get("/api/v1/stores/dummyID/articles")
            .then()
            .statusCode(404)
    }


    @Test
    fun `should return an article of a store`() {
        val createdArticle: ArticleModel.ArticleStockDTO =
            given()
                .contentType("application/json")
                .body(TestData.createArticle1)
                .post("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(201)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.createArticle1.articleName, createdArticle.articleName)
        Assertions.assertEquals(TestData.createArticle1.availableStock, createdArticle.availableStock)

        val articleStockDTO: ArticleModel.ArticleStockDTO =
            given()
                .`when`()
                .get("/api/v1/stores/${TestData.storeUUID}/articles/${createdArticle.articleId}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.createArticle1.availableStock, articleStockDTO.availableStock)
        Assertions.assertEquals(TestData.createArticle1.articleName, articleStockDTO.articleName)
    }

    @Test
    fun `should return an not found article of a store`() {
        given()
            .`when`()
            .get("/api/v1/stores/${TestData.store1.storeId}/articles/${UUID.randomUUID()}")
            .then()
            .statusCode(404)
    }

    @Test
    fun `should update an article of a store`() {
        val createdArticle: ArticleModel.ArticleStockDTO =
            given()
                .contentType("application/json")
                .body(TestData.createArticle1)
                .post("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(201)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.createArticle1.articleName, createdArticle.articleName)
        Assertions.assertEquals(TestData.createArticle1.availableStock, createdArticle.availableStock)

        val articleStockDTO: ArticleModel.ArticleStockDTO =
            given()
                .contentType("application/json")
                .body(TestData.updateArticle1)
                .put("/api/v1/stores/${TestData.store1.storeId}/articles/${createdArticle.articleId}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.updateArticle1.articleName, articleStockDTO.articleName)
        Assertions.assertEquals(TestData.updateArticle1.availableStock, articleStockDTO.availableStock)
    }

    @Test
    fun `should delete an article of a store`() {
        val createdArticle: ArticleModel.ArticleStockDTO =
            given()
                .contentType("application/json")
                .body(TestData.createArticle1)
                .post("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(201)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.createArticle1.articleName, createdArticle.articleName)
        Assertions.assertEquals(TestData.createArticle1.availableStock, createdArticle.availableStock)

        given()
            .delete("/api/v1/stores/${TestData.store1.storeId}/articles/${createdArticle.articleId}")
            .then()
            .statusCode(200)
            .extract()

        val articleStockDTOs: Array<ArticleModel.ArticleStockDTO> =
            given()
                .`when`()
                .get("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<ArticleModel.ArticleStockDTO>::class.java)

        Assertions.assertEquals(0, articleStockDTOs.size)
    }


    @Test
    fun `should reserve an article of a store`() {
        val createdArticle: ArticleModel.ArticleStockDTO =
            given()
                .contentType("application/json")
                .body(TestData.createArticle1)
                .post("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(201)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.createArticle1.articleName, createdArticle.articleName)
        Assertions.assertEquals(TestData.createArticle1.availableStock, createdArticle.availableStock)

        given()
            .contentType("application/json")
            .body(ArticleModel.ArticleReservation(3, createdArticle.articleId))
            .patch("/api/v1/stores/${TestData.store1.storeId}/articles/${createdArticle.articleId}")
            .then()
            .statusCode(200)


        val articleStockDTO: ArticleModel.ArticleStockDTO =
            given()
                .`when`()
                .get("/api/v1/stores/${TestData.storeUUID}/articles/${createdArticle.articleId}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(8, articleStockDTO.availableStock)
    }

    @Test
    fun `should get a forbidden in case of not enough stock`() {
        val createdArticle: ArticleModel.ArticleStockDTO =
            given()
                .contentType("application/json")
                .body(TestData.createArticle1)
                .post("/api/v1/stores/${TestData.store1.storeId}/articles")
                .then()
                .statusCode(201)
                .extract()
                .`as`(ArticleModel.ArticleStockDTO::class.java)

        Assertions.assertEquals(TestData.createArticle1.articleName, createdArticle.articleName)
        Assertions.assertEquals(TestData.createArticle1.availableStock, createdArticle.availableStock)

        given()
            .contentType("application/json")
            .body(ArticleModel.ArticleReservation(34, createdArticle.articleId))
            .patch("/api/v1/stores/${TestData.store1.storeId}/articles/${createdArticle.articleId}")
            .then()
            .statusCode(403)

    }


}