package com.intgm.service.store

import arrow.core.Either
import com.intgm.domain.StoreModel
import com.intgm.domain.StoreModel.storeToStoreDTO
import com.intgm.errors.ServiceError
import com.intgm.service.store.persistence.StoreRepository
import org.apache.logging.log4j.LogManager
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StoreServiceDefault(
    val storeRepository: StoreRepository
) : StoreService {

    private val logger = LogManager.getLogger(this::class.java)

    override fun createStore(storeCreate: StoreModel.StoreCreate): StoreModel.StoreDTO {
        TODO("Not yet implemented")
    }

    override fun getStores(): Either<ServiceError, List<StoreModel.StoreDTO>> {
        return storeRepository.getStores().fold(
            { error ->
                Either.Left(error)
            },
            { stores ->
                Either.Right(stores.map { store ->
                    storeToStoreDTO(store)
                })
            }
        )
    }

    override fun getStore(storeId: UUID): Either<ServiceError, StoreModel.StoreDTO> {
        return storeRepository.getStore(storeId).fold(
            { error ->
                Either.Left(error)
            },
            { store ->
                Either.Right(
                    storeToStoreDTO(store)
                )
            }
        )
    }

    override fun deleteStore(storeId: UUID) {
        TODO("Not yet implemented")
    }

}