package com.intgm.service.store.persistence

import arrow.core.Either
import com.intgm.domain.StoreModel
import com.intgm.errors.DatabaseError
import com.intgm.errors.RecordNotFound
import com.intgm.persistence.DBAccess
import com.intgm.service.store.Store
import com.intgm.service.store.Stores
import com.intgm.service.store.stores
import org.ktorm.dsl.delete
import org.ktorm.dsl.deleteAll
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.EntitySequence
import org.ktorm.entity.find
import org.ktorm.entity.toList
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StoreRepositorySQL(
    dbAccess: DBAccess
) : StoreRepository {

    val db = dbAccess.database

    override fun createStore(storeCreate: StoreModel.StoreCreate): StoreModel.StoreDTO {
        val uuid: UUID = UUID.randomUUID()
        val id: Int = db.insertAndGenerateKey(Stores) {
            set(it.name, storeCreate.storeName)
            set(it.uuid, uuid)
        } as Int
        return StoreModel.StoreDTO(uuid, storeCreate.storeName)
    }

    override fun getStores(): Either<DatabaseError, List<Store>> {
        val stores: EntitySequence<Store, Stores> = db.stores
        return Either.Right(stores.toList())
    }

    override fun getStore(storeId: UUID): Either<DatabaseError, Store> {
        val store: Store? = db.stores.find { it.uuid eq storeId }
        return if (store == null) {
            Either.Left(RecordNotFound("errorMessage"))
        } else {
            Either.Right(store)
        }
    }

    override fun deleteStore(storeId: UUID) {
        TODO("Not yet implemented")
    }

    override fun deleteAllStores() {
        db.deleteAll(Stores)
    }

}