//
//  BMCrowd.swift
//  CloudVision
//
//  Created by Breno Marques on 21/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit
import os.log

class BMCrowd: NSObject, NSCoding {
   
    //MARK: Archiving Paths
    static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    static let ArchiveURL = DocumentsDirectory.appendingPathComponent("crowds")
    var title: String!
    let created: Date!
    var trackedUIImage: UIImage!
    var facesLocation: [Int:CGRect]
    var winnersIndex:[Int]
    //MARK: Types
    struct PropertyKey {
        static let title = "title"
        static let created = "created"
        static let trackedUIImage = "trackedUIImage"
        static let facesLocation = "facesLocation"
        static let winnersIndex = "winnersIndex"
    }
    
    init?(
        title: String!,
        created : Date!,
        trackedUIImage: UIImage!,
        facesLocation: [Int:CGRect] = [Int:CGRect]() ,
        winnersIndex: [Int] = [Int]()) {
        guard title.isEmpty == false else { return nil }
        guard trackedUIImage != nil else { return nil }
        
        self.title = title
        self.created = created
        self.trackedUIImage = trackedUIImage
        self.facesLocation = facesLocation
        self.winnersIndex = winnersIndex
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(self.title, forKey: PropertyKey.title)
        aCoder.encode(self.created, forKey: PropertyKey.created)
        aCoder.encode(self.trackedUIImage, forKey: PropertyKey.trackedUIImage)
        aCoder.encode(self.facesLocation, forKey: PropertyKey.facesLocation)
        aCoder.encode(self.winnersIndex, forKey: PropertyKey.winnersIndex)
    }
    
    required convenience init?(coder aDecoder: NSCoder) {
        guard let decodeTitle = aDecoder.decodeObject(forKey: PropertyKey.title) as? String else {
            os_log("Unable to decode the title for a Crowd object.", log: OSLog.default, type: .debug)
            return nil
        }
        
        guard let decodeCreated = aDecoder.decodeObject(forKey: PropertyKey.created) as? Date else {
            os_log("Unable to decode the created for a Crowd object.", log: OSLog.default, type: .debug)
            return nil
        }
        
        guard let decodeTrackedUIImage = aDecoder.decodeObject(forKey: PropertyKey.trackedUIImage) as? UIImage else {
            os_log("Unable to decode the trackedUIImage for a Crowd object.", log: OSLog.default, type: .debug)
            return nil
        }
        
        guard let decodeFacesLocation = aDecoder.decodeObject(forKey: PropertyKey.facesLocation) as? [Int: CGRect] else {
            os_log("Unable to decode the facesLocation for a Crowd object.", log: OSLog.default, type: .debug)
            return nil
        }
        
        let decodeWinnersIndex = aDecoder.decodeObject(forKey: PropertyKey.winnersIndex) as! [Int]
        
        self.init(
            title: decodeTitle,
            created: decodeCreated,
            trackedUIImage: decodeTrackedUIImage,
            facesLocation: decodeFacesLocation,
            winnersIndex: decodeWinnersIndex
        )
    }
    
    static func save(crowds:[BMCrowd]) -> Bool{
        return NSKeyedArchiver.archiveRootObject(crowds, toFile: BMCrowd.ArchiveURL.path)
    }
    
    static func load() -> [BMCrowd]?{
        return (NSKeyedUnarchiver.unarchiveObject(withFile: BMCrowd.ArchiveURL.path) as? [BMCrowd])
    }
    
}
