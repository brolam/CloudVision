//
//  ViewController.swift
//  CloudVision
//
//  Created by Breno Marques on 11/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import UIKit

class MainViewController: UIViewController , UIImagePickerControllerDelegate , UINavigationControllerDelegate {
    let bmFacesDetector = BMFacesDetector()
    @IBOutlet weak var uiImageFaces: UIImageView!
    @IBOutlet weak var uiImageOneFace: UIImageView!
    @IBOutlet weak var uiLabelIndex: UILabel!
    var facesFictures = [UIImage]()
    var selectedfacesIndex = -1
    
    fileprivate func doDetectFaces(_ imageFaces: UIImage!) {
        self.bmFacesDetector.trackFaces(uiImage: imageFaces)
        self.facesFictures = self.bmFacesDetector.getFacesPictures()
        if ( !facesFictures.isEmpty ){
            self.selectedfacesIndex = 0
            showOneFace(index: selectedfacesIndex)
        } else {
            self.selectedfacesIndex = -1
            NSLog("Detected %s.", "Error" );
        }
    }
    
    @IBAction func onTapPictureLibraryButton(_ sender: UIBarButtonItem) {
        let imagePickerController = UIImagePickerController()
        imagePickerController.sourceType = .photoLibrary
        imagePickerController.delegate = self
        present(imagePickerController, animated: true, completion: nil)
    }
    
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController){
        dismiss(animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]){
        guard let selectedImage = info[UIImagePickerControllerOriginalImage] as? UIImage else {
            fatalError("Expected a dictionary containing an image, but was provided the following: \(info)")
        }
        uiImageFaces.image = selectedImage
        doDetectFaces(selectedImage)
        dismiss(animated: true, completion: nil)
    }
    
    
    @IBAction func onDetectCrowd01(_ sender: UIButton) {
        uiImageFaces.image = UIImage(named: "crowd-test-01.png")!
        if let imageFaces = uiImageFaces.image {
            doDetectFaces(imageFaces)
        }
        
    }
    
    @IBAction func onDetectCrowd02(_ sender: UIButton) {
        self.uiImageFaces.image = UIImage(named: "crowd-test-02.jpg")
        if let imageFaces = self.uiImageFaces.image {
            self.doDetectFaces(imageFaces)
        }
    }
    
    @IBAction func onBackOnFace(_ sender: Any) {
        if  self.selectedfacesIndex > -1 {
            if ( selectedfacesIndex > 0) {
                self.selectedfacesIndex = self.selectedfacesIndex  - 1
            } else {
                self.selectedfacesIndex = (self.facesFictures.count - 1 )
            }
            self.showOneFace(index: selectedfacesIndex)
        }
    }
    
    @IBAction func onNextFace(_ sender: Any) {
        if (self.selectedfacesIndex > -1 ) {
            if ( self.selectedfacesIndex + 1 < self.facesFictures.count){
                self.selectedfacesIndex = self.selectedfacesIndex  + 1
            } else {
                self.selectedfacesIndex = 0
            }
            self.showOneFace( index: selectedfacesIndex)
        }
    }
    
    func showOneFace( index :Int) {
        self.uiImageOneFace.image = self.facesFictures[index]
        self.uiLabelIndex.text = "Index: \(index + 1) /  \(self.facesFictures.count ) "
    }
}

