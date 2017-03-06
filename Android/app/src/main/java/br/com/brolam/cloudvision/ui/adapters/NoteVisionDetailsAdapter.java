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
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import java.util.HashMap;
import br.com.brolam.cloudvision.ui.adapters.holders.NoteVisionDetailsHolder;

/**
 * Gerenciar a exibição de um Note Vision item
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionDetailsAdapter extends FirebaseRecyclerAdapter<HashMap, NoteVisionDetailsHolder> {

    /**
     * Interface para gerencia os eventos de um Note Vision Item.
     */
    public interface INoteVisionDetailsAdapter {
        void onNoteVisionItemButtonClick(String key, HashMap noteVisionItem, View imageButton);
        /**
         * Recuperar a largura do conteúdo de um Note Vision Item na tela para configurar
         * o Swiping List Item.
         * @return tamanho da tela em dips
         */
        int getContentWidth();
    }

    private INoteVisionDetailsAdapter iNoteVisionDetailsAdapter;

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
    public NoteVisionDetailsAdapter(Class<HashMap> modelClass, int modelLayout, Class<NoteVisionDetailsHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    public void onBindViewHolder(NoteVisionDetailsHolder viewHolder, int position) {
        HashMap noteVision = getItem(position);
        populateViewHolder(viewHolder, noteVision, position);
    }

    @Override
    protected void populateViewHolder(final NoteVisionDetailsHolder viewHolder, final HashMap noteVisionItem, int position) {
        final String key = getRef(position).getKey();
        int contentWidth = this.iNoteVisionDetailsAdapter == null ? Toolbar.LayoutParams.WRAP_CONTENT : iNoteVisionDetailsAdapter.getContentWidth();
        viewHolder.bindNoteVisionItem(noteVisionItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemExpandOrCollapse(viewHolder);
            }
        }, contentWidth);

        View.OnClickListener onClickListenerImageButton = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( iNoteVisionDetailsAdapter != null){
                    iNoteVisionDetailsAdapter.onNoteVisionItemButtonClick(key, noteVisionItem, view);
                }

            }
        };
        viewHolder.setImagesButtonOnClickListener(onClickListenerImageButton);

        //Exibir tod0 o conteúdo do item.
        if ( getItemCount() == 1){
            viewHolder.setExpand(true);
        }

    }

    /**
     * Converter snapshot.getValue() em um NoteVisionItem válido.
     *
     * @param snapshot
     * @return
     */
    @Override
    protected HashMap parseSnapshot(DataSnapshot snapshot) {
        return (HashMap<String, Object>) snapshot.getValue();
    }

    public void setINoteVisionDetailsAdapter(INoteVisionDetailsAdapter iNoteVisionDetailsAdapter) {
        this.iNoteVisionDetailsAdapter = iNoteVisionDetailsAdapter;
    }

    /**
     * Inverter a expansão do conteúdo.
     * @param viewHolder
     */
    private void setItemExpandOrCollapse(NoteVisionDetailsHolder viewHolder){
        boolean expand = viewHolder.isExpand();
        if ( expand){
            viewHolder.setExpand(false);
        } else {
            viewHolder.setExpand(true);
        }
    }
}
