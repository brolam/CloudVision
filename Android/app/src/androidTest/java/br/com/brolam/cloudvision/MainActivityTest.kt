package br.com.brolam.cloudvision

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.intent.rule.IntentsTestRule
import br.com.brolam.cloudvision.asserts.AssertsUtils
import br.com.brolam.cloudvision.asserts.AssertsUtils.Companion.recyclerViewItems
import br.com.brolam.cloudvision.mocks.CameraMock
import br.com.brolam.cloudvision.mocks.ImagesGalleryMock
import org.hamcrest.Matchers.not

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val mainActivity = IntentsTestRule(MainActivity::class.java)

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
        onView(withId(R.id.textViewFacesTitle)).check(matches(withText("Everyone")))
        onView(withId(R.id.textViewFacesAmount)).check(matches(withText("19")))
        onView(withId(R.id.flexboxLayoutFaces)).check(matches(isDisplayed()))
        pressBack()
        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(recyclerViewItems(greaterThan =  0)))
    }

    @Test
    fun selectOneCrowd(){
        onView( AssertsUtils.withIndex(withId(R.id.cardViewCrowd), 1)).perform(click())
        val facesActivity = onView(withId(R.id.activity_faces_layout))
        facesActivity.check(matches(isDisplayed()))
    }

}

