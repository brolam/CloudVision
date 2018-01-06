package br.com.brolam.cloudvision

import android.content.Context
import android.graphics.BitmapFactory
import br.com.brolam.cloudvision.helpers.FacesDetectorHelper
import org.junit.Test
import android.support.test.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before

/**
 * Created by brenomarques on 02/01/2018.
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
    fun trackFacesCrowd01Count() {
        this.context.resources.openRawResource(R.raw.crowd_test_01).use { stream ->
            val bitmapCrowd01 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetectorHelper.trackFaces(bitmapCrowd01))
            assertEquals(facesDetectorHelper.countFaces(), 9)
        }
    }

    @Test
    fun trackFacesCrowd02Count() {
        this.context.resources.openRawResource(R.raw.crowd_test_02).use { stream ->
            val bitmapCrowd02 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetectorHelper.trackFaces(bitmapCrowd02))
            assertEquals(facesDetectorHelper.countFaces(), 19)
        }
    }

    @Test
    fun trackFacesCrowd03Count() {
        this.context.resources.openRawResource(R.raw.crowd_test_03).use { stream ->
            val bitmapCrowd03 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetectorHelper.trackFaces(bitmapCrowd03))
            assertEquals(facesDetectorHelper.countFaces(), 1)
        }
    }
}
