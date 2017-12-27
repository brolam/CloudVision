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
    var uiImageCrowd01: UIImage!
    var uiImageCrowd02: UIImage!
    
    override func setUp() {
        super.setUp()
        self.bmFacesDetector = BMFacesDetector()
        self.uiImageCrowd01 = UIImage(named: "crowd-test-01.png")!
        self.uiImageCrowd02 = UIImage(named: "crowd-test-02.jpg")!
    }
    
    override func tearDown() {
        super.tearDown()
        self.bmFacesDetector = nil
        self.uiImageCrowd01 = nil
        self.uiImageCrowd02 = nil
    }
    
    func testTrackFacesCrowd01Count() {
        XCTAssertTrue(bmFacesDetector.trackFaces(uiImage: self.uiImageCrowd01))
        XCTAssertEqual(bmFacesDetector.countFaces(), 9)
    }
    
    func testTrackFacesCrowd02Count() {
        XCTAssertTrue(bmFacesDetector.trackFaces(uiImage: self.uiImageCrowd02))
        XCTAssertEqual(bmFacesDetector.countFaces(), 19)
    }
    
    func testTrackFacesInvalidImage() {
        XCTAssertFalse(bmFacesDetector.trackFaces(uiImage: UIImage(named: "clover")!))
        XCTAssertEqual(bmFacesDetector.countFaces(), 0)
        XCTAssertEqual(bmFacesDetector.getFacesLocation().count, 0 )
    }
    
    func testGetFacesLocation(){
        self.testTrackFacesCrowd02Count()
        let facesLocation: [CGRect]  = bmFacesDetector.getFacesLocation()
        XCTAssertEqual(facesLocation.count , 19)
    }
    
    func testPerformanceTrackFaces() {
        self.measure {
            self.testTrackFacesCrowd01Count()
        }
    }
    
}
