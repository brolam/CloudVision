package br.com.brolam.cloudvision

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.support.test.runner.AndroidJUnit4
import br.com.brolam.cloudvision.models.AppDatabase
import br.com.brolam.cloudvision.models.CrowdDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import android.support.test.InstrumentationRegistry
import br.com.brolam.cloudvision.models.CrowdEntity
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
                trackedImageName = "/Picture/crowd_1.jpp")
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
        allCrowds.forEach { crowd -> assertCrowdEntity(crowd, currentTime) }
    }

    @Test
    fun getOneCrowdById() {
        val spyOnCompleted = CountDownLatch(1)
        val currentTime = Date().time
        this.insertOneCrowd()
        var expectedCrowdEntity: CrowdEntity? = null
        val observer = Observer<CrowdEntity>(function = { crowdEntity ->
            expectedCrowdEntity = crowdEntity
        })
        this.crowdDao.getById(1).observeForever(observer)
        spyOnCompleted.await(2, TimeUnit.SECONDS)
        assertNotNull(expectedCrowdEntity)
        this.assertCrowdEntity(expectedCrowdEntity!!, currentTime)
    }

    @Test
    fun getAllLiveData() {
        val spyOnCompleted = CountDownLatch(1)
        val currentTime = Date().time
        this.insertOneCrowd()
        var expectedCrowdEntities: List<CrowdEntity>? = null
        val observer = Observer<List<CrowdEntity>>(function = { crowdEntities ->
            expectedCrowdEntities = crowdEntities
        })
        this.crowdDao.getAllLiveData().observeForever(observer)
        spyOnCompleted.await(2, TimeUnit.SECONDS)
        assertNotNull(expectedCrowdEntities)
        this.assertCrowdEntity(expectedCrowdEntities!![0], currentTime)
    }

    private fun assertCrowdEntity(crowd: CrowdEntity, currentTime: Long) {
        assertEquals(crowd.title, "Dec 31 2017 00:00:00")
        assertTrue(crowd.created >= currentTime)
        assertEquals(crowd.trackedImageName, "/Picture/crowd_1.jpp")
    }

}