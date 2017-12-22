//
//  BMCrowdTestCase.swift
//  CloudVisionTests
//
//  Created by Breno Marques on 21/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import XCTest

class BMCrowdTestCase: XCTestCase {
    let crowdFields  = (
        title: "Dec 31 2017 00:00:00",
        created : Date(),
        trackedUIImage: UIImage(),
        facesLocation: [0:CGRect(x: 100, y: 100, width: 0, height: 100)],
        winnersIndex: [0]
    )
    
    func testInit() {
        let uiImage = UIImage(named: "crowd-test-01.png")!
        let bmCrowd = BMCrowd(
            title: "Dec 31 2017 00:00:00",
            created : Date(),
            trackedUIImage: uiImage,
            facesLocation: [0:CGRect(x: 100, y: 100, width: 0, height: 100)],
            winnersIndex: [0]
        )
        XCTAssertNotNil(bmCrowd)
    }
    
    func testInvalidInit() {
        XCTAssertNil(
            BMCrowd(
                title:"",
                created: crowdFields.created,
                trackedUIImage: crowdFields.trackedUIImage,
                facesLocation: crowdFields.facesLocation,
                winnersIndex: crowdFields.winnersIndex),
            "Title it is required"
        )
        
        XCTAssertNil(
            BMCrowd(
                title: crowdFields.title,
                created: crowdFields.created,
                trackedUIImage: nil,
                facesLocation: crowdFields.facesLocation,
                winnersIndex: crowdFields.winnersIndex),
            "TrackedUIImage it is required"
        )
    }
    
    func testSaveAndLoadOneCrowd(){
        let oneBMCrowd = BMCrowd(
            title:crowdFields.title,
            created: crowdFields.created,
            trackedUIImage: crowdFields.trackedUIImage,
            facesLocation: crowdFields.facesLocation,
            winnersIndex: crowdFields.winnersIndex)
        XCTAssertTrue(BMCrowd.save(crowds:[oneBMCrowd!]), "Failed to save crowds...")
        let crowds: [BMCrowd]? = BMCrowd.load()
        XCTAssertNotNil(crowds)
        XCTAssertEqual(crowds?.count, 1)
    }
}
