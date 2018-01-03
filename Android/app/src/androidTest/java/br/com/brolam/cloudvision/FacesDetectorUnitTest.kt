package br.com.brolam.cloudvision

import android.content.Context
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
    lateinit var context: Context

    @Before
    fun setUp() {
        this.context = InstrumentationRegistry.getTargetContext()
        this.facesDetectorHelper = FacesDetectorHelper(context)
    }

    @Test
    fun testTrackFacesCrowd01Count() {
        var stream = this.context.getResources().openRawResource(R.raw.crowd_test_01)
        try {
            val bitmapCrowd01 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetectorHelper.trackFaces(bitmapCrowd01))
            assertEquals(facesDetectorHelper.countFaces(), 9)
        } finally {
            stream.close()
        }
    }

    @Test
    fun testTrackFacesCrowd02Count() {
        var stream = this.context.getResources().openRawResource(R.raw.crowd_test_02)
        try {
            val bitmapCrowd02 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetectorHelper.trackFaces(bitmapCrowd02))
            assertEquals(facesDetectorHelper.countFaces(), 19)
        } finally {
            stream.close()
        }
    }

    @Test
    fun testTrackFacesCrowd03Count() {
        var stream = this.context.getResources().openRawResource(R.raw.crowd_test_03)
        try {
            val bitmapCrowd03 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetectorHelper.trackFaces(bitmapCrowd03))
            assertEquals(facesDetectorHelper.countFaces(), 1)
        } finally {
            stream.close()
        }
    }
}
