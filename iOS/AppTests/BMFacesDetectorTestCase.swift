//
//  BMFaceDetectorTests.swift
//  CloudVisionTests
//
//  Created by Breno Marques on 13/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import XCTest

class BMFacesDetectorTestCase: XCTestCase {
    var bmFacesDetector: BMFacesDetector!
    
    override func setUp() {
        super.setUp()
        bmFacesDetector = BMFacesDetector()
    }
    
    override func tearDown() {
        super.tearDown()
        self.bmFacesDetector = nil
    }
    
    func testTrackFacesCrowd01Count() {
        let uiImage = UIImage(named: "crowd-test-01.png")
        bmFacesDetector.trackFaces(uiImage: uiImage!)
        XCTAssertEqual(bmFacesDetector.countFaces(), 9)
    }
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
    
}
