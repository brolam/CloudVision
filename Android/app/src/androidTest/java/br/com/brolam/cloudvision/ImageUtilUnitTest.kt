package br.com.brolam.cloudvision

import android.content.Context
import android.support.test.InstrumentationRegistry
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.mocks.CameraMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Created by brenomarques on 12/01/2018.
 *
 */
class ImageUtilUnitTest {
    private lateinit var context: Context
    private lateinit var imageUtil: ImageUtil

    @Before
    fun setUp(){
        this.context = InstrumentationRegistry.getTargetContext()
        this.imageUtil = ImageUtil(this.context)
    }

    @Test
    fun saveOneImage(){
        val fileName = "testSaveOneImage.jpg"
        val cameraMock = CameraMock(this.context, false)
        this.imageUtil.save(fileName = fileName, bitmap = cameraMock.bitmapCrowd02 )
        val imageFile = File(this.imageUtil.storageDirPicture.absolutePath + "/$fileName")
        Assert.assertTrue(imageFile.exists())
    }
}