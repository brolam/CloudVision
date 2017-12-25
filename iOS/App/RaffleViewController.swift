//
//  RaffleViewController.swift
//  CloudVision
//
//  Created by Breno Marques on 20/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

protocol RaffleViewControllerDelegate {
    func onDoneRaffle(winner:BMCrowd.Person)
}

class RaffleViewController: UIViewController {
    
    @IBOutlet weak var viewModal: UIView!
    @IBOutlet weak var faceUIImageView: UIImageView!
    var bmCrowd: BMCrowd!
    var delegate : RaffleViewControllerDelegate?
    let amountOfRaffles = 10
    var countReafflesRealised = 0
    var competitors = [BMCrowd.Person]()
    var facesFictures = [UIImage]()
    let main = DispatchQueue.main
    let background = DispatchQueue.global()
    
    override func viewDidLoad() {
        self.startRaffle()
    }
    
    func startRaffle(){
        self.competitors = bmCrowd.getNotWinners()
        doRaffle()
    }
    
    func doRaffleAgain() {
        self.countReafflesRealised += 1
        doRaffle()
    }
    
    func doRaffle()  {
        self.background.async {
            guard let partialWinner = BMRaffle.chooseOne( competitors: self.competitors ) else {
                //TODO: incomplete code
                fatalError("One partialWinner was not found with successful")
            }
            self.main.sync {
                self.hideFaceAnimate(
                    completion: {(finished: Bool) -> Void in
                        self.showFaceAnimate(
                            partialWinner,
                            completion: {
                                (finished: Bool) -> Void in
                                if ( self.isDoneRaffle() == false ){
                                    self.doRaffleAgain()
                                }
                        })
                })
            }
            
            if ( self.isDoneRaffle() ){
                self.main.asyncAfter(deadline: .now() + .milliseconds(2000)) {
                    self.doneRaffle(person: partialWinner)
                }
            }
        }
    }
    
    func doneRaffle(person: BMCrowd.Person) {
        defer { self.dismiss(animated: true, completion: nil) }
        if self.delegate != nil {
            self.delegate!.onDoneRaffle(winner: person)
        }
    }
    
    func isLastRaffle() -> Bool {
        return self.countReafflesRealised == self.amountOfRaffles - 1
    }
    
    func isDoneRaffle() -> Bool {
        return self.countReafflesRealised == self.amountOfRaffles
    }
    
    func hideFaceAnimate( completion: @escaping ((Bool) -> Swift.Void)){
        UIView.animate(
            withDuration: 0.1,
            delay: 0.0,
            options: UIViewAnimationOptions.curveEaseOut,
            animations: {
                self.faceUIImageView.alpha = 0.0
        },
            completion: completion
        )
    }
    
    func showFaceAnimate(_ partialWinner:BMCrowd.Person, completion: @escaping ((Bool) -> Swift.Void)){
        self.faceUIImageView.image = getFaceFictureByPerson(partialWinner)
        let duration = self.isLastRaffle() ? 1.0 : 0.4
        UIView.animate(
            withDuration: duration,
            delay: 0.0,
            options: UIViewAnimationOptions.curveEaseIn,
            animations: {
                self.faceUIImageView.alpha = 1.0
        },
            completion: completion
        )
    }
    
    func getFaceFictureByPerson(_ person:BMCrowd.Person) -> UIImage{
        let indexPerson = self.bmCrowd.people.index(of: person)
        return self.facesFictures[indexPerson!]
    }
    
}
