package br.com.brolam.cloudvision

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule var mainActivity = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityStartWithoutErrorTest() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val cardsFragment = onView(withId(R.id.cardsFragment))
        Assert.assertEquals("br.com.brolam.cloudvision", appContext.packageName)
        cardsFragment.check(matches(isDisplayed()))
    }

}
