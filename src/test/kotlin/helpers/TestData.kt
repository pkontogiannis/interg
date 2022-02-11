package helpers

import com.intgm.domain.ArticleModel
import com.intgm.domain.StoreModel
import java.util.*

object TestData {

    val storeUUID: UUID = UUID.randomUUID()
    val articleUUID: UUID = UUID.randomUUID()

    val createArticle1 = ArticleModel.ArticleCreate("Article 1", 11)
    val createArticle2 = ArticleModel.ArticleCreate("Article 2", 10)

    val updateArticle1 = ArticleModel.ArticleCreate("Updated Article 1", 20)

    val store1 = StoreModel.StoreDTO(storeUUID, "Store 1")
    val article1 = ArticleModel.ArticleDTO(articleUUID, "Article 1")
}