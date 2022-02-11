package com.intgm.service.store.persistence

import arrow.core.Either
import com.intgm.domain.StoreModel
import com.intgm.errors.DatabaseError
import com.intgm.service.store.Store
import java.util.*

interface StoreRepository {

    fun createStore(storeCreate: StoreModel.StoreCreate): StoreModel.StoreDTO
    fun getStores(): Either<DatabaseError, List<Store>>
    fun getStore(storeId: UUID): Either<DatabaseError, Store>
    fun deleteStore(storeId: UUID)
    fun deleteAllStores()

}