package br.com.brolam.cloudvision

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import br.com.brolam.cloudvision.viewModels.CrowdViewModel
import br.com.brolam.cloudvision.views.FaceItemView
import br.com.brolam.cloudvision.views.RaffleDialogFragment
import kotlinx.android.synthetic.main.activity_faces.*

class FacesActivity : AppCompatActivity(), CrowdViewModel.CrowdViewModelLifecycle, View.OnClickListener {

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

    override fun onCrowdPeopleUpdated() {
        this.imageViewTrackedImage.setImageBitmap(this.crowdViewModel.getTrackedImage())
        this.textViewTitle.text = this.crowdViewModel.getCrowd().title
        this.fillFlexboxLayoutWinnersFaces()
        this.fillFlexboxLayoutEveryOneFaces()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faces)
        setSupportActionBar(toolbar)
        this.crowdId = this.intent.getLongExtra(PARAM_CROWD_ID, 0)
        this.crowdViewModel = ViewModelProviders.of(this).get(CrowdViewModel::class.java)
        this.crowdViewModel.setCrowdPeopleObserve(crowdId, this)
        fabRaffle.setOnClickListener(this)
    }

    private fun fillFlexboxLayoutWinnersFaces() {
        if (this.crowdViewModel.getWinners().isEmpty()) {
            hideWinners()
            return
        }
        showWinners()
        flexboxLayoutWinnersFaces.removeAllViews()
        this.crowdViewModel.getWinners().map { winner ->
            val faceItemView = layoutInflater.inflate(R.layout.view_face_item, flexboxLayoutWinnersFaces, false) as FaceItemView
            val faceBitmap = this.crowdViewModel.getPersonPicture(winner.id)
            flexboxLayoutWinnersFaces.addView(faceItemView)
            faceItemView.setFaceDrawable(faceBitmap)
        }
        this.textViewWinnersFacesAmount.text = flexboxLayoutWinnersFaces.childCount.toString()
    }

    private fun showWinners() {
        linearLayoutWinnersFaces.visibility = View.VISIBLE
        flexboxLayoutWinnersFaces.visibility = View.VISIBLE
    }

    private fun hideWinners() {
        linearLayoutWinnersFaces.visibility = View.GONE
        flexboxLayoutWinnersFaces.visibility = View.GONE
    }

    private fun fillFlexboxLayoutEveryOneFaces() {
        flexboxLayoutEveryOneFaces.removeAllViews()
        this.crowdViewModel.getPeople().map { person ->
            val faceItemView = layoutInflater.inflate(R.layout.view_face_item, flexboxLayoutEveryOneFaces, false) as FaceItemView
            val faceBitmap = this.crowdViewModel.getPersonPicture(person.id)
            flexboxLayoutEveryOneFaces.addView(faceItemView)
            faceItemView.setFaceDrawable(faceBitmap)
        }
        this.textViewEveryOneFacesAmount.text = flexboxLayoutEveryOneFaces.childCount.toString()
    }

    override fun onClick(v: View?) {
        if (fabRaffle == v){
            try {
                raffleDialogFragment.show(this, supportFragmentManager, this.crowdViewModel)
            } catch (e: RaffleDialogFragment.ExceptionAllRafflesBeenMade) {
                Snackbar.make(fabRaffle, e.message!!, Snackbar.LENGTH_LONG).setAction(null, null).show()
            }
        }
    }
}
