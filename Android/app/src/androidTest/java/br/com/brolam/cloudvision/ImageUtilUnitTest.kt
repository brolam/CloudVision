package br.com.brolam.cloudvision

import android.content.Context
import android.graphics.Bitmap
import android.support.test.InstrumentationRegistry
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.mocks.CameraMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by brenomarques on 12/01/2018.
 *
 */
class ImageUtilUnitTest {
    private lateinit var context: Context
    private lateinit var imageUtil: ImageUtil

    @Before
    fun setUp() {
        this.context = InstrumentationRegistry.getTargetContext()
        this.imageUtil = ImageUtil(this.context)
    }

    @Test
    fun saveOneImage() {
        val fileName = "testSaveOneImage.jpg"
        val cameraMock = CameraMock(this.context, false)
        this.imageUtil.save(fileName = fileName, bitmap = cameraMock.bitmapCrowd02)
        val imageFile = File(this.imageUtil.storageDirPicture.absolutePath + "/$fileName")
        Assert.assertTrue(imageFile.exists())
    }

    @Test
    fun getOneImage() {
        this.saveOneImage()
        val fileName = "testSaveOneImage.jpg"
        val image: Bitmap? = this.imageUtil.getImage(fileName = fileName)
        Assert.assertNotNull(image)
    }

    @Test
    fun getImageNotExists() {
        val fileName = "notExistsImage.jpg"
        val image: Bitmap? = this.imageUtil.getImage(fileName = fileName)
        Assert.assertNull(image)
    }

    @Test
    fun getOneImageAsync() {
        val spyOnCompleted = CountDownLatch(1)
        var expectedImage: Bitmap? = null
        this.saveOneImage()
        val fileName = "testSaveOneImage.jpg"
        this.imageUtil.getImage(fileName = fileName, onCompleted = { image: Bitmap? ->
            expectedImage = image
            spyOnCompleted.countDown()
        })
        spyOnCompleted.await(3, TimeUnit.SECONDS)
        Assert.assertNotNull(expectedImage)
    }

    @Test
    fun cropOneImage() {
        this.saveOneImage()
        val fileName = "testSaveOneImage.jpg"
        val image: Bitmap = this.imageUtil.getImage(fileName = fileName)!!
        val imageCropped = this.imageUtil.crop(
                bitmap = image,
                positionX = 200F,
                positionY = 200F,
                width = 100F,
                height = 100F,
                enlargeWidthInPercent = 10.0F,
                enlargeHeightInPercent = 10.0F
        )
        Assert.assertNotNull(imageCropped)
        Assert.assertEquals(imageCropped.width, 120)
        Assert.assertEquals(imageCropped.height, 120)
    }

    @Test
    fun cropAboveImageArea() {
        this.saveOneImage()
        val fileName = "testSaveOneImage.jpg"
        val image: Bitmap = this.imageUtil.getImage(fileName = fileName)!!
        val imageCropped = this.imageUtil.crop(
                bitmap = image,
                positionX = 0F,
                positionY = 0F,
                width = 100F,
                height = 100F,
                enlargeWidthInPercent = 10.0F,
                enlargeHeightInPercent = 10.0F
        )
        Assert.assertNotNull(imageCropped)
        Assert.assertEquals(imageCropped.width, 120)
        Assert.assertEquals(imageCropped.height, 120)
    }
}