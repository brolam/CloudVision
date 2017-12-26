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
            BMCrowd.Person(key:1 , faceImageLocation: CGRect(x:0, y:0, width: 60 , height:60 ), winnerPosition: 0),
            BMCrowd.Person(key:2 , faceImageLocation: CGRect(x:0, y:0, width: 60 , height:60 ), winnerPosition: 0),
            BMCrowd.Person(key:3 , faceImageLocation: CGRect(x:0, y:0, width: 60 , height:60 ), winnerPosition: 0)
        ]
    )
    
    override func setUp() {
        super.setUp()
        BMCrowd.deleteAll()
    }
    
    override func tearDown() {
        super.tearDown()
        BMCrowd.deleteAll()
    }
    
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
    
    func testLoadCrowdsIfNotLoadedYet(){
        BMCrowd.loadCrowdsIfNotLoadedYet()
        XCTAssertNotNil(BMCrowd.getCrowds())
        XCTAssertEqual(BMCrowd.getCrowds().count, 0)
    }
    
    func testGetCrowds(){
        XCTAssertEqual(BMCrowd.getCrowds().count, 0)
    }
    
    func testAddAndGetOneCrowd(){
        BMCrowd.loadCrowdsIfNotLoadedYet()
        let oneBMCrowd = BMCrowd(
            title: crowdFields.title,
            created : crowdFields.created,
            trackedUIImage: crowdFields.trackedUIImage,
            people: crowdFields.people)
        BMCrowd.add(oneBMCrowd!)
        XCTAssertNotNil(BMCrowd.getCrowds())
        XCTAssertEqual(BMCrowd.getCrowds().count, 1)
    }
    
    func testUpdateAndGetOneCrowd(){
        testAddAndGetOneCrowd()
        let addedCrowd = BMCrowd.getCrowds()[0]
        addedCrowd.people[0].winnerPosition = 1
        BMCrowd.persistCrowds()
        let updatedCrowd  = BMCrowd.getCrowds()[0]
        XCTAssertEqual(updatedCrowd.people[0].winnerPosition, 1 )
    }
    
    func testDeleteOneCrowd(){
        self.testAddAndGetOneCrowd()
        let firstCrowd = BMCrowd.getCrowds()[0]
        BMCrowd.delete(firstCrowd)
        XCTAssertEqual(BMCrowd.getCrowds().count, 0)
    }
    
    func testDeleteInvalidCrowd(){
        let bmCrowdNotAdded = BMCrowd(
            title: crowdFields.title,
            created : crowdFields.created,
            trackedUIImage: crowdFields.trackedUIImage,
            people: crowdFields.people
        )
        BMCrowd.delete(bmCrowdNotAdded!)
        XCTAssertEqual(BMCrowd.getCrowds().count, 0)
    }
    
    func testGetWinnersOrdered(){
        let person1 = 0, person2 = 1, person3 = 2
        let first = 1,  second = 2, third = 3
        testAddAndGetOneCrowd()
        let firstCrowd  = BMCrowd.getCrowds()[0]
        firstCrowd.people[person1].winnerPosition = third
        firstCrowd.people[person2].winnerPosition = second
        firstCrowd.people[person3].winnerPosition = first
        BMCrowd.persistCrowds()
        let winnersOrdered = firstCrowd.getWinnersOrdered()
        XCTAssertEqual(winnersOrdered.count,3)
        XCTAssertEqual(winnersOrdered[0], firstCrowd.people[person3])
        XCTAssertEqual(winnersOrdered[1], firstCrowd.people[person2])
        XCTAssertEqual(winnersOrdered[2], firstCrowd.people[person1])
    }
    
    func testGetZeroWinnersOrdered(){
        testAddAndGetOneCrowd()
        let firstCrowd  = BMCrowd.getCrowds()[0]
        let winnersOrdered = firstCrowd.getWinnersOrdered()
        XCTAssertEqual(winnersOrdered.count,0)
    }
    
    func testGetNotWinners(){
        let person1 = 0, person2 = 1, person3 = 2
        let first = 1, notWinner = 0
        testAddAndGetOneCrowd()
        let firstCrowd  = BMCrowd.getCrowds()[0]
        firstCrowd.people[person3].winnerPosition = first
        BMCrowd.persistCrowds()
        let notWinners = firstCrowd.getNotWinners()
        XCTAssertEqual(notWinners.count,2)
        XCTAssertEqual(notWinners[person2].winnerPosition, notWinner)
        XCTAssertEqual(notWinners[person1].winnerPosition, notWinner)
    }
    
    func testSetNextWinner(){
        let person1 = 0, person2 = 1, person3 = 2
        let first = 1, second = 2, notWinner = 0
        testAddAndGetOneCrowd()
        let firstCrowd  = BMCrowd.getCrowds()[0]
        let people  = BMCrowd.getCrowds()[0].people
        firstCrowd.setNextWinner(person: people[person1])
        firstCrowd.setNextWinner(person: people[person2])
        BMCrowd.persistCrowds()
        XCTAssertEqual(people[person1].winnerPosition, first)
        XCTAssertEqual(people[person2].winnerPosition, second)
        XCTAssertEqual(people[person3].winnerPosition, notWinner)
    }
    
    func testSetNextWinnerOnePersonRaffled(){
        let person1 = 0, first = 1
        testAddAndGetOneCrowd()
        let firstCrowd  = BMCrowd.getCrowds()[0]
        let people  = BMCrowd.getCrowds()[0].people
        firstCrowd.setNextWinner(person: people[person1])
        firstCrowd.setNextWinner(person: people[person1])
        BMCrowd.persistCrowds()
        XCTAssertEqual(people[person1].winnerPosition, first)
    }
}
