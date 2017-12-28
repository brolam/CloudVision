//
//  BMStringsTestCase.swift
//  CloudVisionTests
//
//  Created by Breno Marques on 28/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import XCTest

class BMStringsTestCase: XCTestCase {
    
    
    func testGS() {
        XCTAssertEqual(GS("Winners"), "Winners")
        XCTAssertEqual(GS("Everyone"), "Everyone")
    }
    
    func testStringLongDateTime() {
        let calendar = Calendar.current
        var dateComponents: DateComponents! = calendar.dateComponents([.hour, .minute, .second], from: Date())
        dateComponents.day = 28
        dateComponents.month = 12
        dateComponents.year = 2017
        dateComponents.hour = 0
        dateComponents.minute = 0
        dateComponents.second = 0
        let date = calendar.date(from: dateComponents!)
        XCTAssertEqual(stringLongDateTime(date!), "Thursday, December 28, 2017 at 12:00:00 AM")
    }
    
}
