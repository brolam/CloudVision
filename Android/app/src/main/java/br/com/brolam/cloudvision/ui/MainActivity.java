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
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.ui.helpers.LoginHelper;

/**
 * Atividade principal do aplicativo onde será acionado os fluxos abaixo:
 * - Registrar o usuário no aplicativo {@see LoginHelper}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static int NOTE_VISON_REQUEST_COD = 1000;

    LoginHelper loginHelper;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
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
        this.loginHelper = new LoginHelper(this, navigationView.getHeaderView(0), null);
        navigationView.setNavigationItemSelectedListener(this);
        this.fabAdd.setOnClickListener(this);
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
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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
            NoteVisionActivity.newNoteVision(this, NOTE_VISON_REQUEST_COD);
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
        if ( loginHelper.checkLogin(requestCode, resultCode)){

        }
    }
}
