package br.com.brolam.cloudvision.views

import android.animation.AnimatorSet
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import br.com.brolam.cloudvision.R
import android.animation.ValueAnimator
import android.content.Context
import android.widget.LinearLayout
import br.com.brolam.cloudvision.models.CvRecognizableItemEntity
import br.com.brolam.cloudvision.viewModels.CrowdViewModel
import android.content.pm.ActivityInfo



/**
 * Created by brenomarques on 30/03/2018.
 *
 */
class RaffleDialogFragment : android.support.v4.app.DialogFragment() {
    private var faceContainer: LinearLayout? = null
    private lateinit var crowdViewModel: CrowdViewModel
    private lateinit var raffledList: List<CvRecognizableItemEntity>
    class ExceptionAllRafflesBeenMade(message: String?) : Exception(message) {}

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_raffle, container, false);
        this.faceContainer = view!!.findViewById(R.id.faceContainer) as LinearLayout
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    fun show(context: Context, fragmentManager: FragmentManager?, crowdViewModel: CrowdViewModel) {
        this.crowdViewModel = crowdViewModel
        this.raffledList = this.crowdViewModel.createRaffledPeopleList()
        if (this.raffledList.isEmpty()) throw ExceptionAllRafflesBeenMade(context.getString(R.string.exception_all_raffles_been_made))
        super.show(fragmentManager, tag)
    }

    override fun onResume() {
        super.onResume()
        //lock screen to portrait
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        this.isCancelable = false
        raffleAnimator()
    }

    override fun onPause() {
        super.onPause()
        //set rotation to sensor dependent
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    private fun raffleAnimator() {
        if ( this.faceContainer != null ) {
            val animatorSet = AnimatorSet()
            val x = IntArray(raffledList.size)
            (0 until raffledList.size).forEach({ x[it] = it })

            val facesAnimator = ValueAnimator.ofInt(*x)
            facesAnimator.duration = 5000
            facesAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                this.faceContainer!!.removeAllViews()
                raffledList.let { it ->
                    val faceItemView = layoutInflater.inflate(R.layout.view_face_item_big, this.faceContainer!!, false) as FaceItemView
                    val person = it!![animatedValue]
                    faceItemView!!.setFaceDrawable( this.crowdViewModel.getPersonPicture(person.id) )
                    this.faceContainer!!.addView(faceItemView)
                }
                this.faceContainer!!.requestLayout()

            }

            var endAnimator = ValueAnimator.ofInt(0,1,1, 2)
            endAnimator.duration = 4000
            endAnimator.addUpdateListener {animation ->
                val animatedValue = animation.animatedValue as Int
                if ( animatedValue == 0) this.faceContainer!!.visibility =  View.GONE
                if ( animatedValue == 1) this.faceContainer!!.visibility =  View.VISIBLE
                if ( animatedValue == 2 ){
                    this.crowdViewModel.setWinner(raffledList.last())
                    this.dismiss()
                }
            }
            animatorSet.playSequentially(facesAnimator, endAnimator)
            animatorSet.start()
        }

    }
}