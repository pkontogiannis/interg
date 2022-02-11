package com.intgm.service.store

import com.intgm.domain.StoreModel
import com.intgm.persistence.MigrationService
import com.intgm.service.store.persistence.StoreRepository
import helpers.TestData
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject


@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreRoutesTest(

) {

    @Inject
    lateinit var migrationService: MigrationService

    @Inject
    lateinit var storeRepository: StoreRepository

    @BeforeAll
    fun beforeAll() {
        migrationService.flywayMigrate()
        storeRepository.deleteAllStores()

        val randomUUID = TestData.storeUUID
        Mockito.mockStatic(UUID::class.java).use { utilities ->
            utilities.`when`<Any>(UUID::randomUUID).thenReturn(randomUUID)
            storeRepository.createStore(StoreModel.StoreCreate("Dummy Name"))
        }
    }

    @Test
    fun `should return the list of stores`() {
        val stores: Array<StoreModel.StoreDTO> =
            given()
                .`when`()
                .get("/api/v1/stores")
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<StoreModel.StoreDTO>::class.java)

        Assertions.assertEquals(1, stores.size)
    }

    @Test
    fun `should return a specific store`() {

        val store: StoreModel.StoreDTO =
            given()
                .`when`()
                .get("/api/v1/stores/${TestData.storeUUID}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(StoreModel.StoreDTO::class.java)

        Assertions.assertEquals(TestData.storeUUID, store.storeId)
    }

    @Test
    fun `should return a 404 status code where a store is not exist`() {
        given()
            .`when`()
            .get("/api/v1/stores/dummyID")
            .then()
            .statusCode(404)
    }

}