package br.com.brolam.cloudvision.asserts

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Created by brenomarques on 12/03/2018.
 *
 */
class AssertsUtils {
    companion object {
        fun recyclerViewItems(greaterThan: Int): Matcher<View> {
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("RecyclerView with item count is not greater the : $greaterThan")
                }

                override fun matchesSafely(item: RecyclerView?): Boolean {
                    if ( ( item == null ) || (item?.adapter == null ) ) return false;
                    return item.adapter.itemCount > greaterThan
                }
            }
        }

        fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
            return object : TypeSafeMatcher<View>() {
                internal var currentIndex = 0

                override fun describeTo(description: Description) {
                    description.appendText("with index: ")
                    description.appendValue(index)
                    matcher.describeTo(description)
                }

                override fun matchesSafely(view: View): Boolean {
                    return matcher.matches(view) && currentIndex++ == index
                }
            }
        }

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
    }


}