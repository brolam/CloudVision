package br.com.brolam.cloudvision.models


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


import android.arch.persistence.room.ForeignKey.CASCADE
/**
 * Created by brenomarques on 07/01/2018.
 *
 */

@Entity(foreignKeys = [(ForeignKey(
        entity = CrowdEntity::class,
        parentColumns = ["id"],
        childColumns = ["crowdId"],
        onDelete = CASCADE))]
)
class CrowdPersonEntity(
        @ColumnInfo(name = "id")
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "crowdId") var crowdId: Long
)