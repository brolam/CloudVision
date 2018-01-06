package br.com.brolam.cloudvision

import android.content.Context
import android.graphics.BitmapFactory
import br.com.brolam.cloudvision.helpers.FacesDetector
import org.junit.Test
import android.support.test.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before

/**
* Created by brenomarques on 02/01/2018.
*
*/
class FacesDetectorUnitTest {
    private lateinit var facesDetector: FacesDetector
    private lateinit var context: Context

    @Before
    fun setUp() {
        this.context = InstrumentationRegistry.getTargetContext()
        this.facesDetector = FacesDetector(context)
    }

    @Test
    fun trackFacesCrowd01Count() {
        this.context.resources.openRawResource(R.raw.crowd_test_01).use { stream ->
            val bitmapCrowd01 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetector.trackFaces(bitmapCrowd01))
            assertEquals(facesDetector.countFaces(), 9)
        }
    }

    @Test
    fun trackFacesCrowd02Count() {
        this.context.resources.openRawResource(R.raw.crowd_test_02).use { stream ->
            val bitmapCrowd02 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetector.trackFaces(bitmapCrowd02))
            assertEquals(facesDetector.countFaces(), 19)
        }
    }

    @Test
    fun trackFacesCrowd03Count() {
        this.context.resources.openRawResource(R.raw.crowd_test_03).use { stream ->
            val bitmapCrowd03 = BitmapFactory.decodeStream(stream)
            assertTrue(facesDetector.trackFaces(bitmapCrowd03))
            assertEquals(facesDetector.countFaces(), 1)
        }
    }
}
