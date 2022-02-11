package com.intgm.domain

import com.intgm.service.store.Store
import java.util.*

object StoreModel {

    data class StoreCreate(
        val storeName: String
    )

    data class StoreDTO(
        val storeId: UUID,
        val storeName: String
    )

    fun storeToStoreDTO(store: Store): StoreDTO =
        StoreDTO(
            store.uuid,
            store.name
        )
}