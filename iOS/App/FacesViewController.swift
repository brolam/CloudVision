//
//  FacesViewController.swift
//  CloudVision
//
//  Created by Breno Marques on 18/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class FacesViewController: UICollectionViewController, RaffleViewControllerDelegate {
    var bmCrowd: BMCrowd!
    var facesFictures = [UIImage]()
    var winners = [BMCrowd.Person]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.facesFictures = bmCrowd.getFacesPictures()
        self.winners = self.bmCrowd.getWinnersOrdered()
    }
    
    func onDoneRaffle(winner: BMCrowd.Person) {
        self.bmCrowd.setNextWinner(person: winner)
        if ( BMCrowd.persistCrowds() ){
            self.winners = self.bmCrowd.getWinnersOrdered()
            self.collectionView?.reloadData()
        }
    }
    
    override func numberOfSections(in collectionView: UICollectionView) -> Int {
        return winners.count > 0
            ? 2
            : 1
    }
    
    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return ( (winners.count > 0 ) && (section == 0) )
            ? self.winners.count
            : self.facesFictures.count + 1
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        if ( ( winners.count > 0) && (indexPath.section == 0)){
            let winner = self.winners[indexPath.item]
            let indexPerson = self.bmCrowd.people.index(of:winner)
            let oneFaceViewCell = collectionView.dequeueReusableCell(
                withReuseIdentifier: "OneFaceViewCell",
                for: indexPath) as! BMOneFaceViewCell
            oneFaceViewCell.faceUIImageView.image = self.facesFictures[indexPerson!]
            return oneFaceViewCell
        }
        
        if ( indexPath.item < self.facesFictures.count){
            let oneFaceViewCell = collectionView.dequeueReusableCell(
                withReuseIdentifier: "OneFaceViewCell",
                for: indexPath) as! BMOneFaceViewCell
            oneFaceViewCell.faceUIImageView.image = self.facesFictures[indexPath.item]
            return oneFaceViewCell
        } else {
            let trackedImageViewCell = collectionView.dequeueReusableCell(
                withReuseIdentifier: "TrackedImageViewCell",
                for: indexPath) as! BMTrackedImageViewCell
            trackedImageViewCell.uiImageView.image = self.bmCrowd.trackedUIImage
            return trackedImageViewCell
        }
    }
    
    override func collectionView(_ collectionView: UICollectionView,
                                 viewForSupplementaryElementOfKind kind: String,
                                 at indexPath: IndexPath) -> UICollectionReusableView {
        
        let textTitleUILabel = ( ( winners.count > 0) && (indexPath.section == 0))
            ? "Winners"
            : "Everyone"
        let textAmountUILabel = ( ( winners.count > 0) && (indexPath.section == 0))
            ? String(self.winners.count)
            : String(self.facesFictures.count)
        
        switch kind {
        case UICollectionElementKindSectionHeader:
            let facesHearderView = collectionView.dequeueReusableSupplementaryView(
                ofKind: kind,
                withReuseIdentifier: "FacesHearderView",
                for: indexPath
                ) as! BMFacesHearderView
            facesHearderView.titleUILabel.text = textTitleUILabel
            facesHearderView.amountUILabel.text = textAmountUILabel
            return facesHearderView
        default:
            //TODO: incomplete code
            assert(false, "Unexpected element kind")
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if ( segue.identifier == "SequeRaffleViewController"){
            let  raffleViewController = segue.destination  as! RaffleViewController
            raffleViewController.bmCrowd = self.bmCrowd
            raffleViewController.facesFictures = self.facesFictures
            raffleViewController.delegate = self
        }
    }
}
