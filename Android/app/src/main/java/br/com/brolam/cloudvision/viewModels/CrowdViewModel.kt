package br.com.brolam.cloudvision.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.graphics.Bitmap
import android.os.AsyncTask
import br.com.brolam.cloudvision.helpers.ImageUtil
import br.com.brolam.cloudvision.helpers.Raffle
import br.com.brolam.cloudvision.models.AppDatabase
import br.com.brolam.cloudvision.models.CrowdEntity
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
    private var crowdPeopleEntity: CrowdPeopleEntity? = null
    private var trackedImage: Bitmap? = null;
    private var facesBitmap = HashMap<Long, Bitmap>()

    interface CrowdViewModelLifecycle : LifecycleOwner {
        fun onCrowdPeopleUpdated()
    }

    fun setCrowdPeopleObserve(crowdId: Long, lifecycleOwner: CrowdViewModelLifecycle){
        this.crowdDao.getCrowdPeopleById(crowdId).observe(lifecycleOwner, Observer {
            this.crowdPeopleEntity = it
            if (this.crowdPeopleEntity != null) {
                this.trackedImage = this.imageUtil.getImage(this.getCrowd().trackedImageName)
                this.facesBitmap = HashMap<Long, Bitmap>()
                getPeople().forEach { crowdPersonEntity ->
                    val faceBitmap = this.imageUtil.crop(
                            this.getTrackedImage(),
                            crowdPersonEntity.facePositionX,
                            crowdPersonEntity.facePositionY,
                            crowdPersonEntity.faceWidth,
                            crowdPersonEntity.faceHeight,
                            enlargeWidthInPercent = 15.toFloat(),
                            enlargeHeightInPercent = 15.toFloat())
                    this.facesBitmap.put(crowdPersonEntity.id, faceBitmap)
                }
            }
            lifecycleOwner.onCrowdPeopleUpdated()
        })
    }

    fun getCrowd(): CrowdEntity{
        return this.crowdPeopleEntity!!.crowd
    }

    fun getTrackedImage(): Bitmap{
        return this.trackedImage!!
    }

    fun getPeople(): List<CrowdPersonEntity> {
        return this.crowdPeopleEntity!!.people
    }

    fun getWinners(): List<CrowdPersonEntity> {
        return this.crowdPeopleEntity!!.people.filter { it.winnerPosition > 0 }.sortedBy { it.winnerPosition }
    }

    fun getPersonFaceBitmap(personId:Long):Bitmap{
        return this.facesBitmap.get(personId)!!
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