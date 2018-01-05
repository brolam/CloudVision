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
class CameraMock(context: Context) {
    private val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val tempPictureUrl = storageDir.absolutePath + "/tempPicture.jpg"

    init {
        val streamFilePicture = context.getResources().openRawResource(R.raw.crowd_test_02)
        try {
            val bitmapCrowd02 = BitmapFactory.decodeStream(streamFilePicture)
            val filePictureTemp = File(tempPictureUrl)
            val filePictureTempOut = FileOutputStream(filePictureTemp);
            bitmapCrowd02.compress(Bitmap.CompressFormat.JPEG, 85, filePictureTempOut);
            filePictureTempOut.flush()
            filePictureTempOut.close()
            val bundle = Bundle()
            bundle.putParcelable("data", bitmapCrowd02)
            val resultData = Intent()
            resultData.putExtras(bundle)
            val activityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
            Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(activityResult)
        } finally {
            streamFilePicture.close()
        }

    }

}