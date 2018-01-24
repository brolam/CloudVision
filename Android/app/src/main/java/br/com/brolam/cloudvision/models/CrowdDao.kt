package br.com.brolam.cloudvision.models

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

    @Query("SELECT COUNT(id) FROM crowdsPeople WHERE crowdId =:crowdId")
    fun countPeople(crowdId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crowd: CrowdEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(people: List<CrowdPersonEntity>)

    @Query("SELECT * FROM crowds")
    fun getAll(): List<CrowdEntity>

    @Query("SELECT * FROM crowds WHERE id = :id")
    fun getById(id: Long): LiveData<CrowdEntity>

    @Query("SELECT * FROM crowds WHERE id = :id")
    fun getCrowdPeopleById(id: Long): LiveData<CrowdPeopleEntity>

    @Query("SELECT * FROM crowds")
    fun getAllLiveData(): LiveData<List<CrowdEntity>>

    @Query("SELECT * FROM crowdsPeople WHERE crowdId = :crowdId ORDER BY insertedOrder")
    fun getPeople(crowdId: Int) : LiveData<List<CrowdPersonEntity>>



}