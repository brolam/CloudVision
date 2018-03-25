package br.com.brolam.cloudvision.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.os.AsyncTask
import android.text.format.DateUtils
import br.com.brolam.cloudvision.helpers.FacesDetector
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.models.AppDatabase
import br.com.brolam.cloudvision.models.CrowdEntity
import br.com.brolam.cloudvision.models.CrowdPeopleEntity
import br.com.brolam.cloudvision.models.CrowdPersonEntity
import java.util.*

/**
 * Created by brenomarques on 08/01/2018.
 *
 */
class CrowdsViewModel(application: Application) : AndroidViewModel(application) {
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)
    private val crowdDao = this.appDatabase.crowdDao()
    private val imageUtil = ImageUtil(application)

    fun getAllCrowds(): LiveData<List<CrowdEntity>> {
        return this.crowdDao.getAllLiveData()
    }

    fun insertCrowd(trackedImageBitmap: Bitmap, onCompleted: (Long) -> Unit) {
        AsyncTask.execute {
            val facesDetector = FacesDetector(this.getApplication())
            facesDetector.trackFaces(trackedImageBitmap)
            val created = Date().time
            val title = DateUtils.formatDateTime(
                    this.getApplication(),
                    created,
                    DateUtils.FORMAT_SHOW_DATE + DateUtils.FORMAT_ABBREV_MONTH + DateUtils.FORMAT_SHOW_TIME )

            val trackingFaces = facesDetector.trackingFaces

            val trackedImageName = "/crowd_$created.jpg"
            this.imageUtil.save(trackedImageName, trackedImageBitmap)

            this.appDatabase.runInTransaction {
                val crowdEntity = CrowdEntity(title = title, trackedImageName = trackedImageName, created = created)
                val crowdId = this.crowdDao.insert(crowdEntity)
                val crowdPeople = mutableListOf<CrowdPersonEntity>()
                for (index in 0 until trackingFaces.size()) {
                    val face = trackingFaces.valueAt(index)
                    val crowdPerson = CrowdPersonEntity(
                            crowdId = crowdId,
                            insertedOrder = index,
                            faceWidth = face.width,
                            faceHeight = face.height,
                            facePositionX = face.position.x,
                            facePositionY = face.position.y)
                    crowdPeople.add(crowdPerson)
                }
                this.crowdDao.insert(crowdPeople)
                onCompleted(crowdId)
            }
        }
    }

    fun deleteCrowd(crowd: CrowdEntity, onCompleted: () -> Unit) {
        val asyncTask = object : AsyncTask<Void, Void, Long>() {
            override fun doInBackground(vararg params: Void?): Long {
                appDatabase.runInTransaction {
                    crowdDao.deleteOneCrowd(crowd)
                    imageUtil.delete(crowd.trackedImageName)
                }
                return crowd.id
            }

            override fun onPostExecute(result: Long?) {
                onCompleted()
            }
        }.execute()
    }
}