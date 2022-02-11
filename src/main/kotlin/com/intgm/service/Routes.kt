package com.intgm.service

import arrow.core.Either
import com.intgm.errors.ErrorResponse
import com.intgm.errors.ServiceError
import com.intgm.errors.ServiceErrorObj
import org.apache.logging.log4j.LogManager
import javax.ws.rs.core.Response

interface Routes {

    companion object Routes {
        val logger = LogManager.getLogger(this::class.java)
    }

    fun <T> completeEither(statusCode: Response.Status, either: Either<ServiceError, T>): Response =
        when (either) {
            is Either.Left -> {
                val httpError = ServiceErrorObj.httpErrorMapper(either.value)
                Response
                    .status(httpError.statusCode)
                    .entity(
                        ErrorResponse(
                            httpError.code,
                            httpError.message
                        )
                    )
                    .build()
            }
            is Either.Right ->
                Response.status(statusCode)
                    .entity(either.value)
                    .build()
        }

}
