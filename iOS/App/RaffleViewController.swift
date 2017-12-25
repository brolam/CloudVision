//
//  RaffleViewController.swift
//  CloudVision
//
//  Created by Breno Marques on 20/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class RaffleViewController: UIViewController {
    @IBOutlet weak var viewModal: UIView!
    @IBOutlet weak var faceUIImageView: UIImageView!
    let amountOfRaffles = 10
    var countReafflesRealised = 0
    var competitors = [Int]()
    var facesFictures = [UIImage]()
    let main = DispatchQueue.main
    let background = DispatchQueue.global()
    
    override func viewDidLoad() {
        self.startRaffle()
    }
    
    func startRaffle(){
        self.competitors = [Int](0...facesFictures.count - 1)
        doRaffle()
    }
    
    func doRaffleAgain() {
        self.countReafflesRealised += 1
        doRaffle()
    }
    
    func doRaffle()  {
        self.background.async {
            let partialWinner = BMRaffle.chooseOne( competitors: self.competitors )
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
                    self.doneRaffle(winner: partialWinner)
                }
            }
        }
    }
    
    func doneRaffle(winner: Int) {
        self.dismiss(animated: true, completion: nil)
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
    
    func showFaceAnimate(_ partialWinner:Int, completion: @escaping ((Bool) -> Swift.Void)){
        self.faceUIImageView.image = self.facesFictures[partialWinner]
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
    
}
