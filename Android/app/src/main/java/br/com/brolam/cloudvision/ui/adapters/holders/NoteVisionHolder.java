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

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.ui.helpers.FormatHelper;

/**
 * Atualizar um cartão com informações de um Note Vision.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionHolder extends RecyclerView.ViewHolder {

    ImageView imageViewBackground;
    TextView textViewTitle;
    TextView textViewCreated;
    TextView textViewUpdated;
    TextView textViewSummary;
    Toolbar toolbar;

    public NoteVisionHolder(View itemView) {
        super(itemView);
        this.imageViewBackground = (ImageView) this.itemView.findViewById(R.id.imageViewBackground);
        this.textViewTitle = (TextView) this.itemView.findViewById(R.id.textViewTitle);
        this.textViewCreated = (TextView) this.itemView.findViewById(R.id.textViewCreated);
        this.textViewUpdated = (TextView) this.itemView.findViewById(R.id.textViewUpdated);
        this.textViewSummary = (TextView) this.itemView.findViewById(R.id.textViewSummary);
        this.toolbar = (Toolbar) this.itemView.findViewById(R.id.toolbar);
        this.toolbar.inflateMenu(R.menu.holder_note_vision);
    }

    public ImageView getImageViewBackground() {
        return imageViewBackground;
    }

    /**
     * Vincular um Note Vision ao cartão.
     * @param noteVision              informar um Note Vision Válido.
     * @param onClickListener         informar um evento quando ouver um click no cartão.
     * @param onMenuItemClickListener informar um evento quando um item do menu de cartão for selecionado.
     */
    public void bindNoteVision(HashMap noteVision, View.OnClickListener onClickListener, Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        this.itemView.setOnClickListener(onClickListener);
        this.textViewTitle.setText(NoteVision.getTitle(noteVision));
        this.textViewCreated.setText(FormatHelper.getDateCreated(this.textViewCreated.getContext(), NoteVision.getCreated(noteVision)));
        this.textViewUpdated.setText(FormatHelper.getDateUpdated(this.textViewUpdated.getContext(), NoteVision.getUpdated(noteVision)));
        this.textViewSummary.setText(FormatHelper.getTextInOneLine(NoteVision.getSummary(noteVision)));
        NoteVision.BackgroundOrigin backgroundOrigin = NoteVision.getBackground(noteVision);
        //Atualizar o ícone do item de menu note_vision_background conforme a origem do background
        MenuItem itemNoteVisionBackground = this.toolbar.getMenu().findItem(R.id.note_vision_background);
        itemNoteVisionBackground.setIcon(
                backgroundOrigin == NoteVision.BackgroundOrigin.REMOTE ? R.drawable.ic_on_camera_white : R.drawable.ic_upload_white
        );
        this.toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    /**
     * Ocultar a exibição de um Note Vision
     */
    public void setHide(){
        RecyclerView.MarginLayoutParams marginLayoutParams = (RecyclerView.MarginLayoutParams) this.itemView.getLayoutParams();
        this.itemView.setVisibility(View.GONE);
        /*Ocultar o spaço reservado para o Note Vision */
        this.itemView.getLayoutParams().height = 0;
        marginLayoutParams.setMargins(0, 0, 0, 0);
    }

}

