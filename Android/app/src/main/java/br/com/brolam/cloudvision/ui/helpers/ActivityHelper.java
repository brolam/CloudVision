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

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import br.com.brolam.cloudvision.R;

/**
 * Disponibilizar funcionalidades que podem ser compartilhadas entre as atividades
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class ActivityHelper extends AppCompatActivity {
    // Intent request code to handle updating play services if needed.
    public static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    public static final int RC_HANDLE_CAMERA_PERM = 2;


    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";
    private static final String COORDINATOR_LAYOUT_HELPER_VIEW_STATE = "CoordinatorLayoutHelperViewState";
    private static final String NESTED_SCROLL_HELPER_VIEW_STATE = "nested_scroll_helper_view_state";
    private Parcelable recyclerViewState = null;
    private Parcelable coordinatorLayoutHelperViewState = null;
    private Parcelable nestedScrollHelperViewState = null;

    //Registar a chave do item na lista principal da atividade que deve ser
    //selecionado quando a atividade for recriada.
    private String itemSelectedKey;

    public String getItemSelectedKey() {
        return itemSelectedKey;
    }

    public void setItemSelectedKey(String itemSelectedKey) {
        this.itemSelectedKey = itemSelectedKey;
    }

    public void clearItemSelectedKey(){
        setItemSelectedKey(null);
    }

    /**
     * Salvar o state view do RecyclerView principal da atividade.
     * @param outState informar um Bundle válido.
     */
    protected void saveRecyclerViewState(Bundle outState){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            outState.putParcelable(RECYCLER_VIEW_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    /**
     * Salvar o state view do CoordinatorLayout principal da atividade.
     * @param outState informar um Bundle válido.
     */
    protected void saveCoordinatorLayoutHelper(Bundle outState){
        CoordinatorLayoutHelper coordinatorLayoutHelper = (CoordinatorLayoutHelper) findViewById(R.id.coordinatorLayout);
        if ( coordinatorLayoutHelper != null){
            outState.putParcelable(COORDINATOR_LAYOUT_HELPER_VIEW_STATE, coordinatorLayoutHelper.getRestoreInstanceState());
        }
    }

    /**
     * Salvar o state view do NestedScroll principal da atividade.
     * @param outState informar um Bundle válido.
     */
    protected void saveNestedScrollHelperViewState(Bundle outState){
        NestedScrollViewHelper nestedScrollViewHelper  = (NestedScrollViewHelper)findViewById(R.id.nestedScrollViewHelper);
        if (nestedScrollViewHelper != null) {
            outState.putParcelable(NESTED_SCROLL_HELPER_VIEW_STATE, nestedScrollViewHelper.getRestoreInstanceState());
        }
    }

    /**
     * Recupear os states view somente dos state view salvos.
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        assert savedInstanceState != null;
        if (savedInstanceState.containsKey(COORDINATOR_LAYOUT_HELPER_VIEW_STATE)) {
            this.coordinatorLayoutHelperViewState = savedInstanceState.getParcelable(COORDINATOR_LAYOUT_HELPER_VIEW_STATE);
        }

        if (savedInstanceState.containsKey(RECYCLER_VIEW_STATE)) {
            this.recyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        }

        if (savedInstanceState.containsKey(NESTED_SCROLL_HELPER_VIEW_STATE)) {
            this.nestedScrollHelperViewState = savedInstanceState.getParcelable(NESTED_SCROLL_HELPER_VIEW_STATE);
        }
    }


    /**
     * Restaurar o CoordinatorLayout se o state foi salvo.
     */
    public void restoreCoordinatorLayoutHelperViewState(){
        assert this.coordinatorLayoutHelperViewState != null;
        CoordinatorLayoutHelper coordinatorLayoutHelper = (CoordinatorLayoutHelper) findViewById(R.id.coordinatorLayout);
        if (coordinatorLayoutHelper != null) {
            coordinatorLayoutHelper.restoreInstanceState(this.coordinatorLayoutHelperViewState);
            this.coordinatorLayoutHelperViewState = null;
        }
    }

    /**
     * Restaurar o RecyclerView se o state foi salvo.
     */
    public void restoreRecyclerViewState(){
        assert this.recyclerViewState != null;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if ( recyclerView != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(this.recyclerViewState);
            this.recyclerViewState = null;
        }

    }

    /**
     * Restaurar o NestedScroll se o state foi salvo.
     */
    public void restoreNestedScrollHelperViewState(){
        assert this.nestedScrollHelperViewState != null;
        NestedScrollViewHelper nestedScrollViewHelper  = (NestedScrollViewHelper)findViewById(R.id.nestedScrollViewHelper);
        if ( nestedScrollViewHelper != null ) {
            nestedScrollViewHelper.restoreInstanceState(this.coordinatorLayoutHelperViewState);
            this.coordinatorLayoutHelperViewState = null;
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    public static void requestCameraPermission(String tag, final Activity activity, View view) {
        Log.w(tag, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(view, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }
}
