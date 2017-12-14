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
    }
    
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
    
}
