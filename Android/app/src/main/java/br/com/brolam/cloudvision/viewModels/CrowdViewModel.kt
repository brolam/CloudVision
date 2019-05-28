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
import br.com.brolam.cloudvision.models.CvImageEntity
import br.com.brolam.cloudvision.models.CvRecognizableEntity
import br.com.brolam.cloudvision.models.CvRecognizableItemEntity

/**
 * Created by brenomarques on 08/01/2018.
 *
 */
class CrowdViewModel(application: Application) : AndroidViewModel(application) {
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)
    private val crowdDao = this.appDatabase.crowdDao()
    private val imageUtil = ImageUtil(application)
    private var cvRecognizable: CvRecognizableEntity? = null
    private var trackedImage: Bitmap? = null;
    private var facesBitmap = HashMap<Long, Bitmap>()

    interface CrowdViewModelLifecycle : LifecycleOwner {
        fun onCrowdPeopleUpdated()
    }

    fun setCrowdPeopleObserve(crowdId: Long, lifecycleOwner: CrowdViewModelLifecycle) {
        this.crowdDao.getCrowdPeopleById(crowdId).observe(lifecycleOwner, Observer {
            this.cvRecognizable = it
            if (this.cvRecognizable != null) {
                this.trackedImage = this.imageUtil.getImage(this.getCrowd().trackedImageName)
                this.facesBitmap = HashMap<Long, Bitmap>()
                getPeople().forEach { crowdPersonEntity ->
                    this.facesBitmap[crowdPersonEntity.id] = getPersonPicture(crowdPersonEntity)
                }
            }
            lifecycleOwner.onCrowdPeopleUpdated()
        })
    }


    fun getCrowd(): CvImageEntity {
        return this.cvRecognizable!!.crowd
    }

    fun getTrackedImage(): Bitmap {
        return this.trackedImage!!
    }

    fun getPeople(): List<CvRecognizableItemEntity> {
        return this.cvRecognizable!!.people
    }

    fun getWinners(): List<CvRecognizableItemEntity> {
        return this.cvRecognizable!!.people.filter { it.winnerPosition > 0 }.sortedBy { it.winnerPosition }
    }

    fun createRaffledPeopleList(): List<CvRecognizableItemEntity> {
        val raffledPeopleList = ArrayList<CvRecognizableItemEntity>()
        val competitors = this.cvRecognizable!!.people.filter { it.winnerPosition == 0 }
        (0..10).forEach {
            var person = Raffle.chooseOne(competitors)
            person?.let { raffledPeopleList.add(it) }
        }
        return raffledPeopleList
    }

    private fun getLastWinner(): CvRecognizableItemEntity? {
        return this.cvRecognizable!!.people.sortedBy { it.winnerPosition }.last()
    }

    fun getPersonPicture(personId: Long): Bitmap {
        return this.facesBitmap.get(personId)!!
    }

    private fun getPersonPicture(crowdPersonEntity: CvRecognizableItemEntity): Bitmap {
        return this.imageUtil.crop(
                this.getTrackedImage(),
                crowdPersonEntity.facePositionX,
                crowdPersonEntity.facePositionY,
                crowdPersonEntity.faceWidth,
                crowdPersonEntity.faceHeight,
                enlargeWidthInPercent = 15.toFloat(),
                enlargeHeightInPercent = 15.toFloat())
    }

    fun setWinner(person: CvRecognizableItemEntity) {
        AsyncTask.execute {
            val lastWinnerPosition = (getLastWinner()?.winnerPosition ?: 0)
            person.winnerPosition = lastWinnerPosition + 1
            crowdDao.updatePerson(person)
        }
    }
}