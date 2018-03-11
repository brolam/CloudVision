package br.com.brolam.cloudvision.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
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
    private var _TrackedImage: Bitmap? = null;
    private val _facesBitmap = mutableListOf<Bitmap>()

    fun getCrowdPeopleById(id: Long): LiveData<CrowdPeopleEntity> {
        return this.crowdDao.getCrowdPeopleById(id)
    }

    fun getImagesPeopleFaces(trackedImage: Bitmap, people: List<CrowdPersonEntity>) : List<Bitmap> {
        if ( _facesBitmap.size > 0 ) return _facesBitmap;
        people.forEach { crowdPersonEntity ->
            val faceBitmap = this.imageUtil.crop(
                    trackedImage,
                    crowdPersonEntity.facePositionX,
                    crowdPersonEntity.facePositionY,
                    crowdPersonEntity.faceWidth,
                    crowdPersonEntity.faceHeight,
                    enlargeWidthInPercent = 15.toFloat(),
                    enlargeHeightInPercent =  15.toFloat())
            _facesBitmap.add(faceBitmap)

        }
        return _facesBitmap
    }

    fun getTrackedImage(trackedImageName:String): Bitmap? {
        if ( _TrackedImage != null ) return _TrackedImage
        _TrackedImage = this.imageUtil.getImage(trackedImageName)
        return  _TrackedImage
    }
}