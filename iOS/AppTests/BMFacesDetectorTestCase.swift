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
    
    func testTrackFacesCrowd02Count() {
        let uiImage = UIImage(named: "crowd-test-02.jpg")
        bmFacesDetector.trackFaces(uiImage: uiImage!)
        XCTAssertEqual(bmFacesDetector.countFaces(), 19)
    }
    
    func testPerformanceTrackFaces() {
        self.measure {
            let uiImage = UIImage(named: "crowd-test-01.png")
            bmFacesDetector.trackFaces(uiImage: uiImage!)
        }
    }
    
}
