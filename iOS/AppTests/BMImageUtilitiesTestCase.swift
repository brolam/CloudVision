//
//  BMImageUtilitiesTestCase.swift
//  CloudVisionTests
//
//  Created by Breno Marques on 14/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import XCTest

class BMImageUtilitiesTestCase: XCTestCase {
    
    func testCrop() {
        let uiImage = UIImage(named: "crowd-test-01.png")!
        let cgRect = CGRect(x: 100, y: 100, width: 100, height: 100)
        let cropedUiImage =  BMImageUtilities.crop(
            uiImage: uiImage,
            toRect: cgRect,
            enlargeWidthInPercent: 10,
            enlargeHeightInPercent: 10
        )
        XCTAssertEqual(cropedUiImage.size , CGSize(width: 120, height: 120))
    }
}
