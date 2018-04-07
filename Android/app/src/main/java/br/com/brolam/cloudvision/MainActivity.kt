package br.com.brolam.cloudvision

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.graphics.Bitmap
import br.com.brolam.cloudvision.viewModels.CrowdsViewModel
import br.com.brolam.cloudvision.helpers.ImagePicker
import br.com.brolam.cloudvision.helpers.ImagePickerDelegate
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import br.com.brolam.cloudvision.adapters.MainAdapter
import br.com.brolam.cloudvision.adapters.holders.SwipeToDeleteCallback
import br.com.brolam.cloudvision.models.CrowdEntity


class MainActivity : AppCompatActivity(), View.OnClickListener, ImagePickerDelegate, Observer<List<CrowdEntity>>, MainAdapter.MainAdapterListener {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 5001
        const val REQUEST_IMAGE_SELECT = 5002
    }

    private val imagePiker = ImagePicker(this, REQUEST_IMAGE_CAPTURE, REQUEST_IMAGE_SELECT)
    private lateinit var crowdsViewModel: CrowdsViewModel
    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        this.mainAdapter = MainAdapter(this)
        this.recyclerView.layoutManager = LinearLayoutManager(this)
        this.recyclerView.adapter = mainAdapter
        this.crowdsViewModel = ViewModelProviders.of(this).get(CrowdsViewModel::class.java);
        this.crowdsViewModel.getAllCrowds().observe(this, this )
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val crowd = mainAdapter.crowds[viewHolder.adapterPosition]
                crowdsViewModel.deleteCrowd(crowd)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onPickedOneImage(pikedBitmap: Bitmap): Boolean {
        this.crowdsViewModel.insertCrowd(pikedBitmap, { crowdId: Long ->
            FacesActivity.show(this, crowdId)
        })
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_gallery -> {
                this.imagePiker.selectOneImage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this.imagePiker.parseActivityForResult(this, requestCode, resultCode, data)) return
    }

    override fun onClick(view: View?) {
        if (view == null) return
        if (view == fab) {
            this.imagePiker.captureOneImage()
        }
    }

    override fun onChanged(crowds: List<CrowdEntity>?) {
        if ( crowds != null ){
            this.mainAdapter.setCrows(crowds)
        }
    }

    override fun onSelectOneCrowd(crowd: CrowdEntity) {
        FacesActivity.show(this, crowd.id)
    }

    override fun getContext(): Context {
        return this
    }

}
