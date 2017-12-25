//
//  BMAlert.swift
//  CloudVision
//
//  Created by Breno Marques on 25/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import Foundation
import UIKit

class BMAlert {
    static func withShortTime(_ viewController: UIViewController,_ message:String){
        let main = DispatchQueue.main
        let alert = UIAlertController(
            title: "CloudVision",
            message: message,
            preferredStyle: .alert
        )
        viewController.present(alert, animated: true, completion: nil)
        main.asyncAfter(deadline: .now() + .milliseconds(2000)) {
            alert.dismiss(animated: true, completion: nil)
        }
    }
}
