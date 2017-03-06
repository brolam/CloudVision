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
package br.com.brolam.cloudvision.ui.adapters.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.models.NoteVisionItem;
import br.com.brolam.cloudvision.ui.helpers.FormatHelper;

/**
 * Atualizar um ListView Item com informações de um Note Vision Item.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionDetailsHolder extends RecyclerView.ViewHolder {

    LinearLayout linearLayoutContent;
    TextView textViewCreated;
    TextView textViewContent;


    public NoteVisionDetailsHolder(View itemView) {
        super(itemView);
        this.linearLayoutContent = (LinearLayout) this.itemView.findViewById(R.id.linearLayoutContent);
        this.textViewCreated = (TextView) this.itemView.findViewById(R.id.textViewCreated);
        this.textViewContent = (TextView) this.itemView.findViewById(R.id.textViewContent);
    }

    /**
     * Vincular um Note Vision Item a um ListView Item.
     * @param noteVisionItem              informar um Note Vision Item Válido.
     * @param onClickListener         informar um evento quando ouver um click no Item.
     */
    public void bindNoteVisionItem(HashMap noteVisionItem, View.OnClickListener onClickListener, int linearLayoutContentWidth) {
        this.itemView.setOnClickListener(onClickListener);
        Context context = this.textViewCreated.getContext();
        this.textViewCreated.setText(FormatHelper.getDateCreated(context, NoteVisionItem.getCreated(noteVisionItem)));
        this.textViewContent.setText(FormatHelper.getTextInOneLine(NoteVisionItem.getContent(noteVisionItem)));
        this.linearLayoutContent.getLayoutParams().width = linearLayoutContentWidth;
    }

    public void setImagesButtonOnClickListener(View.OnClickListener onClickListener){
        this.itemView.findViewById(R.id.imageButtonEdit).setOnClickListener(onClickListener);
        this.itemView.findViewById(R.id.imageButtonCopy).setOnClickListener(onClickListener);
        this.itemView.findViewById(R.id.imageButtonShared).setOnClickListener(onClickListener);
        this.itemView.findViewById(R.id.imageButtonDelete).setOnClickListener(onClickListener);
    }


}

