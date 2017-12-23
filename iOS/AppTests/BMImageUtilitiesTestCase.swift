//
//  BMImageUtilitiesTestCase.swift
//  CloudVisionTests
//
//  Created by Breno Marques on 14/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import XCTest

class BMImageUtilitiesTestCase: XCTestCase {
    let uiImage = UIImage(named: "crowd-test-01.png")!
    
    func testCrop() {
        let cgRect = CGRect(x: 100, y: 100, width: 100, height: 100)
        let cropedUiImage =  BMImageUtilities.crop(
            uiImage: uiImage,
            toRect: cgRect,
            enlargeWidthInPercent: 10,
            enlargeHeightInPercent: 10
        )
        XCTAssertEqual(cropedUiImage.size , CGSize(width: 120, height: 120))
    }
    
    func testAllNormalizeOrientation(){
        for indexOrientation in 0...7{
            let imageOrientation = UIImageOrientation.init(rawValue: indexOrientation)
            let uiImageNewOrientation = UIImage(cgImage: uiImage.cgImage!, scale:1.0, orientation: imageOrientation!)
            let normalizedImage = BMImageUtilities.normalizeOrientation(uiImageNewOrientation)
            XCTAssertEqual(normalizedImage.imageOrientation, UIImageOrientation.up)
        }
    }
    
    func testResizeImage(){
        let resizedImage = BMImageUtilities.resizeImage(uiImage: uiImage, newSize: 800)
        XCTAssertEqual(resizedImage.size.width, 800)
        XCTAssertEqual(resizedImage.size.height, 800)
        
    }
    
}
