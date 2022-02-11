package com.intgm.errors

import org.jboss.resteasy.reactive.RestResponse

sealed interface HttpError {
    val statusCode: Int
    val code: String
    val message: String
}

object DefaultNotFoundErrorHttp : HttpError {
    override val statusCode: Int = RestResponse.StatusCode.NOT_FOUND
    override val code = "DefaultNotFoundError"
    override val message = "Can't find requested asset"
}

data class InternalErrorHttp(override val message: String) : HttpError {
    override val statusCode: Int = RestResponse.StatusCode.INTERNAL_SERVER_ERROR
    override val code: String = "internalError"
}

data class ForbiddenErrorHttp(override val message: String) : HttpError {
    override val statusCode: Int = RestResponse.StatusCode.FORBIDDEN
    override val code: String = "forbidden"
}