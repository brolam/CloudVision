package br.com.brolam.cloudvision.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

/**
 * Created by brenomarques on 12/01/2018.
 *
 */
class ImageUtil(val context: Context) {
    val storageDirPicture = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    fun save(fileName: String, bitmap: Bitmap) {
        val imageUri = storageDirPicture.absolutePath + "/$fileName"
        val fileImage = File(imageUri)
        val fileImageOut = FileOutputStream(fileImage)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileImageOut)
        fileImageOut.run {
            flush()
            close()
        }
    }

    fun delete(fileImageName: String) {
        val imageUri = storageDirPicture.absolutePath + "/$fileImageName"
        val fileImage = File(imageUri)
        fileImage.delete()
    }

    fun getImage(fileName: String): Bitmap? {
        val fullPathFileName = storageDirPicture.absolutePath + "/$fileName"
        if (File(fullPathFileName).exists())
            return BitmapFactory.decodeFile(fullPathFileName)
        return null
    }

    fun getImage(fileName: String, onCompleted: (Bitmap?) -> Unit) {
        class GetImageAsyncTask : AsyncTask<Void, Void, Bitmap?>() {
            override fun doInBackground(vararg params: Void?): Bitmap? {
                return getImage(fileName)
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                onCompleted(result)
            }
        }
        GetImageAsyncTask().execute()
    }

    fun crop(bitmap: Bitmap, positionX: Float, positionY: Float, width: Float, height: Float, enlargeWidthInPercent: Float, enlargeHeightInPercent: Float): Bitmap {
        val enlargedX = ((width * enlargeWidthInPercent) / 100.00)
        val enlargedY = ((height * enlargeHeightInPercent) / 100.00)
        val newX = if (positionX - enlargedX >= 1) (positionX - enlargedX).toInt() else 1
        val newY = if (positionY - enlargedY >= 1) (positionY - enlargedY).toInt() else 1
        var newWidth = (width + (enlargedX * 2)).toInt()
        var newHeight = (height + (enlargedY * 2)).toInt()
        //Check if width was exceeds
        if ((newX + newWidth) > bitmap.width) newWidth -= ((newX + newWidth) - bitmap.width)
        //Check if height was exceeds
        if ((newY + newHeight) > bitmap.height) newHeight -= ((newY + newHeight) - bitmap.height)
        return Bitmap.createBitmap(bitmap, newX, newY, newWidth, newHeight)
    }

    fun resizeImage(bitmapOrigin: Bitmap, newSize: Float): Bitmap {
        val scale = newSize / bitmapOrigin.width
        val newHeight = bitmapOrigin.height * scale
        return Bitmap.createScaledBitmap(
                bitmapOrigin,
                newSize.toInt(),
                newHeight.toInt(),
                true
        )

    }
}