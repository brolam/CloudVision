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
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import br.com.brolam.cloudvision.adapters.MainAdapter
import br.com.brolam.cloudvision.adapters.holders.SwipeToDeleteCallback
import br.com.brolam.cloudvision.models.CrowdEntity
import android.os.Parcelable

class MainActivity : AppCompatActivity(), View.OnClickListener, ImagePickerDelegate, Observer<List<CrowdEntity>>, MainAdapter.MainAdapterListener {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 5001
        const val REQUEST_IMAGE_SELECT = 5002
        const val RECYCLER_VIEW_STATE = "recycler_view_state"
    }

    private val imagePiker = ImagePicker(this, REQUEST_IMAGE_CAPTURE, REQUEST_IMAGE_SELECT)
    private lateinit var crowdsViewModel: CrowdsViewModel
    private lateinit var mainAdapter: MainAdapter
    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        this.mainAdapter = MainAdapter(this)
        this.recyclerView.layoutManager = LinearLayoutManager(this)
        this.recyclerView.adapter = mainAdapter
        this.crowdsViewModel = ViewModelProviders.of(this).get(CrowdsViewModel::class.java)
        this.crowdsViewModel.getAllCrowds().observe(this, this)
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val crowd = mainAdapter.crowds[viewHolder.adapterPosition]
                crowdsViewModel.deleteCrowd(crowd)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        if (this.crowdsViewModel.hasBackgroundProcess()) this.showProcessBar() else this.hideProcessBar()

    }

    override fun onPickedOneImage(pikedBitmap: Bitmap): Boolean {
        this.showProcessBar()
        this.crowdsViewModel.insertCrowd(pikedBitmap,
                onCompleted = { crowdId ->
                    this.hideProcessBar()
                    this.showInsertedCrowd()
                    FacesActivity.show(this, crowdId)
                },
                onError = { messageId ->
                    this.hideProcessBar()
                    showSnackBar(messageId)
                })
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!parseHasBackgroundProcess()) return false
        return when (item.itemId) {
            R.id.action_gallery -> {
                this.imagePiker.selectOneImage()
                true
            }
            R.id.action_camera -> {
                this.imagePiker.captureOneImage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this.imagePiker.parseActivityForResult(this, requestCode, resultCode, data)) return
    }

    override fun onClick(view: View?) {
        if (!parseHasBackgroundProcess()) return
        if (view == null) return
        if (view == fab) {
            this.imagePiker.captureOneImage()
        }
    }

    override fun onChanged(crowds: List<CrowdEntity>?) {
        if (crowds != null) {
            this.mainAdapter.setCrows(crowds)
            this.restoreRecyclerViewState()
        }
    }

    override fun onSelectOneCrowd(crowd: CrowdEntity) {
        if (!parseHasBackgroundProcess()) return
        FacesActivity.show(this, crowd.id)
    }

    override fun getContext(): Context {
        return this
    }

    private fun showInsertedCrowd() {
        this.recyclerView.scrollToPosition(0)
    }

    private fun showProcessBar() {
        fab.hide()
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProcessBar() {
        fab.show()
        progressBar.visibility = View.GONE
    }

    private fun parseHasBackgroundProcess(): Boolean {
        if (this.crowdsViewModel.hasBackgroundProcess()) {
            showSnackBar(R.string.exception_current_process_not_finished)
            return false
        }
        return true
    }

    private fun showSnackBar(messageId: Int) {
        Snackbar.make(fab, getString(messageId), Snackbar.LENGTH_LONG).setAction(null, null).show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(RECYCLER_VIEW_STATE, recyclerView.layoutManager.onSaveInstanceState())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState?.containsKey(RECYCLER_VIEW_STATE)!!) {
            this.recyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE)
        }

    }

    private fun restoreRecyclerViewState() {
        assert(this.recyclerViewState != null)
        recyclerView.layoutManager.onRestoreInstanceState(this.recyclerViewState)
        this.recyclerViewState = null

    }
}
