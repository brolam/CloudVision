package br.com.brolam.cloudvision.models

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Room
import android.content.Context


/**
 * Created by brenomarques on 07/01/2018.
 *
 */
@Database(entities = [(CvImageEntity::class), (CrowdPersonEntity::class)], version = 2)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "cloudvision.db"
                ).fallbackToDestructiveMigration()
                 .build()
            }
            return instance!!
        }
    }

    abstract fun crowdDao(): CrowdDao
}