package br.com.brolam.cloudvision

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import br.com.brolam.cloudvision.viewModels.CrowdViewModel
import br.com.brolam.cloudvision.views.FaceItemView
import kotlinx.android.synthetic.main.activity_faces.*


class FacesActivity : AppCompatActivity() {
    private lateinit var crowdViewModel: CrowdViewModel

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
        val crowdId = this.intent.getLongExtra(PARAM_CROWD_ID, 0)
        this.crowdViewModel.getCrowdPeopleById(crowdId).observe(this, Observer { crowdPeople ->
            this.crowdViewModel.getTrackedImage(crowdPeople!!.crowd.trackedImageName)?.let { trackedImage ->
                this.fillFlexboxLayoutFaces(crowdViewModel.getImagesPeopleFaces(trackedImage,  crowdPeople!!.people ))
                imageViewTrackedImage.setImageBitmap(trackedImage)
                textViewTitle.text = crowdPeople!!.crowd.title
            }
        })

        fabRaffle.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }

    private fun fillFlexboxLayoutFaces(facesBitmap: List<Bitmap>) {
        flexboxLayoutFaces.removeAllViews()
        facesBitmap.map { face ->
            val faceItemView = layoutInflater.inflate(R.layout.view_face_item, flexboxLayoutFaces, false) as FaceItemView
            flexboxLayoutFaces.addView(faceItemView)
            faceItemView.setFaceDrawable(face)
        }
        this.textViewEveryOneFacesAmount.text = flexboxLayoutFaces.childCount.toString()
    }


}
