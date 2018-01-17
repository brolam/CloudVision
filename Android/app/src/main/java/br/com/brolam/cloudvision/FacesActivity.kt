package br.com.brolam.cloudvision

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import br.com.brolam.cloudvision.helpers.FacesDetector
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.viewModels.CrowdViewModel
import br.com.brolam.cloudvision.views.FaceItemView
import com.google.android.gms.vision.face.Face
import kotlinx.android.synthetic.main.activity_faces.*


class FacesActivity : AppCompatActivity() {
    private lateinit var crowdViewModel: CrowdViewModel
    private lateinit var imageUtil: ImageUtil

    companion object {
        private const val PARAM_CROWD_ID = "crowd_id"
        fun show(activity: Activity, crowdId: Long) {
            val intent = Intent(activity, FacesActivity::class.java)
            intent.putExtra(PARAM_CROWD_ID, crowdId)
            activity.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faces)
        setSupportActionBar(toolbar)
        this.crowdViewModel = ViewModelProviders.of(this).get(CrowdViewModel::class.java)
        this.imageUtil = ImageUtil(this)
        val crowdId = this.intent.getLongExtra(PARAM_CROWD_ID, 0)
        this.crowdViewModel.getCrowdById(crowdId).observe(this, Observer { crowd ->
            this.imageUtil.getImage(crowd!!.trackedImageName)?.let { trackedImage ->
                val facesDetector = FacesDetector(this)
                facesDetector.trackFaces(trackedImage)
                val trackingFaces = facesDetector.trackingFaces
                this.fillFlexboxLayoutFaces(trackedImage, trackingFaces)
                imageViewTrackedImage.setImageBitmap(trackedImage)
                textViewTitle.text = crowd.title
            }
        })


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }

    private fun fillFlexboxLayoutFaces(trackedImage: Bitmap, trackingFaces: SparseArray<Face>) {
        val facesBitmap = mutableListOf<Bitmap>()
        for (index in 0 until trackingFaces.size()) {
            val face = trackingFaces.valueAt(index)
            val faceBitmap = this.crop(trackedImage, face, 15.toFloat(), 15.toFloat())
            facesBitmap.add(faceBitmap)
        }
        flexboxLayoutFaces.removeAllViews()
        facesBitmap.map { face ->
            val faceItemView = layoutInflater.inflate(R.layout.view_face_item, flexboxLayoutFaces, false) as FaceItemView
            flexboxLayoutFaces.addView(faceItemView)
            faceItemView.setFaceDrawable(face)
        }
        this.textViewFacesAmount.text = flexboxLayoutFaces.childCount.toString()
    }

    private fun crop(trackedImage: Bitmap, face: Face, enlargeWidthInPercent: Float, enlargeHeightInPercent: Float): Bitmap {
        val enlargeX = ((face.width * enlargeWidthInPercent) / 100.00)
        val enlargeY = ((face.height * enlargeHeightInPercent) / 100.00)
        val positionX = if (face.position.x - enlargeX >= 1) (face.position.x - enlargeX).toInt() else 1
        val positionY = if (face.position.y - enlargeY >= 1) (face.position.y - enlargeY).toInt() else 1
        val width = (face.width + (enlargeX * 2)).toInt()
        val height = (face.height + (enlargeY * 2)).toInt()

        val croppedBitmap = Bitmap.createBitmap(
                trackedImage,
                positionX,
                positionY,
                width,
                height);
        return croppedBitmap

    }

}
