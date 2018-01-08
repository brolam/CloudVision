package br.com.brolam.cloudvision.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by brenomarques on 07/01/2018.
 *
 */
@Database(entities = [(CrowdEntity::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun crowdDao(): CrowdDao
}