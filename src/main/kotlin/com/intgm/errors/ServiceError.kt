package com.intgm.errors


sealed interface ServiceError {
    val message: String
}

sealed class DatabaseError : ServiceError

data class RecordNotFound(override val message: String) : DatabaseError()

data class GenericDatabaseError(override val message: String) : DatabaseError()

data class NotEnoughStock(override val message: String) : DatabaseError()

object ServiceErrorObj {

    fun httpErrorMapper(error: ServiceError) = run {
        when (error) {
            is RecordNotFound ->
                DefaultNotFoundErrorHttp
            is GenericDatabaseError -> InternalErrorHttp("")
            is NotEnoughStock -> ForbiddenErrorHttp(error.message)
        }
    }

}
