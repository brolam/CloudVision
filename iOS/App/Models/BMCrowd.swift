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
    fileprivate static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    fileprivate static let ArchiveURL = DocumentsDirectory.appendingPathComponent("crowds")
    fileprivate static var singletonCrowds:[BMCrowd]? = nil
    //MARK: Types
    struct PropertyKey {
        static let title = "title"
        static let created = "created"
        static let trackedUIImage = "trackedUIImage"
        static let people = "people"
        struct People {
            static let key = "key"
            static let faceImageLocation = "faceImageLocation"
            static let winnerPosition = "winnerPosition"
        }
    }
    
    @objc(BMCrowdPerson)
    class Person :  NSObject, NSCoding {
        var key: Int!
        var faceImageLocation: CGRect!
        var winnerPosition: Int!
        
        init(key: Int!, faceImageLocation: CGRect!, winnerPosition: Int!) {
            self.key = key
            self.faceImageLocation = faceImageLocation
            self.winnerPosition = winnerPosition
        }
        
        func encode(with aCoder: NSCoder) {
            aCoder.encode(self.key, forKey: PropertyKey.People.key)
            aCoder.encode(self.faceImageLocation, forKey: PropertyKey.People.faceImageLocation)
            aCoder.encode(self.winnerPosition, forKey: PropertyKey.People.winnerPosition)
        }
        
        convenience required init?(coder aDecoder: NSCoder) {
            guard let decoderKey = aDecoder.decodeObject(forKey: PropertyKey.People.key) as? Int else {
                os_log("Unable to decode the Person Key for a Crowd object.", log: OSLog.default, type: .debug)
                return nil
            }
            
            guard let decoderFaceImageLocation = aDecoder.decodeObject(
                forKey: PropertyKey.People.faceImageLocation) as? CGRect else{
                    os_log("Unable to decode the Person FaceImageLocation for a Crowd object.", log: OSLog.default, type: .debug)
                    return nil
            }
            
            guard let decoderWinnerPosition = aDecoder.decodeObject(
                forKey: PropertyKey.People.winnerPosition) as? Int else{
                    os_log("Unable to decode the Person WinnerPosition for a Crowd object.",
                           log: OSLog.default, type: .debug)
                    return nil
            }
            
            self.init(
                key: decoderKey,
                faceImageLocation: decoderFaceImageLocation,
                winnerPosition: decoderWinnerPosition
            )
        }
        
        func isRaffled() -> Bool{
            return self.winnerPosition != 0
        }
    }
    
    var title: String!
    let created: Date!
    var trackedUIImage: UIImage!
    var people: [Person]
    
    init?(title: String, created : Date, trackedUIImage: UIImage! , people: [Person]!) {
        guard title.isEmpty == false else { return nil }
        guard trackedUIImage != nil else { return nil }
        guard people != nil else { return nil }
        guard people.count > 0  else { return nil }
        
        self.title = title
        self.created = created
        self.trackedUIImage = trackedUIImage
        self.people = people
        
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(self.title, forKey: PropertyKey.title)
        aCoder.encode(self.created, forKey: PropertyKey.created)
        aCoder.encode(self.trackedUIImage, forKey: PropertyKey.trackedUIImage)
        aCoder.encode(self.people, forKey: PropertyKey.people)
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
        
        guard let decodePeople = aDecoder.decodeObject(forKey: PropertyKey.people) as? [Person] else {
            os_log("Unable to decode the People for a Crowd object.", log: OSLog.default, type: .debug)
            return nil
        }
        
        self.init(
            title: decodeTitle,
            created: decodeCreated,
            trackedUIImage: decodeTrackedUIImage,
            people: decodePeople
        )
    }
    
    func getFacesPictures() -> [UIImage]{
        let facesPictures = people.map { (person) -> UIImage in
            BMImageUtilities.crop(
                uiImage: self.trackedUIImage!,
                toRect: person.faceImageLocation,
                enlargeWidthInPercent: 20,
                enlargeHeightInPercent:30
            )
        }
        return facesPictures
    }
    
    func getWinnersOrdered() -> [Person] {
        let winners = self.people.filter { (person) -> Bool in person.winnerPosition > 0 }
        let winnersOrdered = winners.sorted(by: { ( beforePerson, nextPerson) -> Bool in
            return beforePerson.winnerPosition < nextPerson.winnerPosition
        })
        return winnersOrdered
    }
    
    func getNotWinners() -> [Person] {
        let notWinners = self.people.filter { (person) -> Bool in person.winnerPosition == 0 }
        return notWinners
    }
    
    func setNextWinner(person: Person){
        if ( person.isRaffled()) { return }
        let nextPosition = self.people.reduce(0) { (lastPosition, person) -> Int in
            return (lastPosition <= person.winnerPosition)
                ? person.winnerPosition + 1
                : lastPosition
        }
        person.winnerPosition = nextPosition
    }
    
    static func loadCrowdsIfNotLoadedYet() {
        if ( singletonCrowds != nil) { return }
        if  let crowds = (NSKeyedUnarchiver.unarchiveObject(withFile: BMCrowd.ArchiveURL.path) as? [BMCrowd]) {
            singletonCrowds = crowds
        } else {
            singletonCrowds = [BMCrowd]()
        }
    }
    
    static func getCrowds() -> [BMCrowd]{
        loadCrowdsIfNotLoadedYet()
        return singletonCrowds!
    }
    
    static func persistCrowds() -> Bool{
        loadCrowdsIfNotLoadedYet()
        return NSKeyedArchiver.archiveRootObject(singletonCrowds!, toFile: BMCrowd.ArchiveURL.path)
    }
    
    static func add(_ bmCrowd:BMCrowd) -> Bool{
        loadCrowdsIfNotLoadedYet()
        singletonCrowds!.insert(bmCrowd, at:0 )
        return persistCrowds()
    }
    
    static func deleteAll()  {
        let path = BMCrowd.ArchiveURL.path
        singletonCrowds = nil
        if FileManager.default.fileExists(atPath: path) {
            do{
                try FileManager.default.removeItem(atPath: path)
            } catch{}
        }
    }
}
