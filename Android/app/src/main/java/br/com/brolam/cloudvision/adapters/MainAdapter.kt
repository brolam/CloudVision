package br.com.brolam.cloudvision.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView.Adapter
import br.com.brolam.cloudvision.R
import br.com.brolam.cloudvision.adapters.holders.CrowdCardHolder
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.models.CvImageEntity

/**
 * Created by brenomarques on 13/03/2018.
 *
 */
class MainAdapter(private val mainAdapterListener: MainAdapterListener) : Adapter<CrowdCardHolder>() {
    private val imageUtil = ImageUtil(mainAdapterListener.getContext())
    var crowds: List<CvImageEntity> = ArrayList()

    interface MainAdapterListener {
        fun onSelectOneCrowd(crowd: CvImageEntity)
        fun getContext(): Context
    }

    fun setCrows(crowds: List<CvImageEntity>) {
        this.crowds = crowds
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(crowdCardHolder: CrowdCardHolder?, position: Int) {
        if (crowdCardHolder == null) return
        val crowd = crowds[position]
        crowdCardHolder.bindView(crowd, null)
        imageUtil.getImage(crowd.trackedImageName, {
            crowdCardHolder.bindView(crowd, it)
        })
        crowdCardHolder.itemView.setOnClickListener {
            mainAdapterListener.onSelectOneCrowd(crowd)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CrowdCardHolder {
        val view = LayoutInflater.from(mainAdapterListener.getContext()).inflate(R.layout.holder_crowd_card, parent, false)
        return CrowdCardHolder(view)
    }

    override fun getItemCount(): Int {
        return crowds.size
    }
}