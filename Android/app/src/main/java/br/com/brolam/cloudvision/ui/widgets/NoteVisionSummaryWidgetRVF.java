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
package br.com.brolam.cloudvision.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.CloudVisionProvider;
import br.com.brolam.cloudvision.ui.adapters.holders.NoteVisionSummaryHolder;
import br.com.brolam.cloudvision.ui.helpers.AppAnalyticsHelper;

/**
 * Adapter remoto do Note Vision Summary Widget, onde é realizada o login e consulta dos Notes Vision.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionSummaryWidgetRVF implements RemoteViewsService.RemoteViewsFactory, ValueEventListener, FirebaseAuth.AuthStateListener {
    Context context;
    ArrayList<DataSnapshot> notesVision;
    FirebaseAuth firebaseAuth;
    Query queryNotesVision;

    public NoteVisionSummaryWidgetRVF(Context context ){
        this.context = context;
        this.notesVision = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        //Registrar o evento do login.
        this.firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    public void onDestroy() {
        if ( queryNotesVision != null){
            this.queryNotesVision.removeEventListener(this);
        }

        if ( firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(this);
        }
    }

    @Override
    public int getCount() {
        return this.notesVision.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if ((position == AdapterView.INVALID_POSITION)) {
            return null;
        }
        DataSnapshot dataSnapshot = notesVision.get(position);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.holder_note_vision_summary);
        NoteVisionSummaryHolder.fill(context, remoteViews, dataSnapshot.getKey(), (HashMap)dataSnapshot.getValue() );
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.holder_note_vision_summary);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //Atualizar a lista de Notes Vision por ordem decressente.
        notesVision.clear();
        for (DataSnapshot noteVision : dataSnapshot.getChildren()) {
            notesVision.add(0, noteVision);
        }
        NoteVisionSummaryWidget.notifyWidgetUpdate(context);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //Realizar a consulta dos Notes Vision se o login for realizado com sucesso.
        if ( currentUser != null ) {
            CloudVisionProvider cloudVisionProvider = new CloudVisionProvider(currentUser.getUid());
            this.queryNotesVision = cloudVisionProvider.getQueryNotesVision();
            this.queryNotesVision.removeEventListener(this);
            this.queryNotesVision.addValueEventListener(this);
            AppAnalyticsHelper appAnalyticsHelper = new AppAnalyticsHelper(context);
            appAnalyticsHelper.logWidgets(NoteVisionSummaryWidget.TAG);
        } else {
            this.notesVision.clear();
            NoteVisionSummaryWidget.notifyWidgetUpdate(context);
        }
    }

}
