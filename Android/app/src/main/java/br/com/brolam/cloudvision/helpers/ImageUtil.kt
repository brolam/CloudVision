package br.com.brolam.cloudvision.helpers

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

/**
 * Created by brenomarques on 12/01/2018.
 *
 */
class ImageUtil(val context: Context){
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
}