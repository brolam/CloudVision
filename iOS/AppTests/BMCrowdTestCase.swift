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
        people: [
            BMCrowd.Person(key:1 , faceImageLocation: CGRect(x:0, y:0, width: 60 , height:60 ), winnerPosition: 0)
        ]
    )
    
    func testInit() {
        let bmCrowd = BMCrowd(
            title: crowdFields.title,
            created : crowdFields.created,
            trackedUIImage: crowdFields.trackedUIImage,
            people: crowdFields.people
        )
        XCTAssertNotNil(bmCrowd)
    }
    
    func testInvalidInit() {
        XCTAssertNil(
            BMCrowd(
                title:"",
                created : crowdFields.created,
                trackedUIImage: crowdFields.trackedUIImage,
                people: crowdFields.people),
            "Title it is required"
        )
        
        XCTAssertNil(
            BMCrowd(
                title: crowdFields.title,
                created: crowdFields.created,
                trackedUIImage: nil,
                people: crowdFields.people),
            "TrackedUIImage it is required"
        )
        
        XCTAssertNil(
            BMCrowd(
                title: crowdFields.title,
                created: crowdFields.created,
                trackedUIImage: crowdFields.trackedUIImage,
                people: nil ),
            "People it is required"
        )
    }
    
    func testSaveAndLoadOneCrowd(){
        let oneBMCrowd = BMCrowd(
            title: crowdFields.title,
            created : crowdFields.created,
            trackedUIImage: crowdFields.trackedUIImage,
            people: crowdFields.people)
        XCTAssertTrue(BMCrowd.save(crowds:[oneBMCrowd!]), "Failed to save crowds...")
        let crowds: [BMCrowd]? = BMCrowd.load()
        XCTAssertNotNil(crowds)
        XCTAssertEqual(crowds?.count, 1)
    }
}
