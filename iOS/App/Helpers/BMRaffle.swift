//
//  BMPicker.swift
//  CloudVision
//
//  Created by Breno Marques on 20/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation

class BMRaffle {
    static func chooseOne<T>(competitors:[T]) -> T?  {
        if ( competitors.count == 0 ) { return nil  }
        let indexWinner = Int(arc4random_uniform(UInt32(competitors.count)))
        return competitors[indexWinner]
    }
}
