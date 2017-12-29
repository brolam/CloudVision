//
//  BMSourceImageViewCell.swift
//  CloudVision
//
//  Created by Breno Marques on 18/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation

import Foundation
import UIKit

class BMTrackedImageViewCell: UICollectionViewCell {
    @IBOutlet fileprivate weak var uiImageView: UIImageView!
    
    func setUiImage(_ uiImage: UIImage){
        self.uiImageView.image = uiImage
        BMImageUtilities.drawFrameRoundedCorner(self.uiImageView)
    }
    
    
    
}
