package br.com.brolam.cloudvision

import android.arch.persistence.room.Room
import android.support.test.runner.AndroidJUnit4
import br.com.brolam.cloudvision.data.AppDatabase
import br.com.brolam.cloudvision.data.CrowdDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import android.support.test.InstrumentationRegistry
import br.com.brolam.cloudvision.data.CrowdEntity
import java.util.*

/**
* Created by brenomarques on 07/01/2018.
*
*/
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var crowdDao: CrowdDao
    private lateinit var appDataBase: AppDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getContext()
        this.appDataBase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        this.crowdDao = appDataBase.crowdDao()
    }

    @After
    fun tearDown() {
        this.appDataBase.close()
    }

    @Test
    fun insertOneCrowd() {
        val currentCountCrowds = this.crowdDao.count()
        val crowd = CrowdEntity(
                title = "Dec 31 2017 00:00:00",
                created = Date().time,
                trackedImageUri = "/Picture/crowd_1.jpp")
        val nextCrowdId = (currentCountCrowds + 1).toLong()
        assertEquals(this.crowdDao.insert(crowd), nextCrowdId)
        assertEquals(this.crowdDao.count(), currentCountCrowds + 1)
    }

    @Test
    fun getAllCrowds() {
        val currentTime = Date().time
        this.insertOneCrowd()
        this.insertOneCrowd()
        val allCrowds = this.crowdDao.getAll()
        assertEquals(allCrowds.size, 2)
        allCrowds.forEach { crowd ->
            assertEquals(crowd.title, "Dec 31 2017 00:00:00")
            assertTrue(crowd.created >= currentTime)
            assertEquals(crowd.trackedImageUri, "/Picture/crowd_1.jpp")
        }
    }

    @Test
    fun getOneCrowdById() {
        this.insertOneCrowd()
        val liveDataCrowd = this.crowdDao.getById(1)
        assertNotNull(liveDataCrowd)
    }

    @Test
    fun getAllLiveData() {
        this.insertOneCrowd()
        val allLiveData = this.crowdDao.getAllLiveData()
        assertNotNull(allLiveData)
    }

}