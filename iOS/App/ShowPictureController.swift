//
//  ShowPictureController.swift
//  CloudVision
//
//  Created by Breno Marques on 26/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class ShowPictureController : UIViewController {
    @IBOutlet weak var pictureUIImageView: UIImageView!
    var pictureUIImage : UIImage!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.pictureUIImageView.image = self.pictureUIImage
    }
    
    func setPictureImage(uiImage: UIImage){
        self.pictureUIImage = uiImage
    }
    
    @IBAction func done(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
}

