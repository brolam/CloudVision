package br.com.brolam.cloudvision

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import android.support.test.InstrumentationRegistry
import br.com.brolam.cloudvision.models.*
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
    fun setDown() {
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
    fun deleteOneCrowd() {
        insertCrowdWithPeople();
        val allCrowds = this.crowdDao.getAll()
        allCrowds.forEach { crowd ->
            this.crowdDao.deleteOneCrowd(crowd)
            assertEquals(0, this.crowdDao.countPeople(crowd.id))
        }
        assertEquals(0, this.crowdDao.count())
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
    fun insertCrowdWithPeople() {
        val crowd = CrowdEntity(
                title = "Dec 31 2017 00:00:00",
                created = Date().time,
                trackedImageName = "/Picture/crowd_1.jpp"
        )
        val crowdId = this.crowdDao.insert(crowd)
        val people = listOf(
                CrowdPersonEntity(
                        crowdId = crowdId,
                        insertedOrder = 1,
                        facePositionX = 100f,
                        facePositionY = 200f,
                        faceWidth = 101f,
                        faceHeight = 102f
                ),
                CrowdPersonEntity(
                        crowdId = crowdId,
                        insertedOrder = 2,
                        facePositionX = 100f,
                        facePositionY = 200f,
                        faceWidth = 101f,
                        faceHeight = 102f
                )
        )
        this.crowdDao.insert(people)
        assertEquals(1, this.crowdDao.count())
        assertEquals(2, this.crowdDao.countPeople(crowdId))

    }

    @Test
    fun getOneCrowdLiveDataById() {
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
    fun getCrowdPeopleLiveData() {
        val spyOnCompleted = CountDownLatch(1)
        val currentTime = Date().time
        this.insertCrowdWithPeople()
        var expectedCrowdPeople: CrowdPeopleEntity? = null
        val observer = Observer<CrowdPeopleEntity>(function = { crowdPeopleEntity ->
            expectedCrowdPeople = crowdPeopleEntity
        })
        this.crowdDao.getCrowdPeopleById(id = 1).observeForever(observer)
        spyOnCompleted.await(2, TimeUnit.SECONDS)
        assertNotNull(expectedCrowdPeople)
        this.assertCrowdEntity(expectedCrowdPeople!!.crowd, currentTime)
        this.assertCrowdPersonEntity(expectedCrowdPeople!!.people[0], 1, 1)
        this.assertCrowdPersonEntity(expectedCrowdPeople!!.people[1], 1, 2)
    }

    @Test
    fun getAllCrowdsLiveData() {
        val spyOnCompleted = CountDownLatch(1)
        val currentTime = Date().time
        this.insertOneCrowd()
        var expectedCrowdEntities: List<CrowdEntity>? = null
        val observer = Observer<List<CrowdEntity>>(function = { crowdEntities ->
            expectedCrowdEntities = crowdEntities
        })
        this.crowdDao.getAllOrderedByCreated().observeForever(observer)
        spyOnCompleted.await(2, TimeUnit.SECONDS)
        assertNotNull(expectedCrowdEntities)
        this.assertCrowdEntity(expectedCrowdEntities!![0], currentTime)
    }

    @Test
    fun updateOneCrowdPerson(){
        insertCrowdWithPeople()
        var people = this.crowdDao.getPeople(1)
        people[0].winnerPosition = 1
        crowdDao.updatePerson(people[0])
        people = this.crowdDao.getPeople(1)
        assertEquals(1, people[0].winnerPosition)
    }

    private fun assertCrowdEntity(crowd: CrowdEntity, currentTime: Long) {
        assertEquals("Dec 31 2017 00:00:00", crowd.title)
        assertTrue(crowd.created >= currentTime)
        assertEquals("/Picture/crowd_1.jpp", crowd.trackedImageName)
    }

    private fun assertCrowdPersonEntity(crowdPerson: CrowdPersonEntity, crowdId: Long, insertedOrder: Int) {
        assertEquals(insertedOrder, crowdPerson.insertedOrder)
        assertEquals(crowdId, crowdPerson.crowdId)
        assertEquals(100f, crowdPerson.facePositionX)
        assertEquals(200f, crowdPerson.facePositionY)
        assertEquals(101f, crowdPerson.faceWidth)
        assertEquals(102f, crowdPerson.faceHeight)
    }

}