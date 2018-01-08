package br.com.brolam.cloudvision.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 * Created by brenomarques on 07/01/2018.
 *
 */
@Dao
interface CrowdDao {
    @Query("SELECT COUNT(id) FROM crowds")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crowd: CrowdEntity)

    @Query("SELECT * FROM crowds")
    fun getAll(): List<CrowdEntity>

    @Query("SELECT * FROM crowds WHERE id = :id")
    fun getById(id: Long) : LiveData<CrowdEntity>

}