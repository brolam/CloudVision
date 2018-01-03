package br.com.brolam.cloudvision

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import br.com.brolam.cloudvision.helpers.FacesDetectorHelper
import org.junit.Test
import android.support.test.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FacesDetectorUnitTest {
    lateinit var facesDetectorHelper: FacesDetectorHelper
    lateinit var bitmapCrowd01: Bitmap

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        this.facesDetectorHelper = FacesDetectorHelper(context)
        this.bitmapCrowd01 = BitmapFactory.decodeResource(context.resources, R.drawable.crowd_test_01)

    }

    @Test
    fun testTrackFacesCrowd01Count() {
        assertTrue(facesDetectorHelper.trackFaces(this.bitmapCrowd01))
        assertEquals(facesDetectorHelper.countFaces(), 9)
    }
}
