package br.com.brolam.cloudvision

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import java.io.IOException
import java.io.File
import android.support.v4.content.FileProvider

const val REQUEST_IMAGE_CAPTURE = 5001
const val REQUEST_IMAGE_SELECT = 5002;

class MainActivity : AppCompatActivity() , View.OnClickListener  {
    private val tempPicture = "tempPicture"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_gallery -> {
                val galleryIntent = Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(galleryIntent, REQUEST_IMAGE_SELECT)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val imageView = this.findViewById<ImageView>(R.id.imageView)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(getTempPictureUri())
            imageView.setImageBitmap(imageBitmap)
        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    imageView.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onClick(view: View?) {
        if ( view == null ) return
        if ( view.equals(fab)){
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var pictureFileOutput = createTempPictureUri()
            val pictureUriOutput = FileProvider.getUriForFile(this,
                    "br.com.brolam.cloudvision.fileprovider",
                    pictureFileOutput)

            if (takePictureIntent.resolveActivity(packageManager) != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUriOutput);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createTempPictureUri(): File {
        return File(getTempPictureUri())
    }

    private fun getTempPictureUri(): String {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return storageDir.absolutePath + "/" + tempPicture + ".jpg"
    }
}
