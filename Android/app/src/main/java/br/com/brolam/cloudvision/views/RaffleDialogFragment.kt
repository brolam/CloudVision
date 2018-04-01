package br.com.brolam.cloudvision.views

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import br.com.brolam.cloudvision.R
import android.animation.ValueAnimator
import android.view.animation.AnimationUtils
import android.widget.LinearLayout

/**
 * Created by brenomarques on 30/03/2018.
 *
 */
class RaffleDialogFragment : android.support.v4.app.DialogFragment() {
    private var faceContainer: LinearLayout? = null

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

    override fun onResume() {
        super.onResume()
    }


    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    fun show(fragmentManager: FragmentManager?) {
        super.show(fragmentManager, tag)
    }

    fun raffleAnimator(chosenFacesBitmap: List<Bitmap>) {

        if ( this.faceContainer != null ) {
            val animatorSet = AnimatorSet()
            val x = IntArray(chosenFacesBitmap.size)
            (0 until chosenFacesBitmap.size).forEach({ x[it] = it })

            val facesAnimator = ValueAnimator.ofInt(*x)
            facesAnimator.duration = 6000
            facesAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                this.faceContainer!!.removeAllViews()
                chosenFacesBitmap.let { it ->
                    val faceItemView = layoutInflater.inflate(R.layout.view_face_item_big, this.faceContainer!!, false) as FaceItemView
                    faceItemView!!.setFaceDrawable(it!![animatedValue])
                    this.faceContainer!!.addView(faceItemView)

                }
                this.faceContainer!!.requestLayout()

            }

            var endAnimator = ValueAnimator.ofInt(0,1)
            endAnimator.duration = 10000
            endAnimator.addUpdateListener {animation ->
                val animatedValue = animation.animatedValue as Int
                if ( animatedValue == 1 ) this.dismiss()
                return@addUpdateListener
            }

            animatorSet.playSequentially(facesAnimator, endAnimator)
            //facesAnimator.startDelay = 1000
            animatorSet.start()
        }

    }

}