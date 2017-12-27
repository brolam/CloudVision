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
        BMCrowd.persistCrowds()
        self.winners = self.bmCrowd.getWinnersOrdered()
        self.collectionView?.reloadData()
    }
    
    override func numberOfSections(in collectionView: UICollectionView) -> Int {
        return hasWinners()
            ? 2
            : 1
    }
    
    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return isWinnerSection(section)
            ? self.winners.count
            : self.facesFictures.count + 1
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        if ( isWinnerSection(indexPath.section) ){
            return getOneWinnerCell(indexPath, collectionView)
        } else if isPeopleSection(indexPath){
            return getOneFaceViewCell(collectionView, indexPath)
        } else {
            return getTrackedImageViewCell(collectionView, indexPath)
        }
    }
    
    override func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        return getFacesHearderView(indexPath, collectionView, kind)
    }
    
    override func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let showPictureUIImage = getFaceOfCellSelected(indexPath)
        performSegue(
            withIdentifier: "SegueShowPictureModal",
            sender: showPictureUIImage
        )
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if ( identifier == "SequeRaffleViewController"){
            let notWinners = self.bmCrowd.getNotWinners()
            return RaffleViewController.parse(self, competidors: notWinners)
        }
        return true
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if ( segue.identifier == "SequeRaffleViewController"){
            let  raffleViewController = segue.destination  as! RaffleViewController
            raffleViewController.delegate = self
        } else if ( segue.identifier == "SegueShowPictureModal"){
            let  showPictureController = segue.destination  as! ShowPictureController
            showPictureController.setPictureImage(uiImage: sender as! UIImage)
        }
    }
    
    fileprivate func hasWinners() -> Bool{
        return self.winners.count > 0
    }
    
    fileprivate func isWinnerSection(_ section: Int) -> Bool {
        return  ( hasWinners() && (section == 0) )
    }
    
    fileprivate func isPeopleSection(_ indexPath: IndexPath) -> (Bool) {
        return ( indexPath.item < self.facesFictures.count )
    }
    
    fileprivate func getFacesHearderView(_ indexPath: IndexPath, _ collectionView: UICollectionView, _ kind: String) -> UICollectionReusableView {
        let (textTitleUILabel, textAmountUILabel)  = isWinnerSection(indexPath.section )
            ? ("Winners" , String(self.winners.count))
            : ("Everyone", String(self.facesFictures.count) )
        
        let facesHearderView = collectionView.dequeueReusableSupplementaryView(
            ofKind: kind,
            withReuseIdentifier: "FacesHearderView",
            for: indexPath
            ) as! BMFacesHearderView
        facesHearderView.titleUILabel.text = textTitleUILabel
        facesHearderView.amountUILabel.text = textAmountUILabel
        return facesHearderView
    }
    
    fileprivate func getOneWinnerCell(_ indexPath: IndexPath, _ collectionView: UICollectionView) -> UICollectionViewCell {
        let winner = self.winners[indexPath.item]
        let indexPerson = self.bmCrowd.people.index(of:winner)
        let oneFaceViewCell = collectionView.dequeueReusableCell(
            withReuseIdentifier: "OneFaceViewCell",
            for: indexPath) as! BMOneFaceViewCell
        oneFaceViewCell.faceUIImageView.image = self.facesFictures[indexPerson!]
        return oneFaceViewCell
    }
    
    fileprivate func getOneFaceViewCell(_ collectionView: UICollectionView, _ indexPath: IndexPath) -> UICollectionViewCell {
        let oneFaceViewCell = collectionView.dequeueReusableCell(
            withReuseIdentifier: "OneFaceViewCell",
            for: indexPath) as! BMOneFaceViewCell
        oneFaceViewCell.faceUIImageView.image = self.facesFictures[indexPath.item]
        return oneFaceViewCell
    }
    
    fileprivate func getTrackedImageViewCell(_ collectionView: UICollectionView, _ indexPath: IndexPath) -> UICollectionViewCell {
        let trackedImageViewCell = collectionView.dequeueReusableCell(
            withReuseIdentifier: "TrackedImageViewCell",
            for: indexPath) as! BMTrackedImageViewCell
        trackedImageViewCell.uiImageView.image = self.bmCrowd.trackedUIImage
        return trackedImageViewCell
    }
    
    fileprivate func getFaceOfCellSelected(_ indexPath: IndexPath) -> UIImage {
        if ( isWinnerSection(indexPath.section) ){
            let winner = self.winners[indexPath.item]
            let indexPerson = self.bmCrowd.people.index(of:winner)
            return self.facesFictures[indexPerson!]
        } else if ( isPeopleSection(indexPath) ) {
            return self.facesFictures[indexPath.item]
        } else {
            return self.bmCrowd.trackedUIImage
        }
    }
}
