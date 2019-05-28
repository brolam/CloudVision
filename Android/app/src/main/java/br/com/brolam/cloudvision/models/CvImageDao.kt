package br.com.brolam.cloudvision.models

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

/**
 * Created by brenomarques on 07/01/2018.
 *
 */
@Dao
interface CvImageDao {
    @Query("SELECT COUNT(id) FROM cvImageEntity")
    fun count(): Int

    @Query("SELECT COUNT(id) FROM cvRecognizableItemEntity WHERE crowdId =:crowdId")
    fun countPeople(crowdId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crowd: CvImageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(people: List<CvRecognizableItemEntity>)

    @Query("SELECT * FROM cvImageEntity")
    fun getAll(): List<CvImageEntity>

    @Query("SELECT * FROM cvImageEntity WHERE id = :id")
    fun getById(id: Long): LiveData<CvImageEntity>

    @Query("SELECT * FROM cvImageEntity WHERE id = :id")
    fun getCrowdPeopleById(id: Long): LiveData<CvRecognizableEntity>

    @Query("SELECT * FROM cvImageEntity ORDER BY created DESC")
    fun getAllOrderedByCreated(): LiveData<List<CvImageEntity>>

    @Query("SELECT * FROM cvRecognizableItemEntity WHERE crowdId = :crowdId ORDER BY insertedOrder")
    fun getPeople(crowdId: Long): List<CvRecognizableItemEntity>

    @Delete
    fun deleteOneCrowd(crowd: CvImageEntity)

    @Query("DELETE FROM cvImageEntity")
    fun deleteAllCrowds()

    @Update
    fun updatePerson(person: CvRecognizableItemEntity)

}