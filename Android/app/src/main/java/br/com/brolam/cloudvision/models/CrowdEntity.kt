package br.com.brolam.cloudvision.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by brenomarques on 07/01/2018.
 *
 */
@Entity(tableName = "crowds")
data class CrowdEntity(
        @ColumnInfo(name = "id")
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "trackedImageName") var trackedImageName: String = "",
        @ColumnInfo(name = "created") var created: Long
)