package com.intgm.service.store

import com.intgm.service.Routes
import org.apache.logging.log4j.LogManager
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StoreRoutes(
    val storeService: StoreService
) : Routes {

    private val logger = LogManager.getLogger(this::class.java)

    @GET
    @Path("/stores")
    fun getStores(): Response = run {
        completeEither(Response.Status.OK, storeService.getStores())
    }

    @GET
    @Path("/stores/{storeId}")
    fun getStore(@PathParam("storeId") storeId: UUID) =
        completeEither(Response.Status.OK, storeService.getStore(storeId))


}