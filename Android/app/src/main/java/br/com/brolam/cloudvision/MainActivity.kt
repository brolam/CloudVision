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
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import br.com.brolam.cloudvision.adapters.MainAdapter
import br.com.brolam.cloudvision.models.CrowdEntity


class MainActivity : AppCompatActivity(), View.OnClickListener, ImagePickerDelegate, Observer<List<CrowdEntity>> {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 5001
        const val REQUEST_IMAGE_SELECT = 5002
    }

    private val imagePiker = ImagePicker(this, REQUEST_IMAGE_CAPTURE, REQUEST_IMAGE_SELECT)
    private lateinit var crowdsViewModel: CrowdsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        this.crowdsViewModel = ViewModelProviders.of(this).get(CrowdsViewModel::class.java);
        this.crowdsViewModel.getAllCrowds().observe(this, this )
    }

    override fun onPickedOneImage(pikedBitmap: Bitmap): Boolean {
        //val imageView = this.findViewById<ImageView>(R.id.imageView)
        //imageView.setImageBitmap(pikedBitmap)
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
        recyclerView.layoutManager = LinearLayoutManager(this)
        if ( crowds != null ) recyclerView.adapter = MainAdapter(crowds, this)
    }

}
