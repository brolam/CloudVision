package br.com.brolam.cloudvision.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.os.AsyncTask
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.models.AppDatabase
import br.com.brolam.cloudvision.models.CrowdPeopleEntity
import br.com.brolam.cloudvision.models.CrowdPersonEntity

/**
 * Created by brenomarques on 08/01/2018.
 *
 */
class CrowdViewModel(application: Application) : AndroidViewModel(application) {
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)
    private val crowdDao = this.appDatabase.crowdDao()
    private val imageUtil = ImageUtil(application)
    private var trackedImage: Bitmap? = null;
    private val facesBitmap = mutableListOf<Bitmap>()

    fun getCrowdPeopleById(id: Long): LiveData<CrowdPeopleEntity> {
        return this.crowdDao.getCrowdPeopleById(id)
    }

    fun getImagesPeopleFaces(trackedImage: Bitmap, people: List<CrowdPersonEntity>) : List<Bitmap> {
        if ( this.facesBitmap.size > 0 ) return this.facesBitmap;
        people.forEach { crowdPersonEntity ->
            val faceBitmap = this.imageUtil.crop(
                    trackedImage,
                    crowdPersonEntity.facePositionX,
                    crowdPersonEntity.facePositionY,
                    crowdPersonEntity.faceWidth,
                    crowdPersonEntity.faceHeight,
                    enlargeWidthInPercent = 15.toFloat(),
                    enlargeHeightInPercent =  15.toFloat())
            this.facesBitmap.add(faceBitmap)

        }
        return this.facesBitmap
    }

    fun getTrackedImage(trackedImageName:String): Bitmap? {
        if ( this.trackedImage != null ) return this.trackedImage
        this.trackedImage = this.imageUtil.getImage(trackedImageName)
        return this.trackedImage
    }

    fun raffleOnePerson(crowdId: Long, onBegin:() -> Unit, onEnd:() -> Unit ){
        /*
        object : AsyncTask<Void, Void, Long>() {
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): Long {
                val competitors = crowdDao.getPeople(crowdId).filter { person -> person.winnerPosition == 0 }
            }

            override fun onPostExecute(result: Long?) {
                super.onPostExecute(result)
            }

        }.execute()
        */

    }
}