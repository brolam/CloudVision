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
        continueAfterFailure = false
        self.app = XCUIApplication()
        self.app.launchArguments.append("XCTestCase")
    }
    
    override func tearDown() {
        super.tearDown()
        self.app = nil
    }
    
    func testAddOnePhotoByPhotoLibrary() {
        app.launchEnvironment[BMImagePicker.FAKER_IMAGE_SELECTED]  = "crowd-test-01.png"
        app.launch()
        app.toolbars.buttons.element(boundBy:PICTURE_LIBRARY_BUTTON).tap()
        XCTAssertTrue(waiterResultWithExpextation(app.collectionViews.element ) == .completed)
        XCTAssertEqual(app.collectionViews.cells.count, 10)
    }
    
    func testAddOnePhotoByCamera() {
        app.launchEnvironment[BMImagePicker.FAKER_IMAGE_SELECTED]  = "crowd-test-02.jpg"
        app.launch()
        app.toolbars.buttons.element(boundBy:CAMERA_BUTTON).tap()
        XCTAssertTrue(waiterResultWithExpextation(app.collectionViews.element ) == .completed)
        XCTAssertEqual(app.collectionViews.cells.count, 20)
    }

    func testTryAddInvalidPhotoByPhotoLibrary() {
        app.launchEnvironment[BMImagePicker.FAKER_IMAGE_SELECTED]  = "invaliIdPhoto.jpg"
        app.launch()
        app.toolbars.buttons.element(boundBy:PICTURE_LIBRARY_BUTTON).tap()
        XCTAssertTrue(waiterResultWithExpextation(app.alerts.element) == .completed)
        XCTAssertTrue(app.staticTexts["Sorry, this picture is not valid."].exists)
        XCTAssertFalse(waiterResultWithExpextation(app.alerts.element) == .completed)
    }
    
    func testTryAddPhotoWithoutFacesByPhotoLibrary() {
        app.launchEnvironment[BMImagePicker.FAKER_IMAGE_SELECTED]  = "clover"
        app.launch()
        app.toolbars.buttons.element(boundBy:PICTURE_LIBRARY_BUTTON).tap()
        XCTAssertTrue(waiterResultWithExpextation(app.alerts.element) == .completed)
        XCTAssertTrue(app.staticTexts["Sorry, but I could not find faces in this photo."].exists)
        XCTAssertFalse(waiterResultWithExpextation(app.alerts.element, timeout: 10) == .completed)
    }
    
    func testRaffleOnePerson(){
        testAddOnePhotoByCamera()
        app.navigationBars.buttons.element(boundBy:RAFFLE_BUTTON).tap()
        XCTAssertTrue(
            waiterResultWithExpextation(app.collectionViews.staticTexts["Winners"] , timeout: 10 ) == .completed
        )
        XCTAssertEqual(app.collectionViews.cells.count, 21)
    }
    
    func testAddAndShowOneCrowdTableVeiw() {
        self.testAddOnePhotoByPhotoLibrary()
        app.navigationBars.buttons.element(boundBy:BACK_BUTTON).tap()
        XCTAssertEqual(app.tables.cells.count, 1)
    }
    
    //Source: https://github.com/Shashikant86/Xcode83_Demo/blob/master/Xcode83_DemoUITests/Xcode83_DemoUITests.swift
    func waiterResultWithExpextation(_ element: XCUIElement, timeout:Double = 5 ) -> XCTWaiter.Result {
        let myPredicate = NSPredicate(format: "exists == true")
        let myExpectation = expectation(for: myPredicate, evaluatedWith: element,
                                        handler: nil)
        let result = XCTWaiter().wait(for: [myExpectation], timeout: timeout)
        return result
    }

}
