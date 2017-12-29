//
//  BMOneFaceViewCell.swift
//  CloudVision
//
//  Created by Breno Marques on 18/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class BMOneFaceViewCell: UICollectionViewCell {
    @IBOutlet fileprivate weak var faceUIImageView: UIImageView!
    
    func setFaceUIImage( uiImage: UIImage){
        self.faceUIImageView.image = uiImage
        BMImageUtilities.drawFrameCircle(self.faceUIImageView)
    }
    
}
