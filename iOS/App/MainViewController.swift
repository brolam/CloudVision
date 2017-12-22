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
        if ( self.bmFacesDetector.trackFaces(uiImage: imageFaces) ){
            guard let crowd = self.saveOneCrowd(self.bmFacesDetector) else {
                //TODO: incomplete code
                fatalError("One crowd was not saved with successful")
            }
            performSegue(
                withIdentifier: "SequeFacesViewController",
                sender: crowd
            )
            
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if ( segue.identifier == "SequeFacesViewController"){
            let facesViewController = segue.destination  as! FacesViewController
            facesViewController.bmCrowf = sender as! BMCrowd
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
    
    func saveOneCrowd(_ bmFacesDetector: BMFacesDetector! ) -> BMCrowd? {
        let created = Date()
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .full
        dateFormatter.timeStyle = .medium
        let people = bmFacesDetector.getFacesLocation().enumerated().map{
            (index, cgRect) in BMCrowd.Person(key: index, faceImageLocation: cgRect, winnerPosition: 0)
        }
        let bmCrowd = BMCrowd(
            title: dateFormatter.string(from: created),
            created: created,
            trackedUIImage: bmFacesDetector.trackedUIImage,
            people: people
        )
        
        return BMCrowd.save(crowds: [bmCrowd!]) ? bmCrowd : nil
    }
}

