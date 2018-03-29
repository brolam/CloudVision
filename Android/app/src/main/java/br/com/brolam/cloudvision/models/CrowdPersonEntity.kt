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

@Entity(tableName = "crowdsPeople",
        foreignKeys = [(ForeignKey(
                entity = CrowdEntity::class,
                parentColumns = ["id"],
                childColumns = ["crowdId"],
                onDelete = CASCADE))]
)
data class CrowdPersonEntity(
        @ColumnInfo(name = "id")
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "crowdId") var crowdId: Long = 0,
        @ColumnInfo(name = "insertedOrder") var insertedOrder: Int,
        @ColumnInfo(name = "facePositionX") var facePositionX: Float,
        @ColumnInfo(name = "facePositionY") var facePositionY: Float,
        @ColumnInfo(name = "faceWidth") var faceWidth: Float,
        @ColumnInfo(name = "faceHeight") var faceHeight: Float,
        @ColumnInfo(name = "winnerPosition") var winnerPosition: Int = 0
)