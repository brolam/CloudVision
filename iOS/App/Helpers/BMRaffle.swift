//
//  BMPicker.swift
//  CloudVision
//
//  Created by Breno Marques on 20/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation

class BMRaffle {
    static func chooseOne(competitors:[Int]!) -> Int  {
        if ( competitors.count == 0 ) { return -1 }
        let winner = competitors[Int(arc4random_uniform(UInt32(competitors.count)))]
        return winner
    }
}
