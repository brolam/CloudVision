package br.com.brolam.cloudvision.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Environment
import android.text.format.DateUtils
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.models.AppDatabase
import br.com.brolam.cloudvision.models.CrowdEntity
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by brenomarques on 08/01/2018.
 *
 */
class CrowdViewModel(application: Application) : AndroidViewModel(application) {
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)
    private val crowdDao = this.appDatabase.crowdDao()
    private val imageUtil = ImageUtil(application)

    fun insertCrowd(trackedImageBitmap: Bitmap, onCompleted: (Long) -> Unit) {
        AsyncTask.execute {
            val created = Date().time
            val title = DateUtils.formatDateTime(
                    this.getApplication(),
                    created,
                    DateUtils.FORMAT_SHOW_DATE + DateUtils.FORMAT_ABBREV_MONTH + DateUtils.FORMAT_SHOW_TIME )
            val trackedImageName = "/crowd_$created.jpg"
            this.imageUtil.save(trackedImageName, trackedImageBitmap)
            val crowdEntity = CrowdEntity(title = title, trackedImageName = trackedImageName, created = created)
            val crowdId = this.crowdDao.insert(crowdEntity)
            if (onCompleted != null) onCompleted(crowdId)
        }
    }

    fun getCrowdById(id: Long): LiveData<CrowdEntity> {
        return this.crowdDao.getById(id)
    }

}