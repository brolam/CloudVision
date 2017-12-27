//
//  BMImagePicker.swift
//  CloudVision
//
//  Created by Breno Marques on 19/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit
import AVFoundation
import Photos

class BMImagePicker{
    #if DEBUG
    static let FAKER_IMAGE_SELECTED = "fakerImageSelected"
    #endif
    var viewController:
    UIViewController &
    UIImagePickerControllerDelegate &
    UINavigationControllerDelegate
    var imagePickerController: UIImagePickerController!
    
    init(delegate: UIViewController & UIImagePickerControllerDelegate & UINavigationControllerDelegate,
         sourceType: UIImagePickerControllerSourceType) {
        self.viewController = delegate
        self.imagePickerController = UIImagePickerController()
        imagePickerController.delegate = self.viewController
        #if arch(i386) || arch(x86_64) // the device is a Simulator and do not support camera.
            imagePickerController.sourceType = .photoLibrary
        #else
            imagePickerController.sourceType = sourceType
        #endif
    }
    
    func show(){
        #if DEBUG
            if let fakerImage =  ProcessInfo.processInfo.environment[BMImagePicker.FAKER_IMAGE_SELECTED] {
                let info:[String : Any] = [UIImagePickerControllerOriginalImage: UIImage(named: fakerImage) as Any]
                self.viewController.imagePickerController!(self.imagePickerController, didFinishPickingMediaWithInfo: info)
                return
            }
        #endif
        
        if ( imagePickerController.sourceType == .photoLibrary && PHPhotoLibrary.authorizationStatus() == .denied) {
            BMAlert.withShortTime(viewController, "Go into iPhone settings and give photo library permission to CloudVision.")
            return
        }
        
        if (   imagePickerController.sourceType == .camera && AVCaptureDevice.authorizationStatus(for: .video) ==  .denied ) {
            BMAlert.withShortTime(viewController, "Go into iPhone settings and give camera permission to CloudVision.")
            return
        }
        
        viewController.present(imagePickerController, animated: true, completion: nil)
    }
    
    
    
}
