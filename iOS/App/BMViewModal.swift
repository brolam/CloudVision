//
//  BMViewModal.swift
//  CloudVision
//
//  Created by Breno Marques on 21/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class BMViewModal: UIView {
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        BMImageUtilities.drawFrameRoundedCorner(self)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        BMImageUtilities.drawFrameRoundedCorner(self)
    }
}
