package br.com.brolam.cloudvision.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.os.AsyncTask
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.helpers.Raffle
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

    fun getCrowdPeopleById(id: Long): LiveData<CrowdPeopleEntity> {
        return this.crowdDao.getCrowdPeopleById(id)
    }

    fun getImagesPeopleFaces(trackedImage: Bitmap, people: List<CrowdPersonEntity>): List<Bitmap> {
        val facesBitmap = mutableListOf<Bitmap>()
        people.forEach { crowdPersonEntity ->
            val faceBitmap = this.imageUtil.crop(
                    trackedImage,
                    crowdPersonEntity.facePositionX,
                    crowdPersonEntity.facePositionY,
                    crowdPersonEntity.faceWidth,
                    crowdPersonEntity.faceHeight,
                    enlargeWidthInPercent = 15.toFloat(),
                    enlargeHeightInPercent = 15.toFloat())
            facesBitmap.add(faceBitmap)

        }
        return facesBitmap
    }

    fun getTrackedImage(trackedImageName: String): Bitmap? {
        if (this.trackedImage != null) return this.trackedImage
        this.trackedImage = this.imageUtil.getImage(trackedImageName)
        return this.trackedImage
    }

    fun raffleOnePerson(crowdId: Long, onBegin: () -> Unit, onEnd: (facesBitmap: List<Bitmap>) -> Unit) {
        object : AsyncTask<Void, Void, List<Bitmap>>() {
            override fun onPreExecute() {
                super.onPreExecute()
                onBegin()
            }

            override fun doInBackground(vararg params: Void?): List<Bitmap> {
                val people = crowdDao.getPeople(crowdId)
                val competitors = people.filter { person -> person.winnerPosition == 0 }
                val lastWinner = people.sortedBy{it.winnerPosition }.last()
                val pickedList = ArrayList<CrowdPersonEntity>()
                val pickedFacesBitmap = ArrayList<Bitmap>()
                (0..10).forEach { index ->
                    var personPicked = Raffle.chooseOne(competitors)
                    personPicked?.let { pickedList.add(it) }
                }
                pickedList.forEach() { person ->
                    val faceBitmap = trackedImage?.let {
                        imageUtil.crop(
                                it,
                                person.facePositionX,
                                person.facePositionY,
                                person.faceWidth,
                                person.faceHeight,
                                enlargeWidthInPercent = 15.toFloat(),
                                enlargeHeightInPercent = 15.toFloat())
                    }
                    faceBitmap?.let { pickedFacesBitmap.add(it) }

                }
                pickedList.last().winnerPosition = lastWinner.winnerPosition + 1
                crowdDao.updatePerson(pickedList.last())
                return pickedFacesBitmap
            }

            override fun onPostExecute(result: List<Bitmap>) {
                super.onPostExecute(result)
                onEnd(result)
            }

        }.execute()
    }
}