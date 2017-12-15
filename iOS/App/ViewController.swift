//
//  ViewController.swift
//  CloudVision
//
//  Created by Breno Marques on 11/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import UIKit
import GoogleMobileVision

class ViewController: UIViewController {
    var gmvDetector:GMVDetector!
    @IBOutlet weak var uiImageFaces: UIImageView!
    @IBOutlet weak var uiImageOneFace: UIImageView!
    @IBOutlet weak var uiLabelIndex: UILabel!
    var facesRect = [CGRect]()
    var selectedfacesIndex = -1
    
    override func viewDidLoad() {
        super.viewDidLoad()
        gmvDetector = GMVDetector(ofType: GMVDetectorTypeFace, options:[
            GMVDetectorFaceLandmarkType : "GMVDetectorFaceLandmarkAll'",
            GMVDetectorFaceClassificationType : "GMVDetectorFaceClassificationAll",
            GMVDetectorFaceMinSize : "(0.3)",
            GMVDetectorFaceTrackingEnabled : "NO"
            ])
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    fileprivate func doDetectFaces(_ imageFaces: UIImage!) {
        facesRect = [CGRect]()
        if (gmvDetector != nil) {
            if let faces =  gmvDetector.features( in: imageFaces, options: nil){
                NSLog("Detected %lu face(s).", faces.count )
                var item = 0
                for face in faces{
                    let rect:CGRect = face.bounds;
                    facesRect.append(rect)
                    print("item \(item) origin \( rect.origin.debugDescription  ) size : \(rect.size.debugDescription)")
                    item = item + 1
                }
            }
            if ( !facesRect.isEmpty ){
                selectedfacesIndex = 0
                showOneFace(imageFaces: imageFaces, index: selectedfacesIndex)
            }
        } else {
            NSLog("Detected %s.", "Error" );
        }
    }
    
    @IBAction func onDetectCrowd01(_ sender: UIButton) {
        uiImageFaces.image = UIImage(named: "crowd-test-01.png")
        if let imageFaces = uiImageFaces.image {
            doDetectFaces(imageFaces)
        }
        
    }
    
    @IBAction func onDetectCrowd02(_ sender: UIButton) {
        uiImageFaces.image = UIImage(named: "crowd-test-02.jpg")
        if let imageFaces = uiImageFaces.image {
            doDetectFaces(imageFaces)
        }
    }
    
    @IBAction func onBackOnFace(_ sender: Any) {
        if let imageFaces = uiImageFaces.image, selectedfacesIndex > -1 {
            if ( selectedfacesIndex > 0) {
                selectedfacesIndex = selectedfacesIndex  - 1
            } else {
                selectedfacesIndex = (facesRect.count - 1 )
            }
            showOneFace(imageFaces: imageFaces, index: selectedfacesIndex)
        }
    }
    
    @IBAction func onNextFace(_ sender: Any) {
        if let imageFaces = uiImageFaces.image, selectedfacesIndex > -1 {
            if ( selectedfacesIndex + 1 < facesRect.count){
                selectedfacesIndex = selectedfacesIndex  + 1
            } else {
                selectedfacesIndex = 0
            }
            showOneFace(imageFaces: imageFaces, index: selectedfacesIndex)
        }
    }
    
    func showOneFace( imageFaces: UIImage, index :Int) {
        /*
        //twenty percent in width
        let dx = ((facesRect[index].width * 20.00) / 100.00) * -1
        //thirty percent in height
        let dy = ((facesRect[index].height * 30.00) / 100.00) * -1
        let rect =  facesRect[index].insetBy( dx: dx, dy: dy)
        */
        uiImageOneFace.image = BMImageUtilities.crop(
            uiImage: imageFaces,
            toRect: facesRect[index],
            enlargeWidthInPercent: 20,
            enlargeHeightInPercent: 30)
        uiLabelIndex.text = "Index: \(index + 1) /  \(facesRect.count - 1) "
    }
    
    func cropImage(image: UIImage, toRect: CGRect) -> UIImage? {
        // Cropping is available trhough CGGraphics
        let cgImage :CGImage! = image.cgImage
        let croppedCGImage: CGImage! = cgImage.cropping(to: toRect)
        
        return UIImage(cgImage: croppedCGImage)
    }
}

