/*
 * Copyright (C) The Android Open Source Project
 * https://github.com/googlesamples/android-vision
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.brolam.cloudvision.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.ui.camera.CameraSource;
import br.com.brolam.cloudvision.ui.camera.CameraSourcePreview;
import br.com.brolam.cloudvision.ui.camera.GraphicOverlay;
import br.com.brolam.cloudvision.ui.vision.OcrDetectorProcessor;
import br.com.brolam.cloudvision.ui.vision.OcrGraphic;

/**
 * Essa atividade é uma modificação da atividade OcrCaptureActivity.java em https://github.com/googlesamples/android-vision/blob/master/visionSamples/ocr-codelab/
 * responsável pelo o acionamento da camera fotográfica e capturar dos blocos de textos.
 * Sendo importante destacar, que foram realizadas modificações para atender as necessidades do Cloud Vision.
 */
public class NoteVisionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NoteVisionActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    public static final String UseFlash = "UseFlash";
    private boolean useFlash;
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private Toolbar toolbar;
    private FloatingActionButton fabFlashOnOff;
    private FloatingActionButton fabCameraPlayStop;
    private View contentNoteVisionCamera;
    private View contentNoteVisionKeyboard;
    private EditText editTextContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_vision);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);
        this.fabFlashOnOff = (FloatingActionButton) this.findViewById(R.id.fabFlashOnOff);
        this.fabCameraPlayStop = (FloatingActionButton) this.findViewById(R.id.fabCameraPlayStop);
        this.contentNoteVisionCamera = this.findViewById(R.id.contentNoteVisionCamera);
        this.contentNoteVisionKeyboard = this.findViewById(R.id.contentNoteVisionKeyboard);
        this.editTextContent = (EditText) this.findViewById(R.id.editTextContent);

        // read parameters from the intent used to launch the activity.
        if ( savedInstanceState != null){
            this.useFlash = savedInstanceState.getBoolean(UseFlash, false);
        } else {
            this.useFlash = getIntent().getBooleanExtra(UseFlash, false);
        }


        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(UseFlash, useFlash);
    }

    public static void newNoteVision(Activity  activity, int requestCod){
        Intent intent = new Intent(activity, NoteVisionActivity.class );
        intent.putExtra(UseFlash, false);
        activity.startActivityForResult(intent, requestCod);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_note_vision, menu);
        return true;
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource() {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }


        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                        .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            keyboardOnOff(false);
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
                this.fabCameraPlayStop.setImageResource(R.drawable.ic_pause_camera_white);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
                this.fabCameraPlayStop.setImageResource(R.drawable.ic_play_camera_white);
            }
        }
    }

    /**
     * Play ou Pause do preview da camera fotográfica.
     * @param play informar true para iniciar o preview da camera ou false para Pause,
     *             se for informado null, será invertido a situação atual do preview.
     */
    private void cameraPlayPause(Boolean play){
        if ( mPreview != null) {
            play =  play == null? mPreview.isPaused(): play;
            if (play) {
                startCameraSource();
            } else {
                mPreview.stop();
                this.fabCameraPlayStop.setImageResource(R.drawable.ic_play_camera_white);
            }
        }
    }

    /**
     * Ativar ou desativar o flash da camera fotográfica
     * @param flashOn informar true para ativar o flash ou false para desativar,
     *             se for informado null, será invertido a situação atual do flash.
     */
    private void cameraFlashOnOff(Boolean flashOn){
        flashOn = flashOn == null? mCameraSource.getFlashMode() != Camera.Parameters.FLASH_MODE_TORCH: flashOn;
        if ( flashOn){
            this.mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            this.fabFlashOnOff.setImageResource(R.drawable.ic_off_flash_white);
            this.useFlash = true;
        } else {
            this.mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            this.fabFlashOnOff.setImageResource(R.drawable.ic_on_flash_white);
            this.useFlash = false;
        }
    }


    /**
     * Ativar ou desativar o teclado para editar os blocos de textos do note vision.
     * @param keyboardOn informar true para ativar o teclado ou false para desativar,
     *             se for informado null, será invertido a situação atual do teclado.
     */
    private void keyboardOnOff(Boolean keyboardOn){
        keyboardOn = keyboardOn == null? this.contentNoteVisionKeyboard.getVisibility() != View.VISIBLE : keyboardOn;
        MenuItem menuItemNoteVision =  toolbar.getMenu().findItem(R.id.note_vision_keyboard_or_camera);
        if (keyboardOn) {
            this.contentNoteVisionKeyboard.setVisibility(View.VISIBLE);
            if ( mPreview != null) {
                cameraPlayPause(false);
                contentNoteVisionCamera.setVisibility(View.GONE);
                contentNoteVisionKeyboard.setVisibility(View.VISIBLE);
            }
            if ( menuItemNoteVision != null) {
                menuItemNoteVision
                        .setIcon(R.drawable.ic_on_camera_white)
                        .setTitle(R.string.note_vision_camera_on);
            }

        } else {
            contentNoteVisionCamera.setVisibility(View.VISIBLE);
            contentNoteVisionKeyboard.setVisibility(View.GONE);
            if ( menuItemNoteVision != null) {
                menuItemNoteVision
                        .setIcon(R.drawable.ic_on_keyboard_white)
                        .setTitle(R.string.note_vision_keyboard_on);
            }
        }
    }

    /**
     * Atribuir os blocos de textos selecionados ao editTextContent.
     */
    private void setNoteVisionContent() {
        StringBuilder stringBuilder = new StringBuilder();
        for (OcrGraphic graphic : mGraphicOverlay.getOrcGraphics()) {
            if ((graphic.isSelected())) {
                TextBlock textBlock = graphic.getTextBlock();
                if ((textBlock != null) && (textBlock.getValue() != null)) {
                    stringBuilder.append(String.format("%s\n\r", textBlock.getValue()));
                }
            }
        }
        this.editTextContent.setText(stringBuilder.toString());
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                //Intent data = new Intent();
                //data.putExtra(TextBlockObject, text.getValue());
                //setResult(CommonStatusCodes.SUCCESS, data);
                //finish();
                boolean isSelected = !graphic.isSelected();
                graphic.setSelected(isSelected);
                mPreview.stop();
                mGraphicOverlay.invalidate();
                setNoteVisionContent();
                Log.d(TAG, "Preview Camera Stopped!");
                Log.d(TAG, String.format("Selected == %s \n\r Text: \n\r  %s", graphic.isSelected(), text.getValue()));
            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
           navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.note_vision_keyboard_or_camera) {
            keyboardOnOff(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if ( view.equals(this.fabFlashOnOff)){
          cameraFlashOnOff(null);
        } else  if ( view.equals(this.fabCameraPlayStop)){
            cameraPlayPause(null);
        }

    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }
}
