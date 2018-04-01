package br.com.brolam.cloudvision

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.brolam.cloudvision.viewModels.CrowdViewModel
import br.com.brolam.cloudvision.views.FaceItemView
import br.com.brolam.cloudvision.views.RaffleDialogFragment
import kotlinx.android.synthetic.main.activity_faces.*


class FacesActivity : AppCompatActivity() {
    private var crowdId: Long = 0
    private lateinit var crowdViewModel: CrowdViewModel
    private val raffleDialogFragment = RaffleDialogFragment()

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
        this.crowdId = this.intent.getLongExtra(PARAM_CROWD_ID, 0)
        this.crowdViewModel = ViewModelProviders.of(this).get(CrowdViewModel::class.java)
        this.crowdViewModel.getCrowdPeopleById(crowdId).observe(this, Observer { crowdPeople ->
            this.crowdViewModel.getTrackedImage(crowdPeople!!.crowd.trackedImageName)?.let { trackedImage ->
                val imagesPeopleFaces = crowdViewModel.getImagesPeopleFaces(trackedImage, crowdPeople!!.people)
                this.fillFlexboxLayoutFaces(imagesPeopleFaces)
                imageViewTrackedImage.setImageBitmap(trackedImage)
                textViewTitle.text = crowdPeople!!.crowd.title

            }
        })

        fabRaffle.setOnClickListener { view ->
            //this.raffleDialogFragment.doRaffle(supportFragmentManager, "dialog");
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show()
            this.crowdViewModel.raffleOnePerson(crowdId,
                    {
                        fabRaffle.hide()
                        raffleDialogFragment.show(supportFragmentManager)

                    }, {chosenFacesBitmap ->
                raffleDialogFragment.raffleAnimator(chosenFacesBitmap)
            })
        }

    }

    private fun fillFlexboxLayoutFaces(facesBitmap: List<Bitmap>) {
        flexboxLayoutEveryOneFaces.removeAllViews()
        facesBitmap.map { face ->
            val faceItemView = layoutInflater.inflate(R.layout.view_face_item, flexboxLayoutEveryOneFaces, false) as FaceItemView
            flexboxLayoutEveryOneFaces.addView(faceItemView)
            faceItemView.setFaceDrawable(face)
        }
        this.textViewEveryOneFacesAmount.text = flexboxLayoutEveryOneFaces.childCount.toString()
    }


}
