package br.com.brolam.cloudvision.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

/**
 * Created by brenomarques on 21/01/2018.
 *
 */
class CvRecognizableEntity {
    @Embedded
    lateinit var crowd: CvImageEntity
    @Relation(parentColumn = "id", entityColumn = "crowdId", entity = CvRecognizableItemEntity::class)
    lateinit var people: List<CvRecognizableItemEntity>
}
