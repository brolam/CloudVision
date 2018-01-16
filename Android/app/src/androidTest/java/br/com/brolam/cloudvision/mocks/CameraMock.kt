package br.com.brolam.cloudvision.mocks

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import br.com.brolam.cloudvision.R
import java.io.File
import java.io.FileOutputStream

/**
 * Created by brenomarques on 05/01/2018.
 */
class CameraMock(context: Context, private var isDoActivityResult : Boolean = true) {
    private val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val tempPictureUrl = storageDir.absolutePath + "/tempPicture.jpg"
    lateinit var bitmapCrowd02: Bitmap

    init {
        context.resources.openRawResource(R.raw.crowd_test_02).use { streamFilePicture ->
            this.bitmapCrowd02 = BitmapFactory.decodeStream(streamFilePicture)
            val filePictureTemp = File(tempPictureUrl)
            val filePictureTempOut = FileOutputStream(filePictureTemp)
            this.bitmapCrowd02.compress(Bitmap.CompressFormat.JPEG, 85, filePictureTempOut)
            filePictureTempOut.flush()
            filePictureTempOut.close()
            if (isDoActivityResult) {
                val bundle = Bundle()
                bundle.putParcelable("data", this.bitmapCrowd02)
                val resultData = Intent()
                resultData.putExtras(bundle)
                val activityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
                Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(activityResult)
            }
        }
    }

}