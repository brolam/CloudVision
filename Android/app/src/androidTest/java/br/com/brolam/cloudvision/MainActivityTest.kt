package br.com.brolam.cloudvision

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import android.widget.ImageView
import br.com.brolam.cloudvision.mocks.CameraMock
import org.hamcrest.Description


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val mainActivity = IntentsTestRule(MainActivity::class.java)

    fun hasDrawable(): BoundedMatcher<View, ImageView> {
        return object : BoundedMatcher<View, ImageView>(ImageView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has drawable")
            }

            public override fun matchesSafely(imageView: ImageView): Boolean {
                return imageView.drawable != null
            }
        }
    }

    @Test
    fun mainActivityStartWithoutErrorTest() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val cardsFragment = onView(withId(R.id.cardsFragment))
        Assert.assertEquals("br.com.brolam.cloudvision", appContext.packageName)
        cardsFragment.check(matches(isDisplayed()))
    }

    @Test
    fun takeOnePhotoTest() {
        val appContext = InstrumentationRegistry.getTargetContext()
        CameraMock(appContext)
        val cameraButton = onView(withId(R.id.fab))
        cameraButton.perform(click())
        onView(withId(R.id.imageView)).check(matches(hasDrawable()))
    }
}

