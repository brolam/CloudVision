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
    
    static func resizeImage(uiImage: UIImage, newSize: CGFloat) -> UIImage {
        let scale = newSize / uiImage.size.width
        let newHeight = uiImage.size.height * scale
        UIGraphicsBeginImageContext(CGSize(width: newSize, height: newHeight))
        uiImage.draw(in: CGRect(x: 0, y: 0, width: newSize, height: newHeight))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage!
    }
    
    static func drawFrameRoundedCorner(_ view: UIView){
        view.layer.shadowColor = UIColor.black.cgColor
        view.layer.shadowOpacity = 1
        view.layer.shadowOffset = CGSize.zero
        view.layer.shadowRadius = 15
        view.layer.shadowPath = UIBezierPath(rect: view.bounds).cgPath
        view.layer.shouldRasterize = true
        view.layer.cornerRadius = 15
        view.layer.masksToBounds = true
    }
    
    static func drawFrameCircle(_ view: UIView){
        let radius = view.frame.width / 2
        view.layer.shadowColor = UIColor.black.cgColor
        view.layer.shadowOpacity = 1
        view.layer.shadowOffset = CGSize.zero
        view.layer.shadowRadius = radius
        view.layer.shadowPath = UIBezierPath(rect: view.bounds).cgPath
        view.layer.cornerRadius = radius
        view.layer.masksToBounds = true
    }
}
