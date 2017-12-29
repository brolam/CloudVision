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
    var bmCrowd: BMCrowd!  { get }
    var facesFictures: [UIImage]  { get }
    func onDoneRaffle(winner:BMCrowd.Person)
}

class RaffleViewController: UIViewController {
    
    @IBOutlet weak var viewModal: UIView!
    @IBOutlet weak var faceUIImageView: UIImageView!
    var delegate : RaffleViewControllerDelegate!
    let amountOfRaffles = 10
    var countReafflesRealised = 0
    var competitors = [BMCrowd.Person]()
    let main = DispatchQueue.main
    let background = DispatchQueue.global()
    
    static func parse(_ faffleViewControllerDelegate: UIViewController & RaffleViewControllerDelegate, competidors:[BMCrowd.Person]) -> Bool{
        if ( competidors.count <= 1){
            BMAlert.withShortTime(faffleViewControllerDelegate, keyMessage: "ms_all_raffles_been_made")
            if ( competidors.count == 1){
                faffleViewControllerDelegate.onDoneRaffle(winner: competidors[0])
            }
            return false
        }
        return true
    }
    
    override func viewDidLoad() {
        self.faceUIImageView.image = nil
        BMImageUtilities.drawCircleFrame(self.faceUIImageView)
        BMImageUtilities.drawCircleFrame(self.viewModal)
        self.startRaffle()
    }
    
    func startRaffle(){
        self.competitors = self.delegate.bmCrowd.getNotWinners()
        doRaffle()
    }
    
    func doRaffleAgain() {
        self.countReafflesRealised += 1
        doRaffle()
    }
    
    func doRaffle()  {
        self.background.async {
            let partialWinner = BMRaffle.chooseOne( competitors: self.competitors )!
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
        let indexPerson = self.delegate.bmCrowd.people.index(of: person)
        return self.delegate.facesFictures[indexPerson!]
    }
    
}
