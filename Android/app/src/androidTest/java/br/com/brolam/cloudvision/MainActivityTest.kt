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
        val appContext = InstrumentationRegistry.getTargetContext()
        CameraMock(appContext)
        val cameraButton = onView(withId(R.id.fab))
        cameraButton.perform(click())
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
        this.takeOnePhotoByCamera()
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

    @Test
    fun selectOneCrowd(){
        this.newCrowdByCamera()
        onView( AssertsUtils.withIndex(withId(R.id.cardViewCrowd), 0)).perform(click())
        val facesActivity = onView(withId(R.id.activity_faces_layout))
        facesActivity.check(matches(isDisplayed()))
    }

    @Test
    fun deleteOneCrowd(){
        this.newCrowdByCamera()
        val recyclerView = onView(withId(R.id.recyclerView))
        val cardViewCrowd = onView(AssertsUtils.withIndex(withId(R.id.cardViewCrowd), 0))
        cardViewCrowd.perform(swipeLeft())
        recyclerView.check(matches(recyclerViewCountEqual(expect =  0)))
    }

    @Test
    fun raffleOnePerson(){
        selectOneCrowd()
        onView(withId(R.id.fabRaffle)).perform(click())
        try {
            Thread.sleep(10000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.textViewWinnersFacesTitle)).check(matches(withText("Winners")))
        onView(withId(R.id.textViewWinnersFacesAmount)).check(matches(withText("1")))
        onView(withId(R.id.flexboxLayoutWinnersFaces)).check(matches(isDisplayed()))
    }

}

