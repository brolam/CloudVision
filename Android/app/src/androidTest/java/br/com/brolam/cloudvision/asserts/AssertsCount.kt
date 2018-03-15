package br.com.brolam.cloudvision.asserts

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Created by brenomarques on 12/03/2018.
 *
 */
class AssertsCount {
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
    }
}