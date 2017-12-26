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
    let ImageFacesSize: CGFloat = 800
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
    
    private func getTrackFacesByOptions(_ uiImage: UIImage,_ options:[String:String] ) -> [GMVFeature]! {
        return gmvDetector.features(
            in: uiImage,
            options: options
        )
    }
    
    func trackFaces(uiImage: UIImage) -> Bool{
        self.trackedUIImage = self.parseImage(uiImage)
        let gmvDetectorOptions = [GMVDetectorImageOrientation : String.init(GMVImageOrientation.topLeft.rawValue)]
        if let newTrackinFaces = getTrackFacesByOptions(self.trackedUIImage! , gmvDetectorOptions) {
            self.trackingFaces = parseFaces(faces: newTrackinFaces )
        } else {
            self.trackingFaces = [GMVFeature]()
        }
        return countFaces() > 0
    }
    
    func countFaces() -> Int {
        return trackingFaces.count
    }
    
    func getFacesLocation() -> [CGRect]{
        if ( self.trackingFaces.count == 0 ){ return [CGRect]() }
        let facesLocation = trackingFaces.enumerated().map{ (index, element ) in element.bounds }
        return facesLocation
    }
    
    private func parseFaces(faces: [GMVFeature]) -> [GMVFeature]!{
        if ( faces.count == 0 ) { return [GMVFeature]() }
        let maxFaceWidth = faces.reduce(0.0, { (maxWidth: CGFloat , oneFace: GMVFeature ) -> CGFloat in
            maxWidth <  oneFace.bounds.width
                ? oneFace.bounds.width
                : maxWidth
        })
        //removes faces less than 50% of the maximum size
        return faces.filter({ (oneFace) -> Bool in
            oneFace.bounds.width > ( maxFaceWidth / 2.0 )
        })
    }
    
    private func parseImage(_ uiImage:UIImage) -> UIImage {
        let resizedImage = BMImageUtilities.resizeImage(uiImage: uiImage, newSize: self.ImageFacesSize)
        let normalizedImage = BMImageUtilities.normalizeOrientation(resizedImage)
        return normalizedImage
    }
}
