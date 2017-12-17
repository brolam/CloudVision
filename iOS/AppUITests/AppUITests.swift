//
//  CloudVisionUITests.swift
//  CloudVisionUITests
//
//  Created by Breno Marques on 11/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import XCTest

class AppUITests: XCTestCase {
    
   
    
    let PICTURE_LIBRARY_BUTTON = 0
    let CAMERA_BUTTON = 1
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        self.app = XCUIApplication()
        self.app.launchArguments.append("--uitesting")
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
        self.app = nil
    }
    
    func testShowPhotoLibrary() {
     app.launch()
     app.toolbars.buttons.element(boundBy:PICTURE_LIBRARY_BUTTON).tap()
    }
    
    func testShowCamera() {
        app.launch()
        app.toolbars.buttons.element(boundBy:CAMERA_BUTTON).tap()
    }
    
}
