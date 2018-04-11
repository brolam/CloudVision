package br.com.brolam.cloudvision.views

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import br.com.brolam.cloudvision.R
import android.support.v7.app.AppCompatActivity

/**
 * Created by brenomarques on 30/03/2018.
 *
 */
class ZoomFaceDialogFragment : android.support.v4.app.DialogFragment() {
    var faceBitmap: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_zoom_face, container, false);
        faceBitmap?.let {
            val zoomFaceItemView = view!!.findViewById<FaceItemView>(R.id.zoomFaceItemView)
            zoomFaceItemView.setFaceDrawable(it)
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        savedInstanceState?.let {
            it.getParcelable<Bitmap>("faceBitmap")?.let {
                this.faceBitmap = it
            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    fun show(activity: AppCompatActivity, faceBitmap: Bitmap) {
        this.faceBitmap = faceBitmap
        super.show(activity.supportFragmentManager, "zoomFaceDialogFragment")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            outState.putParcelable("faceBitmap", this.faceBitmap)
        }
    }

}