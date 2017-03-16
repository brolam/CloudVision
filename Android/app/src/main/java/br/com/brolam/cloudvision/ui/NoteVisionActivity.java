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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
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
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.CloudVisionProvider;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.data.models.NoteVisionItem;
import br.com.brolam.cloudvision.ui.camera.CameraSource;
import br.com.brolam.cloudvision.ui.camera.CameraSourcePreview;
import br.com.brolam.cloudvision.ui.camera.GraphicOverlay;
import br.com.brolam.cloudvision.ui.helpers.ActivityHelper;
import br.com.brolam.cloudvision.ui.helpers.AppAnalyticsHelper;
import br.com.brolam.cloudvision.ui.helpers.LoginHelper;
import br.com.brolam.cloudvision.ui.vision.OcrDetectorProcessor;
import br.com.brolam.cloudvision.ui.vision.OcrGraphic;
import br.com.brolam.cloudvision.ui.widgets.NoteVisionSummaryWidget;

/**
 * Essa atividade é uma modificação da atividade OcrCaptureActivity.java em https://github.com/googlesamples/android-vision/blob/master/visionSamples/ocr-codelab/
 * responsável pelo o acionamento da camera fotográfica e capturar dos blocos de textos.
 * Sendo importante destacar, que foram realizadas modificações para atender as necessidades do Cloud Vision.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionActivity extends AppCompatActivity implements View.OnClickListener, LoginHelper.ILoginHelper {
    private static final String TAG = "NoteVisionActivity";

    public static final String USE_FLASH = "useFlash";
    private boolean useFlash;

    public static final String ON_KEYBOARD = "onKeyboard";
    private boolean onKeyboard;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private Toolbar toolbar;
    private FloatingActionButton fabFlashOnOff;
    private FloatingActionButton fabCameraPlayStop;
    private FloatingActionButton fabAdd;
    private View contentNoteVisionCamera;
    private View contentNoteVisionKeyboard;
    private EditText editTextTitle;
    private EditText editTextContent;

    private LoginHelper loginHelper;
    private CloudVisionProvider  cloudVisionProvider;
    private AppAnalyticsHelper appAnalyticsHelper;

    public static final String NOTE_VISION_KEY = "noteVisionKey";
    private String noteVisionKey;
    public static final String NOTE_VISION_ITEM_KEY = "noteVisionItemKey";
    private String noteVisionItemKey;

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
        this.fabAdd = (FloatingActionButton) this.findViewById(R.id.fabAdd);
        this.contentNoteVisionCamera = this.findViewById(R.id.contentNoteVisionCamera);
        this.contentNoteVisionKeyboard = this.findViewById(R.id.contentNoteVisionKeyboard);
        this.editTextTitle = (EditText) this.findViewById(R.id.editTextTitle);
        this.editTextContent = (EditText) this.findViewById(R.id.editTextContent);

        setSaveInstanceState(savedInstanceState);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            ActivityHelper.requestCameraPermission(TAG, this, mGraphicOverlay);
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

         /*
         * Criar um LoginHelper para registrar o login do usuário no aplicativo.
         * Veja os métodos onResume, onPause e onActivityResult para mais detalhes
         * sobre o fluxo de registro do usuário.
         */
        this.loginHelper = new LoginHelper(this, null, this);

    }

    private void setSaveInstanceState(Bundle savedInstanceState){
        // read parameters from the  savedInstanceState ou intent used to launch the activity.
        Bundle bundle = savedInstanceState != null? savedInstanceState : getIntent().getExtras();
        if ( bundle != null) {
            this.useFlash = bundle.getBoolean(USE_FLASH, false);
            this.onKeyboard = bundle.getBoolean(ON_KEYBOARD, false);
            this.noteVisionKey = bundle.getString(NOTE_VISION_KEY, null);
            this.noteVisionItemKey = bundle.getString(NOTE_VISION_ITEM_KEY, null);
            this.editTextTitle.setText(bundle.getString(NoteVision.TITLE,this.editTextTitle.getText().toString()));
            this.editTextContent.setText(bundle.getString(NoteVisionItem.CONTENT,this.editTextContent.getText().toString()));
        } else {
            this.useFlash =  false;
            this.onKeyboard = false;
            this.noteVisionKey = null;
            this.noteVisionItemKey = null;
        }
    }

    /**
     * Ativar o provedor de dados se o login do usuário for realizado com sucesso.
     * @param firebaseUser  informar um usuário válido.
     */
    @Override
    public void onLogin(FirebaseUser firebaseUser) {
        if ( this.cloudVisionProvider == null) {
            this.cloudVisionProvider = new CloudVisionProvider(firebaseUser.getUid());
            this.appAnalyticsHelper = new AppAnalyticsHelper(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(USE_FLASH, this.useFlash);
        outState.putBoolean(ON_KEYBOARD, this.onKeyboard);
        outState.putString(NOTE_VISION_KEY, this.noteVisionKey);
        outState.putString(NOTE_VISION_ITEM_KEY, this.noteVisionItemKey);
        outState.putString(NoteVision.TITLE, editTextTitle.getText().toString());
        outState.putString(NoteVisionItem.CONTENT, editTextContent.getText().toString());
    }

    @Override
    public void onBackPressed() {
        this.setLockScreenOrientation(false);
        if ( this.noteVisionKey != null){
            Intent data = new Intent();
            data.putExtra(NOTE_VISION_KEY, this.noteVisionKey);
            setResult(0,data);
            this.finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Incluir um NoteVision e seu conteúdo
     * @param activity informar uma atividade válida
     * @param requestCod informar o código de requisição da atividade {@see Activity.onActivityResult}
     */
    public static void newNoteVision(Activity  activity, int requestCod){
        Intent intent = new Intent(activity, NoteVisionActivity.class );
        activity.startActivityForResult(intent, requestCod);

    }

    /**
     * Atualizar um NoteVision e seu conteúdo(item)
     * @param activity informar uma atividade válida
     * @param requestCod informar o código de requisição da atividade {@see Activity.onActivityResult}
     * @param noteVisionKey informar um chave válida.
     * @param noteVisionItemKey informar um chave válida.
     * @param title informar o título do NoteVision
     * @param content informar o conteúdo do item do Notevision
     * @param onKeyboard informar se o teclado deve ser ativiado em vez da camera.
     */
    public static void updateNoteVision(Activity  activity, int requestCod, String noteVisionKey, String noteVisionItemKey , String title , String content, Boolean onKeyboard) {
        Intent intent = new Intent(activity, NoteVisionActivity.class);
        intent.putExtra(NOTE_VISION_KEY, noteVisionKey);
        intent.putExtra(NOTE_VISION_ITEM_KEY, noteVisionItemKey);
        intent.putExtra(NoteVision.TITLE, title);
        intent.putExtra(NoteVisionItem.CONTENT, content);
        intent.putExtra(ON_KEYBOARD, onKeyboard);
        activity.startActivityForResult(intent, requestCod);

    }

    /**
     * Adicionar um conteúdo ao NoteVision
     * @param activity informar uma atividade válida
     * @param requestCod informar o código de requisição da atividade {@see Activity.onActivityResult}
     * @param noteVisionKey informar um chave válida.
     * @param title informar o título do NoteVision
     * @param onKeyboard informar se o teclado deve ser ativiado em vez da camera.
     */
    public static void addNoteVisionContent(Activity  activity, int requestCod, String noteVisionKey, String title , Boolean onKeyboard) {
        updateNoteVision(activity, requestCod, noteVisionKey, null, title, null,onKeyboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_note_vision, menu);
        //Atualizar o menu quando a tela for reconstruida.
        setMenuItemNoteVisionKeyboardOrCamera(this.onKeyboard);
        return true;
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
        this.loginHelper.begin();
        //Acionar o teclado ou camera conforme a situação antes da tela ser reconstruída ou
        //se a atividade foi acionada com parâmetro onKeyboard = true
        if ( this.onKeyboard )
            this.keyboardOnOff(true);
        else {
            this.startCameraSource();
        }
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        this.loginHelper.pause();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Validar se o login do usuário foi realizado com sucess.
         * Sendo importante destacar, se o login for cancelado a MainActivity será encerrada!
         */
        if ( loginHelper.checkLogin(requestCode, resultCode)){

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
        if (requestCode != ActivityHelper.RC_HANDLE_CAMERA_PERM) {
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
        builder.setTitle(R.string.app_name)
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
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, ActivityHelper.RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            keyboardOnOff(false);
            try {
                this.setLockScreenOrientation(false);
                mPreview.start(mCameraSource, mGraphicOverlay);
                this.fabCameraPlayStop.setImageResource(R.drawable.ic_pause_camera_white);
                this.fabCameraPlayStop.setContentDescription(getString(R.string.talk_back_camera_pause));
                this.fabFlashOnOff.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
                this.fabCameraPlayStop.setImageResource(R.drawable.ic_play_camera_white);
            }
        }
    }

    /**
     * Informar se a camera está ativa.
     * @return
     */
    private boolean isCameraPlay() {
        if  ( mPreview != null ){
            return !mPreview.isPaused();
        }
        return  false;
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
                this.fabCameraPlayStop.setContentDescription(getString(R.string.talk_back_camera_play));
                this.fabFlashOnOff.setVisibility(View.INVISIBLE);
                this.setLockScreenOrientation(true);

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
            this.fabFlashOnOff.setContentDescription(getString(R.string.talk_back_camera_flash_off));
            this.useFlash = true;
        } else {
            this.mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            this.fabFlashOnOff.setImageResource(R.drawable.ic_on_flash_white);
            this.fabFlashOnOff.setContentDescription(getString(R.string.talk_back_camera_flash_on));
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
        if (keyboardOn) {
            this.contentNoteVisionKeyboard.setVisibility(View.VISIBLE);
            if ( mPreview != null) {
                cameraPlayPause(false);
                contentNoteVisionCamera.setVisibility(View.GONE);
                contentNoteVisionKeyboard.setVisibility(View.VISIBLE);
            }
            setMenuItemNoteVisionKeyboardOrCamera(true);
            this.onKeyboard = true;
            this.setLockScreenOrientation(false);

        } else {
            this.contentNoteVisionCamera.setVisibility(View.VISIBLE);
            this.contentNoteVisionKeyboard.setVisibility(View.GONE);
            this.onKeyboard = false;
            setMenuItemNoteVisionKeyboardOrCamera(false);
        }
    }

    /**
     * Atualizar item note_vision_keyboard_or_camera no menu do toolbar.
     * @param onKeyboard
     */
    private void setMenuItemNoteVisionKeyboardOrCamera(boolean onKeyboard){
        MenuItem menuItemNoteVision =  toolbar.getMenu().findItem(R.id.note_vision_keyboard_or_camera);
        if ( onKeyboard){
            if ( menuItemNoteVision != null) {
                menuItemNoteVision
                        .setIcon(R.drawable.ic_on_camera_white)
                        .setTitle(R.string.note_vision_camera_on);
            }
        } else{
            if ( menuItemNoteVision != null) {
                menuItemNoteVision
                        .setIcon(R.drawable.ic_on_keyboard_white)
                        .setTitle(R.string.note_vision_keyboard_on);
            }
            showOrHideFabAdd();

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
                    stringBuilder.append(String.format("%s ", textBlock.getValue()));
                }
            }
        }
        this.editTextContent.setText(stringBuilder.toString());
        showOrHideFabAdd();
    }

    /**
     * Novo Note Vision Item.
     */
    private void newNoteVisionContent() {
        for (OcrGraphic graphic : mGraphicOverlay.getOrcGraphics()) {
            graphic.setSelected(false);
        }
        this.editTextContent.setText("");
        this.noteVisionItemKey = null;
        showOrHideFabAdd();
    }

    /**
     * Perguntar se o título deve ser preenchido com o texto selecionado.
     * @param graphic informar um OcrGraphic válido.
     */
    private void parseFillTitle(final OcrGraphic graphic){
        assert (graphic != null) && graphic.isSelected() && graphic.getTextBlock() != null;
        if ( !NoteVision.checkTitle(this.editTextTitle.getText().toString())){
            Snackbar snackbar = Snackbar.make(fabCameraPlayStop, R.string.note_vision_ask_fill_title, BaseTransientBottomBar.LENGTH_LONG);
            snackbar.setAction(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assert (graphic != null && graphic.getTextBlock() != null);
                    editTextTitle.setText(graphic.getTextBlock().getValue());
                    graphic.setSelected(false);
                }
            }).show();
        }
    }

    /**
     * Validar e salvar um NoteVision
     */
    private void saveNoteVision(Boolean finish) {
        String title = editTextTitle.getText().toString();
        Date dateNow = new Date();
        String content = editTextContent.getText().toString();
        this.editTextTitle.setError(null);
        this.editTextContent.setError(null);

        if (!NoteVisionItem.checkContent(content)) {
            this.editTextContent.setError(getString(R.string.note_vision_validate_content_empty));
            if (this.isCameraPlay()) {
                Toast.makeText(this, getString(R.string.note_vision_validate_content_empty), Toast.LENGTH_SHORT).show();
            } else {
                this.editTextContent.requestFocus();
            }
            return;
        } else if (!NoteVision.checkTitle(title)) {
            this.editTextTitle.setError(getString(R.string.note_vision_validate_title_empty));
            this.editTextTitle.requestFocus();
            return;
        }

        boolean newNoteVision = this.noteVisionKey == null;

        //Salvar o Note Vision / Item e também atualizar a chave do Note Vision,
        //para que as próximas inclusões dos itens sejam no mesmo Note Vision.
        HashMap<String, String> keys = this.cloudVisionProvider.setNoteVision(
                this.noteVisionKey,
                title,
                this.noteVisionItemKey,
                content,
                dateNow);
        this.noteVisionKey = keys.get(NOTE_VISION_KEY);
        this.noteVisionItemKey = keys.get(NOTE_VISION_ITEM_KEY);

        if (newNoteVision) this.appAnalyticsHelper.logNoteVisionAdded(TAG);
        //Solicitar a atualização do Widget.
        NoteVisionSummaryWidget.notifyWidgetUpdate(this);
        //Retornar com a chave do NoteVision e item confirmado e encerrar a inclusão.
        if (finish) {
            Intent intent = new Intent();
            intent.putExtra(NOTE_VISION_KEY, this.noteVisionKey);
            intent.putExtra(NOTE_VISION_ITEM_KEY, this.noteVisionItemKey);
            setResult(Activity.RESULT_OK, intent);
            this.setLockScreenOrientation(false);
            this.finish();
        } else {
            //Limpar a tela para a inclusão de novos itens.
            newNoteVisionContent();
        }
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and set to selected   *
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
                mGraphicOverlay.invalidate();
                cameraPlayPause(false);
                graphic.setSelected(!graphic.isSelected());
                parseFillTitle(graphic);
                //Atualizar o conteúdo do NoteVision
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
           onBackPressed();
            return true;
        } else if (id == R.id.note_vision_keyboard_or_camera) {
            keyboardOnOff(null);
            if (this.contentNoteVisionKeyboard.getVisibility() == View.VISIBLE ) {
                this.appAnalyticsHelper.logNoteVisionKeyboardOn(TAG);
            }
        } else if (id == R.id.note_vision_save){
            saveNoteVision(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.fabFlashOnOff)) {
            cameraFlashOnOff(null);
        } else if (view.equals(this.fabCameraPlayStop)) {
            cameraPlayPause(null);
        } else if (view.equals(this.fabAdd)) {
            saveNoteVision(false);
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

    /**
     * Bloquear a rotação da tela,
     * @param lock
     */
    protected void setLockScreenOrientation(boolean lock) {
        setRequestedOrientation(lock?ActivityInfo.SCREEN_ORIENTATION_LOCKED:ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    /**
     * Mostrar ou esconder FabAdd conforme a situação to Note Vision Item.
     */
    private void showOrHideFabAdd(){
        //Se for uma inclusão e o conteúdo for válido, ativar o botão de inclusão.
        if ( ( this.noteVisionItemKey == null) && NoteVisionItem.checkContent(editTextContent.getText().toString())) {
            this.fabAdd.setVisibility(View.VISIBLE);
        } else {
            this.fabAdd.setVisibility(View.INVISIBLE);
        }
    }
}
