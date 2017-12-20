//
//  BMPickerTestCase.swift
//  CloudVisionTests
//
//  Created by Breno Marques on 20/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import XCTest

class BMPickerTestCase: XCTestCase {
    
    func testChooseOne() {
        let competitors = [Int](0...10)
        let winner = BMRaffle.chooseOne(competitors: competitors)
        XCTAssertGreaterThanOrEqual(competitors.count, winner)
        XCTAssertLessThanOrEqual(winner, competitors.count)
    }
    
    func testChooseOneWithoutCompetitors() {
        let competitors = [Int]()
        let winner = BMRaffle.chooseOne(competitors: competitors)
        XCTAssertEqual(winner,-1)
    }
}
