package br.com.brolam.cloudvision.helpers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File

/**
 * Created by brenomarques on 06/01/2018.
 *
 */

interface ImagePickerDelegate {
    fun onPickedOneImage(pikedBitmap: Bitmap): Boolean
}

class ImagePicker(
        private val activity: Activity,
        private val requestCodeCapture: Int,
        private val requestCodeSelect: Int) {
    private val tempPicture = "tempPicture"
    private val fileProvider = "br.com.brolam.cloudvision.fileprovider"

    fun selectOneImage() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        this.activity.startActivityForResult(galleryIntent, requestCodeSelect)
    }

    fun captureOneImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val pictureFileOutput = createTempPictureUri()
        val pictureUriOutput = FileProvider.getUriForFile(
                this.activity,
                fileProvider,
                pictureFileOutput
        )

        if (takePictureIntent.resolveActivity(this.activity.packageManager) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUriOutput)
            this.activity.startActivityForResult(takePictureIntent, requestCodeCapture)
        }
    }

    fun parseActivityForResult(delegate: ImagePickerDelegate, requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK) return false
        if (requestCode == this.requestCodeSelect && data == null) return false
        val pikedBitmap = when (requestCode) {
            this.requestCodeCapture -> BitmapFactory.decodeFile(getTempPictureUri())
            this.requestCodeSelect -> MediaStore.Images.Media.getBitmap(this.activity.contentResolver, data!!.data)
            else -> null
        }

        return if (pikedBitmap != null) {
            delegate.onPickedOneImage(pikedBitmap)
        } else {
            false
        }
    }

    private fun createTempPictureUri(): File {
        return File(getTempPictureUri())
    }

    private fun getTempPictureUri(): String {
        val storageDir = this.activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return storageDir.absolutePath + "/" + tempPicture + ".jpg"
    }

}
