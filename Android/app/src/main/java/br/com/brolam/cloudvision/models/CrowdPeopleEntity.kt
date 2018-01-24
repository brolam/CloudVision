package br.com.brolam.cloudvision.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

/**
 * Created by brenomarques on 21/01/2018.
 *
 */
class CrowdPeopleEntity {
    @Embedded
    lateinit var crowd: CrowdEntity
    @Relation(parentColumn = "id", entityColumn = "crowdId", entity = CrowdPersonEntity::class)
    lateinit var people: List<CrowdPersonEntity>
}
