//
//  BMCoderMock.swift
//  CloudVision
//
//  Created by Breno Marques on 27/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation

class BMCoderMock: NSCoder{
    var simulateValueNilKey: String? = nil
    var fields: [String: Any] =  [String: Any]()
    override func encode(_ object: Any?, forKey key: String) {
        self.fields[key] = object
    }
    
    override func decodeObject(forKey key: String) -> Any? {
        if ( self.simulateValueNilKey == key) { return nil }
        return fields[key]
    }
    
    func simulateValueNil(forKey: String){
        self.simulateValueNilKey = forKey
    }
    
    func cancelSimulateValueNil(){
        self.simulateValueNilKey = nil
    }
}
