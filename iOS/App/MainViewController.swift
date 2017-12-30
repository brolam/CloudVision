//
//  ViewController.swift
//  CloudVision
//
//  Created by Breno Marques on 11/12/2017.
//  Copyright © 2017 Breno Marques. All rights reserved.
//

import UIKit

class MainViewController: UIViewController , UIImagePickerControllerDelegate , UINavigationControllerDelegate, UITableViewDelegate, UITableViewDataSource {
    let main = DispatchQueue.main
    let background = DispatchQueue.global()
    
    @IBOutlet weak var tableCardsView: UITableView!
    @IBOutlet weak var activityIndicatorView: UIActivityIndicatorView!
    let bmFacesDetector = BMFacesDetector()
    
    override func viewDidLoad() {
        navigationItem.rightBarButtonItem = editButtonItem
        self.loadCrowdsAsync()
    }
    
    func doDetectFacesAsync(_ imageFaces: UIImage!) {
        startActivityIndicator()
        self.background.async {
            defer{ self.stopActivityIndicatorMainSync() }
            if ( self.bmFacesDetector.trackFaces(uiImage: imageFaces) ){
                let crowd = self.saveOneCrowd(self.bmFacesDetector)
                self.main.sync {
                    self.performSegue(
                        withIdentifier: "SequeFacesViewController",
                        sender: crowd
                    )
                }
            } else {
                BMAlert.withShortTime(self, keyMessage: "ms_not_find_faces_in_the_picture")
            }
        }
    }
    
    func startActivityIndicator(){
        self.activityIndicatorView.startAnimating()
    }
    
    func stopActivityIndicatorMainSync(){
        self.main.sync { self.activityIndicatorView.stopAnimating() }
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        //TODO: incomplete code
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return BMCrowd.getCrowds().count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let bmCrowdCardView = tableView.dequeueReusableCell(withIdentifier: "BMCrowdCardView", for: indexPath) as! BMCrowdCardView
        let bmCrowd = BMCrowd.getCrowds()[indexPath.item]
        bmCrowdCardView.backgroundUIImage.image = bmCrowd.trackedUIImage
        bmCrowdCardView.titleLabel.text = bmCrowd.title
        return bmCrowdCardView
    }
    
    func tableView(_ tableView: UITableView, willSelectRowAt indexPath: IndexPath) -> IndexPath? {
        let crowd =  BMCrowd.getCrowds()[indexPath.item]
        performSegue(
            withIdentifier: "SequeFacesViewController",
            sender: crowd
        )
        return indexPath
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)
        if ( segue.identifier == "SequeFacesViewController"){
            let facesViewController = segue.destination  as! FacesViewController
            facesViewController.bmCrowd = sender as! BMCrowd
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
            BMAlert.withShortTime(self, keyMessage: "ms_not_valid_picture")
            return
        }
        doDetectFacesAsync(selectedImage)
        dismiss(animated: true, completion: nil)
    }
    
    override func setEditing(_ editing: Bool, animated: Bool) {
        super.setEditing(editing, animated: animated)
        tableCardsView.setEditing(editing, animated: true)
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            self.startActivityIndicator()
            self.background.async {
                defer{ self.stopActivityIndicatorMainSync() }
                let bmCrowd = BMCrowd.getCrowds()[indexPath.row]
                BMCrowd.delete(bmCrowd)
                self.main.sync { tableView.deleteRows(at: [indexPath], with: .fade) }
            }
        }
    }
    
    func loadCrowdsAsync() {
        self.startActivityIndicator()
        self.background.async {
            defer{ self.stopActivityIndicatorMainSync() }
            BMCrowd.loadCrowdsIfNotLoadedYet()
            self.main.sync {
                self.tableCardsView.reloadData()
            }
        }
    }
    
    func saveOneCrowd(_ bmFacesDetector: BMFacesDetector! ) -> BMCrowd {
        let created = Date()
        let people = bmFacesDetector.getFacesLocation().enumerated().map{
            (index, cgRect) in BMCrowd.Person(key: index, faceImageLocation: cgRect, winnerPosition: 0)
        }
        let bmCrowd = BMCrowd(
            title: stringLongDateTime(created),
            created: created,
            trackedUIImage: bmFacesDetector.trackedUIImage,
            people: people
        )
        BMCrowd.add(bmCrowd!)
        self.main.sync {
            self.tableCardsView.reloadData()
            self.scrollToTop()
        }
        return bmCrowd!
    }
    //Source: https://stackoverflow.com/questions/724892/uitableview-scroll-to-the-top
    func scrollToTop() {
        let indexPath = IndexPath(row: 0, section: 0)
        self.tableCardsView.scrollToRow(at: indexPath, at: .top, animated: true)
    }
}

