package br.com.brolam.cloudvision

import android.os.Environment
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeLeft
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.intent.rule.IntentsTestRule
import br.com.brolam.cloudvision.asserts.AssertsUtils
import br.com.brolam.cloudvision.asserts.AssertsUtils.Companion.recyclerViewCount
import br.com.brolam.cloudvision.asserts.AssertsUtils.Companion.recyclerViewCountEqual
import br.com.brolam.cloudvision.mocks.CameraMock
import br.com.brolam.cloudvision.mocks.ImagesGalleryMock
import br.com.brolam.cloudvision.models.AppDatabase
import br.com.brolam.cloudvision.models.CrowdPersonEntity
import org.hamcrest.Matchers.not
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val mainActivity = IntentsTestRule(MainActivity::class.java)

    @Before
    fun setUpTests(){
        val context = mainActivity.activity
        //Clean Pictures
        val storageDirPicture =  context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        storageDirPicture.deleteRecursively()
        //Clean Database
        AppDatabase.getInstance(context).crowdDao().deleteAllCrowds()
    }

    @Test
    fun mainActivityStartWithoutError() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val recyclerView = onView(withId(R.id.recyclerView))
        Assert.assertEquals("br.com.brolam.cloudvision", appContext.packageName)
        recyclerView.check(matches(isDisplayed()))
    }

    @Test
    fun takeOnePhotoByCamera() {
        baseTakeOnePhotoByCamera()
    }

    @Test
    fun selectOnePhotoOnImagesGallery() {
        val appContext = InstrumentationRegistry.getTargetContext()
        ImagesGalleryMock(appContext)
        val showGalleryButton = onView(withId(R.id.action_gallery))
        showGalleryButton.perform(click())
    }

    @Test
    fun newCrowdByCamera() {
        baseNewCrowdByCamera()
    }

    @Test
    fun selectOneCrowd(){
        baseSelectOneCrowd()
    }

    @Test
    fun deleteOneCrowd(){
        this.baseNewCrowdByCamera()
        val recyclerView = onView(withId(R.id.recyclerView))
        val cardViewCrowd = onView(AssertsUtils.withIndex(withId(R.id.cardViewCrowd), 0))
        cardViewCrowd.perform(swipeLeft())
        recyclerView.check(matches(recyclerViewCountEqual(expect =  0)))
    }

    @Test
    fun raffleOnePerson(){
        baseSelectOneCrowd()
        onView(withId(R.id.fabRaffle)).perform(click())
        waitingRaffle()
        onView(withId(R.id.textViewWinnersFacesTitle)).check(matches(withText("Winners")))
        onView(withId(R.id.textViewWinnersFacesAmount)).check(matches(withText("1")))
        onView(withId(R.id.flexboxLayoutWinnersFaces)).check(matches(isDisplayed()))
    }

    @Test
    fun raffleOnePersonWithAllRafflesMade(){
        baseSelectOneCrowd()
        doRaffle(18)
        val exceptionPeopleListIsEmpty  = mainActivity.activity.getString(R.string.exception_all_raffles_been_made)
        onView(withId(R.id.fabRaffle)).perform(click())
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(withText(exceptionPeopleListIsEmpty)))
        onView(withId(R.id.textViewWinnersFacesAmount)).check(matches(withText("19")))
    }

    @Test
    fun raffleOnePersonWithOneFacePicture(){
        baseSelectOneCrowd()
        val people = doRaffle(17)
        Assert.assertEquals(1, people.filter { it.winnerPosition == 0 }.size)
        onView(withId(R.id.fabRaffle)).perform(click())
        waitingRaffle()
        onView(withId(R.id.textViewWinnersFacesAmount)).check(matches(withText("19")))
    }

    @Test
    fun tryAddPhotoWithoutFacesByPhotoLibrary(){
        val appContext = InstrumentationRegistry.getTargetContext()
        val recyclerView = onView(withId(R.id.recyclerView))
        ImagesGalleryMock(appContext, R.raw.photo_without_faces)
        val showGalleryButton = onView(withId(R.id.action_gallery))
        showGalleryButton.perform(click())
        val notValidPicture  = mainActivity.activity.getString(R.string.exception_not_valid_picture)
        recyclerView.check(matches(recyclerViewCountEqual(expect =  0)))
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(withText(notValidPicture)))
    }

    @Test
    fun zoomFace(){
        baseSelectOneCrowd()
        onView( AssertsUtils.withIndex(withId(R.id.faceItemView), 0)).perform(click())
        onView(withId(R.id.zoomFaceItemView)).check(matches(isDisplayed()))

    }

    private fun baseNewCrowdByCamera() {
        this.baseTakeOnePhotoByCamera()
        val facesActivity = onView(withId(R.id.activity_faces_layout))
        facesActivity.check(matches(isDisplayed()))
        onView(withId(R.id.imageViewTrackedImage)).check(matches(AssertsUtils.hasDrawable()))
        onView(withId(R.id.textViewTitle)).check(matches(not(withText(""))))
        onView(withId(R.id.textViewEveryOneFacesTitle)).check(matches(withText("Everyone")))
        onView(withId(R.id.textViewEveryOneFacesAmount)).check(matches(withText("19")))
        onView(withId(R.id.flexboxLayoutEveryOneFaces)).check(matches(isDisplayed()))
        pressBack()
        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(recyclerViewCount(greaterThan =  0)))
    }

    private fun baseSelectOneCrowd(){
        this.baseNewCrowdByCamera()
        onView( AssertsUtils.withIndex(withId(R.id.cardViewCrowd), 0)).perform(click())
        val facesActivity = onView(withId(R.id.activity_faces_layout))
        facesActivity.check(matches(isDisplayed()))
    }

    private fun baseTakeOnePhotoByCamera() {
        val appContext = InstrumentationRegistry.getTargetContext()
        CameraMock(appContext)
        val cameraButton = onView(withId(R.id.fab))
        cameraButton.perform(click())
    }

    private fun doRaffle(amount:Int): List<CrowdPersonEntity> {
        val appDataBase = AppDatabase.getInstance(mainActivity.activity)
        val crowd = appDataBase.crowdDao().getAll()[0]
        val people = appDataBase.crowdDao().getPeople(crowd.id)
        (0..amount).forEach {
            people[it].winnerPosition = it + 1
            appDataBase.crowdDao().updatePerson(people[it])
        }
        return people
    }

    private fun waitingRaffle() {
        try {
            Thread.sleep(10000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}

