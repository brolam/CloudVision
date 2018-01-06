package br.com.brolam.cloudvision.helpers

import android.content.Context
import android.graphics.Bitmap
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector

/**
* Created by brenomarques on 02/01/2018.
*
*/
class FacesDetector(context: Context) {
    private val faceDetector = FaceDetector.Builder(context)
            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .setProminentFaceOnly(false)
            .setMinFaceSize(0.01F)
            .setTrackingEnabled(false)
            .setMode(FaceDetector.ACCURATE_MODE)
            .build()
    private var trackingFaces = SparseArray<Face>()

    fun trackFaces(bitmap: Bitmap): Boolean {
        val frame = Frame.Builder()
                .setBitmap(bitmap)
                .build()
        val facesDetected = faceDetector.detect(frame)
        return if (facesDetected.size() > 0) {
            this.trackingFaces = facesDetected
            true
        } else {
            this.trackingFaces = SparseArray()
            false
        }
    }

    fun countFaces(): Int {
        return this.trackingFaces.size()
    }
}