//
//  BMStrings.swift
//  CloudVision
//
//  Created by Breno Marques on 27/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation

let dateFormatter = DateFormatter()

/**
 global strings
 **/
public func GS(_ keyOrString:String) -> String {
    return NSLocalizedString(keyOrString , comment: "")
}

public func stringLongDateTime(_ date: Date) -> String {
    dateFormatter.dateStyle = .full
    dateFormatter.timeStyle = .medium
    return dateFormatter.string(from: date)
}

