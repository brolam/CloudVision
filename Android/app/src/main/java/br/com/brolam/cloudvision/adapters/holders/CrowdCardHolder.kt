package br.com.brolam.cloudvision.adapters.holders

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import br.com.brolam.cloudvision.models.CrowdEntity
import kotlinx.android.synthetic.main.holder_crowd_card.view.*;

/**
 * Created by brenomarques on 13/03/2018.
 *
 */
class CrowdCardHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(crowd: CrowdEntity, trackedImage: Bitmap?) {
        val imageViewTrackedImage = itemView.imageViewTrackedImage
        val title = itemView.textView_title
        title.text = crowd.title
        imageViewTrackedImage.setImageBitmap(trackedImage)
        this.itemView.setOnClickListener { v: View? ->
            title.text = "123"
        }
    }
}