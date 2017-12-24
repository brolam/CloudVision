//
//  AppDelegate.swift
//  CloudVision
//
//  Created by Breno Marques on 11/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        //Always delete all information to not modify the UI testing environment.
        if ( ProcessInfo.processInfo.arguments.contains("XCTestCase")){
            BMCrowd.deleteAll()
        }
        return true
    }
}

