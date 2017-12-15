//
//  BMFacesDetector.swift
//  CloudVision
//
//  Created by Breno Marques on 14/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import GoogleMobileVision

class BMFacesDetector {
    let gmvDetector:GMVDetector!
    var trackingFaces:[GMVFeature]!
    var trackedUIImage: UIImage?
    
    
    init() {
        self.gmvDetector = GMVDetector(
            ofType: GMVDetectorTypeFace,
            options:
            [
                GMVDetectorFaceLandmarkType : "GMVDetectorFaceLandmarkAll'",
                GMVDetectorFaceClassificationType : "GMVDetectorFaceClassificationAll",
                GMVDetectorFaceMinSize : "(0.3)",
                GMVDetectorFaceTrackingEnabled : "NO"
            ])
        self.trackingFaces = [GMVFeature]()
    }
    
    func trackFaces(uiImage: UIImage ){
        self.trackedUIImage = uiImage
        self.trackingFaces = gmvDetector.features(
            in: uiImage,
            options: nil
        )
        self.removeInvalidFaces()
    }
    
    func countFaces() -> Int {
        return trackingFaces.count
    }
    
    func getFacesPictures() -> [UIImage]{
        if (( self.trackedUIImage == nil ) || (self.trackingFaces.count == 0 ) ){
            return [UIImage]()
        }
        
        let facesPictures = trackingFaces.map { (trackFace) -> UIImage in
            BMImageUtilities.crop(
                uiImage: self.trackedUIImage!,
                toRect: trackFace.bounds,
                enlargeWidthInPercent:20,
                enlargeHeightInPercent: 30
            )
        }
        return facesPictures
    }
    
    private func removeInvalidFaces(){
        if (self.trackingFaces.count == 0 ) { return }
        let maxFaceWidth = trackingFaces.reduce(0.0, { (maxWidth: CGFloat , oneFace: GMVFeature ) -> CGFloat in
            maxWidth <  oneFace.bounds.width
                ? oneFace.bounds.width
                : maxWidth
        })
        trackingFaces = trackingFaces.filter({ (oneFace) -> Bool in
            oneFace.bounds.width > ( maxFaceWidth / 2.0 )
        })
        
    }
}
