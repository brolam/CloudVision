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
package br.com.brolam.cloudvision.ui.adapters;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import java.util.HashMap;
import br.com.brolam.cloudvision.ui.adapters.holders.NoteVisionHolder;

/**
 * Gerenciar a exibição dos Notes Vision cards
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionAdapter extends FirebaseRecyclerAdapter<HashMap, NoteVisionHolder> {

    /**
     * Interface para gerencia os eventos de um Note Vision Card.
     */
    public interface INoteVisionAdapter {
        void onNoteVisionSelect(String key, HashMap noteVision);
        void onNoteVisionMenuItemClick(MenuItem menuItem, String key, HashMap noteVision);
    }

    private INoteVisionAdapter iNoteVisionAdapter;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public NoteVisionAdapter(Class<HashMap> modelClass, int modelLayout, Class<NoteVisionHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }


    @Override
    public void onBindViewHolder(NoteVisionHolder viewHolder, int position) {
        HashMap noteVision = getItem(position);
        populateViewHolder(viewHolder, noteVision, position);
    }

    @Override
    protected void populateViewHolder(NoteVisionHolder viewHolder, final HashMap model, int position) {
        final String key = getRef(position).getKey();
        viewHolder.bindNoteVision(model, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iNoteVisionAdapter != null) {
                    iNoteVisionAdapter.onNoteVisionSelect(key, model);
                }
            }
        }, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (iNoteVisionAdapter != null) {
                    iNoteVisionAdapter.onNoteVisionMenuItemClick(item, key, model);
                }
                return true;
            }
        });

    }

    /**
     * Converter snapshot.getValue() em um NoteVision válido.
     * @param snapshot
     * @return
     */
    @Override
    protected HashMap parseSnapshot(DataSnapshot snapshot) {
        return (HashMap<String, Object>) snapshot.getValue();
    }

    public void setICloudVisionAdapter(INoteVisionAdapter iNoteVisionAdapter) {
        this.iNoteVisionAdapter = iNoteVisionAdapter;
    }
}
