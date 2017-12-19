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
    
    func doDetectFaces(_ imageFaces: UIImage!) {
        self.bmFacesDetector.trackFaces(uiImage: imageFaces)
        performSegue(
            withIdentifier: "SequeFacesViewController",
            sender: self
        )
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if ( segue.identifier == "SequeFacesViewController"){
            let facesViewController = segue.destination  as! FacesViewController
            facesViewController.bmFacesDetector = self.bmFacesDetector
        }
    }
    
    @IBAction func onTapPictureLibraryButton(_ sender: UIBarButtonItem) {
        let imagePicker =  BMImagePicker(delegate: self, sourceType: .photoLibrary)
        imagePicker.show()
    }
    
    @IBAction func onTapCameraButton(_ sender: UIBarButtonItem) {
        let imagePicker =  BMImagePicker(delegate: self, sourceType: .camera)
        imagePicker.show()
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController){
        dismiss(animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]){
        guard let selectedImage = info[UIImagePickerControllerOriginalImage] as? UIImage else {
            //TODO: incomplete code
            fatalError("Expected a dictionary containing an image, but was provided the following: \(info)")
        }
        doDetectFaces(selectedImage)
        dismiss(animated: true, completion: nil)
    }
}

