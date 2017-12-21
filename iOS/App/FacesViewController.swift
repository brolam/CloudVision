//
//  FacesViewController.swift
//  CloudVision
//
//  Created by Breno Marques on 18/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class FacesViewController: UICollectionViewController {
    var bmFacesDetector:BMFacesDetector!
    var facesFictures = [UIImage]()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.facesFictures = bmFacesDetector.getFacesPictures()
    }
    
    
    override func numberOfSections(in collectionView: UICollectionView) -> Int {
        //TODO: incomplete code
        return 1
    }
    
    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
       //TODO: incomplete code
        return self.facesFictures.count + 1
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
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
            trackedImageViewCell.uiImageView.image = self.bmFacesDetector.trackedUIImage
            return trackedImageViewCell
            
        }
    }
    
    override func collectionView(_ collectionView: UICollectionView,
                                 viewForSupplementaryElementOfKind kind: String,
                                 at indexPath: IndexPath) -> UICollectionReusableView {
        
        switch kind {
        case UICollectionElementKindSectionHeader:
            let facesHearderView = collectionView.dequeueReusableSupplementaryView(
                ofKind: kind,
                withReuseIdentifier: "FacesHearderView",
                for: indexPath
                ) as! BMFacesHearderView
            facesHearderView.titleUILabel.text = "Everyone"
            return facesHearderView
        default:
            //TODO: incomplete code
            assert(false, "Unexpected element kind")
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if ( segue.identifier == "SequeRaffleViewController"){
            let  raffleViewController = segue.destination  as! RaffleViewController
            raffleViewController.facesFictures = self.facesFictures
        }
    }
}
