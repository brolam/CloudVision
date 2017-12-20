//
//  BMImageUtilities.swift
//  CloudVision
//
//  Created by Breno Marques on 15/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class BMImageUtilities {
    static func crop(uiImage: UIImage, toRect: CGRect, enlargeWidthInPercent: CGFloat, enlargeHeightInPercent: CGFloat) -> UIImage {
        let enlargeDx = ( ( toRect.width  * enlargeWidthInPercent  ) / 100.00) * -1
        let enlargeDy = ( ( toRect.height * enlargeHeightInPercent ) / 100.00) * -1
        let cropedCGImage = uiImage.cgImage!.cropping(to: toRect.insetBy( dx: enlargeDx, dy: enlargeDy))
        return UIImage(cgImage: cropedCGImage!)
    }
    
    static func normalizeOrientation(_ uiImage: UIImage) -> UIImage {
        if uiImage.imageOrientation == .up {
            return uiImage
        }
        UIGraphicsBeginImageContextWithOptions(uiImage.size, false, uiImage.scale)
        uiImage.draw(in: CGRect(origin: CGPoint.zero, size: uiImage.size))
        let normalizedImage: UIImage? = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return normalizedImage!
    }
}
