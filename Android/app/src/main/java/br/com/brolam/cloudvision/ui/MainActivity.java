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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import java.util.HashMap;
import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.CloudVisionProvider;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.ui.adapters.NoteVisionAdapter;
import br.com.brolam.cloudvision.ui.adapters.holders.NoteVisionHolder;
import br.com.brolam.cloudvision.ui.helpers.ActivityHelper;
import br.com.brolam.cloudvision.ui.helpers.ClipboardHelper;
import br.com.brolam.cloudvision.ui.helpers.ImagesHelper;
import br.com.brolam.cloudvision.ui.helpers.LoginHelper;
import br.com.brolam.cloudvision.ui.helpers.ShareHelper;

/**
 * Atividade principal do aplicativo onde será acionado os fluxos abaixo:
 * - Registrar o usuário no aplicativo {@link LoginHelper};
 * - Listar os Notes Vision por ordem de prioridade (data de atualização) {@link NoteVisionAdapter};
 * - Acionar a inclusão de um Note Vision {@link NoteVisionActivity};
 * - Pesquisar Notes Vision {@link NoteVisionSearchable}
 * - Adicionar uma imagem de background para um Note Vision {@link ImagesHelper}
 * - Copiar um Note Vision para a área de transferência {@link ClipboardHelper}
 * - Acionar a exibição do detalhe de um Note Vision {@link NoteVisionDetailsActivity}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MainActivity extends ActivityHelper
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LoginHelper.ILoginHelper, NoteVisionAdapter.INoteVisionAdapter {
    private static final int NOTE_VISION_REQUEST_COD = 1000;
    private static final int NOTE_VISION_DETAILS_REQUEST_COD = 3000;
    private static final String TAG = "MainActivity";

    LoginHelper loginHelper;
    FloatingActionButton fabAdd;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CloudVisionProvider cloudVisionProvider;
    NoteVisionAdapter noteVisionAdapter;
    ImagesHelper imagesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        this.linearLayoutManager = new LinearLayoutManager(this);
        this.linearLayoutManager.setReverseLayout(true);
        this.linearLayoutManager.setStackFromEnd(true);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        /*
         * Criar um LoginHelper para registrar o login do usuário no aplicativo.
         * Veja os métodos onResume, onPause e onActivityResult para mais detalhes
         * sobre o fluxo de registro do usuário.
         */
        this.loginHelper = new LoginHelper(this, navigationView.getHeaderView(0), this);
        navigationView.setNavigationItemSelectedListener(this);
        this.fabAdd.setOnClickListener(this);
    }

    /**
     * Quando o Login do usuário for realizado com sucesso, iniciar os componentes / processos
     * que estão relacionados ao ID do usuário:
     * @param firebaseUser
     */
    @Override
    public void onLogin(FirebaseUser firebaseUser) {
        if ( this.cloudVisionProvider == null) {
            this.cloudVisionProvider = new CloudVisionProvider(firebaseUser.getUid());
            this.noteVisionAdapter = new NoteVisionAdapter(HashMap.class, R.layout.holder_note_vision, NoteVisionHolder.class, this.cloudVisionProvider.getQueryNotesVision());
            this.imagesHelper = new ImagesHelper(this, this.cloudVisionProvider);
            this.noteVisionAdapter.setICloudVisionAdapter(this);
            this.recyclerView.setAdapter(this.noteVisionAdapter);
        }
        //Adicionar o ouvinte para excluir os arquivos registrados no {@link br.com.brolam.cloudvision.data.models.DeletedFiles}
        this.cloudVisionProvider.addListenerDeletedFiles(this.imagesHelper);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Salvar o state do RecyclerView
        super.saveRecyclerViewState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        //Remover o ouvinte para excluir os arquivos registrados no {@link br.com.brolam.cloudvision.data.models.DeletedFiles}
        if ( this.cloudVisionProvider != null ){
            this.cloudVisionProvider.removeListenerDeletedFiles(this.imagesHelper);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.noteVisionAdapter != null){
            this.noteVisionAdapter.cleanup();
        }

        //Remover o ouvinte para excluir os arquivos registrados no {@link br.com.brolam.cloudvision.data.models.DeletedFiles}
        if ( this.cloudVisionProvider != null ){
            this.cloudVisionProvider.removeListenerDeletedFiles(this.imagesHelper);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.note_vision_search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_note_vision) {
            // Handle the camera action
        }if (id == R.id.nav_user_login_off) {
            loginHelper.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.fabAdd)) {
            NoteVisionActivity.newNoteVision(this, NOTE_VISION_REQUEST_COD);
            //NoteVisionActivity.updateNoteVision(this, NOTE_VISON_REQUEST_COD, "-Kda2ezEKZ0C3qydkjat", "-Kda2ezH9bLL5EC_WDyr", "Realtime Database", "https://cloudvision-cdad2. firebaseio.com/\\n\\rl cloudvision-c4ad2: nuli\\n\\r", true );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * Validar se o login do usuário foi realizado com sucess.
         * Sendo importante destacar, se o login for cancelado a MainActivity será encerrada!
         */
        if (loginHelper.checkLogin(requestCode, resultCode)) {
            //Verificar se exitem algum requisão para salvar uma imagem.
            //Observação: Se ocorrer a rotação da tela essa verificação será cancelada,
            //            sendo assim, essa verificar também deve ocorrer novamente na
            //            reconstrução da tela, {@link ImagesHelper.restoreStorageReference }
            if (this.imagesHelper != null) {
                this.imagesHelper.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * Quando um Note Vision for selecionado.
     * @param noteVisionKey informar uma chave válida
     * @param noteVision informar um Note Vision válido
     */
    @Override
    public void onNoteVisionSelect(String noteVisionKey, HashMap noteVision) {
        NoteVisionDetailsActivity.show(this, NOTE_VISION_DETAILS_REQUEST_COD, noteVisionKey, noteVision);
    }

    /**
     * Quando um item do menu de um Note Vision for acionado.
     * @param menuItem informar um item do menu válido.
     * @param noteVisionKey informar uma chave válida.
     * @param noteVision informar um Note Vision válido.
     */
    @Override
    public void onNoteVisionMenuItemClick(MenuItem menuItem, String noteVisionKey, HashMap noteVision) {
        int id = menuItem.getItemId();
        if ( id == R.id.note_vision_add){
            String title = NoteVision.getTitle(noteVision);
            NoteVisionActivity.addNoteVisionContent(this, NOTE_VISION_REQUEST_COD, noteVisionKey, title, false );
        } else if ( id == R.id.note_vision_background){
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
        } else if (id == R.id.note_vision_copy){
            ClipboardHelper clipboardHelper = new ClipboardHelper(this);
            clipboardHelper.noteVision(noteVision);
            Toast.makeText(this, R.string.note_vision_clipboard_copied, Toast.LENGTH_LONG).show();
        } else if (id == R.id.note_vision_share){
            ShareHelper.noteVision(this, noteVision);
        }

    }

    /**
     Sempre retornar Verdadeiro para exibir todos os cartões  {@see NoteVisionAdapter.onBindViewHolder  }
     */
    @Override
    public Boolean searchNoteVision(HashMap noteVision){
        return true;
    }

    /*
     Atualizar a imagem de background para um Note Vision
     */
    @Override
    public void setBackground(String noteVisionKey, HashMap noteVision, ImageView imageView) {
        if ( this.imagesHelper != null){
            this.imagesHelper.loadNoteVisionBackground(noteVisionKey, noteVision, imageView);
        }
    }

    /**
     * Esse método é acionado no final da atualização do Adapter {@link NoteVisionAdapter}
     */
    @Override
    public void restoreViewState() {
        super.restoreRecyclerViewState();
    }

}
