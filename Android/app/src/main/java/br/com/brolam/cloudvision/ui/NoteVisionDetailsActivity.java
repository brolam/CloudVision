/*
 * Copyright (C) 2017 Breno Marques
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.CloudVisionProvider;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.data.models.NoteVisionItem;
import br.com.brolam.cloudvision.ui.adapters.NoteVisionDetailsAdapter;
import br.com.brolam.cloudvision.ui.adapters.holders.NoteVisionDetailsHolder;
import br.com.brolam.cloudvision.ui.helpers.ClipboardHelper;
import br.com.brolam.cloudvision.ui.helpers.FormatHelper;
import br.com.brolam.cloudvision.ui.helpers.ImagesHelper;
import br.com.brolam.cloudvision.ui.helpers.LoginHelper;
import br.com.brolam.cloudvision.ui.helpers.ShareHelper;

/**
 * Manutenção e exbição de um Note Vision Item.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionDetailsActivity extends AppCompatActivity implements LoginHelper.ILoginHelper, NoteVisionDetailsAdapter.INoteVisionDetailsAdapter, View.OnClickListener, ValueEventListener {
    private static final String TAG = "DetailsActivity";
    public static final String NOTE_VISION_KEY = "noteVisionKey";
    public static final String NOTE_VISION = "noteVision";
    private static final int NOTE_VISION_REQUEST_COD = 1000;

    private String noteVisionKey;
    private HashMap noteVision;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    LoginHelper loginHelper;
    CloudVisionProvider cloudVisionProvider;
    ImagesHelper imagesHelper;
    NoteVisionDetailsAdapter noteVisionDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_vision_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setSupportActionBar(toolbar);

        this.linearLayoutManager = new LinearLayoutManager(this);
        this.linearLayoutManager.setReverseLayout(true);
        this.linearLayoutManager.setStackFromEnd(true);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        setSaveInstanceState(savedInstanceState);
         /*
         * Criar um LoginHelper para registrar o login do usuário no aplicativo.
         * Veja os métodos onResume, onPause e onActivityResult para mais detalhes
         * sobre o fluxo de registro do usuário.
         */
        this.loginHelper = new LoginHelper(this, null, this);
    }

    /**
     * Acionar a exibição dos detalhes de um Note Vision.
     *
     * @param activity      informar a atividade que receberá o retorno.
     * @param requestCod    informar o código de requisição dessa atividade.
     * @param noteVisionKey informar uma chave válida.
     * @param noteVision    informar um NoteVision válido.
     */
    public static void show(Activity activity, int requestCod, String noteVisionKey, HashMap noteVision) {
        Intent intent = new Intent(activity, NoteVisionDetailsActivity.class);
        intent.putExtra(NOTE_VISION_KEY, noteVisionKey);
        intent.putExtra(NOTE_VISION, noteVision);
        activity.startActivityForResult(intent, requestCod);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NOTE_VISION_KEY, this.noteVisionKey);
        outState.putSerializable(NOTE_VISION, this.noteVision);
    }

    private void setSaveInstanceState(Bundle savedInstanceState) {
        // read parameters from the  savedInstanceState ou intent used to launch the activity.
        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        if (bundle != null) {
            this.noteVisionKey = bundle.getString(NOTE_VISION_KEY);
            this.noteVision = (HashMap) bundle.getSerializable(NOTE_VISION);
            this.setHeader();
        }
    }

    /**
     * Atualizar o cabeçalho com informações do Note Vision.
     */
    private void setHeader() {
        this.setTitle(NoteVision.getTitle(this.noteVision));
        TextView textViewCreated = (TextView) this.findViewById(R.id.textViewCreated);
        ImageView imageViewBackground = (ImageView) this.findViewById(R.id.imageViewBackground);
        if (textViewCreated != null) {
            textViewCreated.setText(FormatHelper.getDateCreated(this, NoteVision.getCreated(this.noteVision)));
        }
        if ((this.imagesHelper != null) && (imageViewBackground != null)) {
            this.imagesHelper.loadNoteVisionBackground(this.noteVisionKey, this.noteVision, imageViewBackground);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loginHelper.begin();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.loginHelper.pause();
        if ( this.cloudVisionProvider != null){
            this.cloudVisionProvider.removeListenerOneNoteVision(this.noteVisionKey, this);
        }
    }

    @Override
    public void onLogin(FirebaseUser firebaseUser) {
        this.cloudVisionProvider = new CloudVisionProvider(firebaseUser.getUid());
        this.imagesHelper = new ImagesHelper(this, this.cloudVisionProvider);
        this.noteVisionDetailsAdapter = new NoteVisionDetailsAdapter(
                HashMap.class,
                R.layout.holder_note_vision_details,
                NoteVisionDetailsHolder.class,
                this.cloudVisionProvider.getQueryNoteVisionItems(this.noteVisionKey)
        );
        this.noteVisionDetailsAdapter.setINoteVisionDetailsAdapter(this);
        this.recyclerView.setAdapter(noteVisionDetailsAdapter);
        if ( this.cloudVisionProvider != null){
            this.cloudVisionProvider.addListenerOneNoteVision(this.noteVisionKey, this);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        this.noteVision = (HashMap)dataSnapshot.getValue();
        setHeader();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_vision_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_vision_share:
                ShareHelper.noteVision(this, noteVision);
                return true;
            case R.id.note_vision_background:
                if (this.imagesHelper  != null){
                    try {
                        NoteVision.BackgroundOrigin backgroundOrigin = NoteVision.getBackground(noteVision);
                        if ( backgroundOrigin == NoteVision.BackgroundOrigin.LOCAL) {
                            Toast.makeText(this, R.string.note_vision_alert_background_image_in_processing, Toast.LENGTH_LONG).show();
                        } else {
                            this.imagesHelper.takeNoteVisionBackground(noteVisionKey);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(this, String.format(getString(R.string.main_activity_request_error),ImagesHelper.REQUEST_IMAGE_CAPTURE), Toast.LENGTH_LONG).show();
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Validar se o login do usuário foi realizado com sucess.
         * Sendo importante destacar, se o login for cancelado a atividade será encerrada!
         */
        if (loginHelper.checkLogin(requestCode, resultCode)) {
            //Confirmar a alteração da imagem de background.
            if (this.imagesHelper != null) {
                try {
                    this.imagesHelper.onActivityResult(requestCode, resultCode, data);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(this, String.format(getString(R.string.main_activity_request_error),requestCode), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //Adicionar um Note Vision Item.
            case R.id.fab:
                String title = NoteVision.getTitle(this.noteVision);
                NoteVisionActivity.addNoteVisionContent(this, NOTE_VISION_REQUEST_COD, this.noteVisionKey, title, false);
                return;
        }
    }

    @Override
    public void onNoteVisionItemButtonClick(String noteVisionItemKey, HashMap noteVisionItem, View imageButton) {
        switch (imageButton.getId()) {
            //Editar o Note Vision Seleciondo.
            case R.id.imageButtonEdit:
                String title = NoteVision.getTitle(this.noteVision);
                String content = NoteVisionItem.getContent(noteVisionItem);
                NoteVisionActivity.updateNoteVision(this, NOTE_VISION_REQUEST_COD, this.noteVisionKey, noteVisionItemKey, title, content, true);
                return;
            //Copiar para a área de transferência o Note Vision Item selecionado.
            case R.id.imageButtonCopy:
                ClipboardHelper clipboardHelper = new ClipboardHelper(this);
                clipboardHelper.noteVisionItem(noteVision, noteVisionItem);
                Toast.makeText(this, R.string.note_vision_clipboard_copied, Toast.LENGTH_LONG).show();
                return;
            //Compartilhar o Note Vision Item selelcionado com outros aplicativos.
            case R.id.imageButtonShared:
                ShareHelper.noteVisionItem(this, noteVision, noteVisionItem);
                return;
            case R.id.imageButtonDelete:
                deleteNoteVisionItem(noteVisionItemKey);
                return;
        }
    }

    @Override
    public int getContentWidth() {
        return this.recyclerView.getWidth();
    }

    /**
     * Solicitar a confirmação de exclusão de um Note Vision Item.
     * @param noteVisionItemKey informar uma chave válida.
     */
    private void deleteNoteVisionItem(final String noteVisionItemKey) {
        Snackbar snackbar = Snackbar.make
                (
                        this.recyclerView,
                        String.format(getString(R.string.note_vision_confirm_delete), getString(R.string.ok)),
                        BaseTransientBottomBar.LENGTH_LONG
                );
        snackbar.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloudVisionProvider != null) {
                    cloudVisionProvider.deleteNoteVisionItem(noteVisionKey, noteVisionItemKey);
                }
            }
        });
        snackbar.show();
    }

}
