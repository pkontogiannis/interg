package com.intgm.service.store

import arrow.core.Either
import com.intgm.domain.StoreModel.StoreCreate
import com.intgm.domain.StoreModel.StoreDTO
import com.intgm.errors.ServiceError
import java.util.*

interface StoreService {
    fun createStore(storeCreate: StoreCreate): StoreDTO
    fun getStores(): Either<ServiceError, List<StoreDTO>>
    fun getStore(storeId: UUID): Either<ServiceError, StoreDTO>
    fun deleteStore(storeId: UUID)
}