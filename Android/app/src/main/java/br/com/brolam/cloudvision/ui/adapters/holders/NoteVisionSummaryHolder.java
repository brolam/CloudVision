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
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.HashMap;

import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.data.models.NoteVisionItem;
import br.com.brolam.cloudvision.ui.NoteVisionDetailsActivity;
import br.com.brolam.cloudvision.ui.helpers.FormatHelper;

/**
 * Atualizar um ListView Item com informações de um Note Vision Summary.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionSummaryHolder {

    /**
     * Preencher um RemoteViews com os campos de um Note Vision Summary em um Widget.
     * @param context informar um contexo válido.
     * @param remoteViews informar um RemoteViews válido.
     * @param noteVisionKey informar uma chave válida
     * @param noteVision informar um NoteVision válido.
     */
    public static void fill(Context context, RemoteViews remoteViews, String noteVisionKey, HashMap noteVision){
        remoteViews.setTextViewText(R.id.textViewTitle, NoteVision.getTitle(noteVision) );
        remoteViews.setTextViewText(R.id.textViewSubtitle, FormatHelper.getDateCreated(context, NoteVision.getUpdated(noteVision)) );
        remoteViews.setTextViewText(R.id.textViewSummary, FormatHelper.getTextInOneLine(NoteVision.getSummary(noteVision)) );
        //Acionar a tela de detalhes no onClick
        Intent intent = NoteVisionDetailsActivity.getIntent(context, noteVisionKey, noteVision);
        remoteViews.setOnClickFillInIntent(R.id.linearLayoutNoteVisionSummary, intent);

    }

}

