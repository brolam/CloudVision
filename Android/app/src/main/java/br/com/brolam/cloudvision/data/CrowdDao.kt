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
    @Query("select count(id) from crowds")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crowd: CrowdEntity)

    @Query("select * from crowds")
    fun getAll(): List<CrowdEntity>

    @Query("select * from crowds where id = :id")
    fun getById(id: Long) : LiveData<CrowdEntity>

}