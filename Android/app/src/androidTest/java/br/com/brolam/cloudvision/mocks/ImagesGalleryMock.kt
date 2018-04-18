package br.com.brolam.cloudvision.mocks

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.v4.content.FileProvider
import br.com.brolam.cloudvision.R
import java.io.File
import java.io.FileOutputStream

/**
* Created by brenomarques on 05/01/2018.
*/
class ImagesGalleryMock(context: Context, rawResource:Int = R.raw.crowd_test_02 ) {
    private val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val tempPictureUrl = storageDir.absolutePath + "/tempPicture.jpg"

    init {
        val streamFilePicture = context.resources.openRawResource(rawResource)
        streamFilePicture.use {
            val bitmapCrowd02 = BitmapFactory.decodeStream(it)
            val filePictureTemp = File(tempPictureUrl)
            val filePictureTempOut = FileOutputStream(filePictureTemp)
            bitmapCrowd02.compress(Bitmap.CompressFormat.JPEG, 85, filePictureTempOut)
            filePictureTempOut.flush()
            filePictureTempOut.close()
            val pictureUriOutput = FileProvider.getUriForFile(
                    context,
                    "br.com.brolam.cloudvision.fileprovider",
                    filePictureTemp)
            val resultData = Intent()
            resultData.data = pictureUriOutput
            val activityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
            Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(activityResult)
        }

    }

}