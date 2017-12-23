//
//  CloudVisionUITests.swift
//  CloudVisionUITests
//
//  Created by Breno Marques on 11/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import XCTest

class AppUITests: XCTestCase {
    
    let BACK_BUTTON = 0
    let PICTURE_LIBRARY_BUTTON = 0
    let CAMERA_BUTTON = 1
    let RAFFLE_BUTTON = 1
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        BMCrowd.deleteAll()
        continueAfterFailure = false
        self.app = XCUIApplication()
    }
    
    override func tearDown() {
        super.tearDown()
        BMCrowd.deleteAll()
        self.app = nil
    }
    
    func testSelectOnePhotoByPhotoLibrary() {
        app.launchEnvironment[BMImagePicker.FAKER_IMAGE_SELECTED]  = "crowd-test-01.png"
        app.launch()
        app.toolbars.buttons.element(boundBy:PICTURE_LIBRARY_BUTTON).tap()
        XCTAssertEqual(app.collectionViews.cells.count, 10)
    }
    
    func testSelectOnePhotoByCamera() {
        app.launchEnvironment[BMImagePicker.FAKER_IMAGE_SELECTED]  = "crowd-test-02.jpg"
        app.launch()
        app.toolbars.buttons.element(boundBy:CAMERA_BUTTON).tap()
        XCTAssertEqual(app.collectionViews.cells.count, 20)
    }
    
    func testRaffleOneCompetitor(){
        testSelectOnePhotoByPhotoLibrary()
        app.navigationBars.buttons.element(boundBy:RAFFLE_BUTTON).tap()
    }
    
    func testAddAndShowOneCrowdTableVeiw() {
        self.testSelectOnePhotoByPhotoLibrary()
        app.navigationBars.buttons.element(boundBy:BACK_BUTTON).tap()
        XCTAssertEqual(app.tables.cells.count, 1)
    }
}
