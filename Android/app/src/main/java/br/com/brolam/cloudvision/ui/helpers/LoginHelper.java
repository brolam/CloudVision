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
package br.com.brolam.cloudvision.ui.helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import br.com.brolam.cloudvision.R;

/**
 * LoginHelper - Realizar o registro do usuário no aplicativo e também facilitar a integração
 *               com o Firebase Authentication {link https://firebase.google.com/docs/auth/}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class LoginHelper implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "LoginHelper";
    private static final int RC_SIGN_IN = 1001; //Flag para identificar o retorna da tela de login.
    private Activity activity;
    private FirebaseAuth firebaseAuth;
    private ImageView imageViewUserPhoto;
    private TextView textViewUserName;
    private TextView textViewUserEmail;

    /**
     * Interface opicional para ser executada quando o login for realizado com sucesso.
     */
    public interface ILoginHelper {
        void onLogin( FirebaseUser firebaseUser);
    }

    ILoginHelper iLoginHelper;

    /**
     * Construtor principal e obrigatório para instanciar uma classe.
     * @param activity informar a atividade onde será executada o registro ou verificação do usuário.
     * @param navigationViewHeard informar o View onde será exibido as informações do usuário ou null se não existir.
     */
    public LoginHelper(Activity activity, View navigationViewHeard, ILoginHelper iLoginHelper) {
        this.activity = activity;
        this.iLoginHelper = iLoginHelper;
        this.firebaseAuth = FirebaseAuth.getInstance();
        if ( navigationViewHeard != null) {
            this.imageViewUserPhoto = (ImageView) navigationViewHeard.findViewById(R.id.imageViewUserPhoto);
            this.textViewUserName = (TextView) navigationViewHeard.findViewById(R.id.textViewUserName);
            this.textViewUserEmail = (TextView) navigationViewHeard.findViewById(R.id.textViewUserEmail);
        }
    }

    /**
     * Iniciar o registro ou verificar se o login do usuário é válido.
     */
    public void begin(){
        this.firebaseAuth.addAuthStateListener(this);
    }

    /**
     * Parar a verificacão do login do usuário.
     */
    public void pause(){
        this.firebaseAuth.removeAuthStateListener(this);
    }

    /**
     * Somente cancelar o login do usuário no aplicativo, o usuário não
     * será execluido.
     */
    public void signOut(){
        this.pause();
        this.firebaseAuth.signOut();
        this.activity.finish();
    }

    /**
     * Esse evento é executado após a verificação do login do usuário.
     * @param firebaseAuth Uma instância de {@see FirebaseAuth} se o login for válido ou null quando inválido.
     */
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            this.setViewUser(firebaseUser);
            if ( iLoginHelper != null){
                iLoginHelper.onLogin(firebaseUser);
            }
        } else {
            this.doLogin();
        }
    }

    /**
     * Exibir as informações do usuário.
     * @param firebaseUser informar um {@see FirebaseUser} válido.
     */
    private void setViewUser(FirebaseUser firebaseUser) {
        try {
            if ((firebaseUser.getPhotoUrl() != null) && (this.imageViewUserPhoto != null))
                Glide.with(this.activity).
                        load(firebaseUser.getPhotoUrl()).
                        into(this.imageViewUserPhoto);
            if (this.textViewUserName != null)
                this.textViewUserName.setText(firebaseUser.getDisplayName());
            if (this.textViewUserEmail != null)
                this.textViewUserEmail.setText(firebaseUser.getEmail());
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Acionar o fluxo do login do usuário.
     */
    private void doLogin() {
        this.activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.AppTheme)
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                        ))
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Verificar se o login foi realizado com sucesso ou encerrar o aplicativo se o login for cancelado.
     * @param requestCode para verificar se corresponde ao código da tela de login.
     * @param resultCode para verificar se o login foi realizado com sucesso ou cancelado.
     * @return
     */
    public boolean checkLogin(int requestCode, int resultCode){
        if ( requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                this.activity.finish();
                return false;
            }
        }
        return true;
    }

}
