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
    }
}
