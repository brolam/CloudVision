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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import java.util.HashMap;
import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.data.models.NoteVisionItem;

/**
 * Facilitar a transferência de informações para a área de transferência, clipboard.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class ClipboardHelper {
    ClipboardManager clipboard;
    Context context;

    public ClipboardHelper(Context context){
        this.clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        this.context = context;
    }

    /**
     * Copiar um Note Vision para área de transferência
     * @param noteVision informar um Note Vision válido.
     */
    public void noteVision(HashMap noteVision){
        String label = String.format("%s  %s", context.getString(R.string.app_name), FormatHelper.getTextInOneLine(NoteVision.getTitle(noteVision)));
        String title = NoteVision.getTitle(noteVision);
        String summary = NoteVision.getSummary(noteVision);
        String text = String.format("%s %s\n\r%s %s",
                FormatHelper.getTextInOneLine(title),
                FormatHelper.getDateCreated(context, NoteVision.getCreated(noteVision)),
                FormatHelper.getTextInOneLine(summary),
                FormatHelper.getDateCreated(context, NoteVision.getUpdated(noteVision))
        );
        ClipData clip = ClipData.newPlainText(label, text);
        this.clipboard.setPrimaryClip(clip);
    }


    /**
     * Copiar um Note Vision Item para área de transferência
     * @param noteVision informar um Note Vision válido.
     * @param noteVisionItem informar um Note Vision Item válido.
     */
    public void noteVisionItem(HashMap noteVision, HashMap noteVisionItem){
        String label = String.format("%s  %s", context.getString(R.string.app_name), FormatHelper.getTextInOneLine(NoteVision.getTitle(noteVision)));
        String title = NoteVision.getTitle(noteVision);
        String content = NoteVisionItem.getContent(noteVisionItem);
        String text = String.format("%s %s\n\r%s %s",
                FormatHelper.getTextInOneLine(title),
                FormatHelper.getDateCreated(context, NoteVision.getCreated(noteVision)),
                FormatHelper.getTextInOneLine(content),
                FormatHelper.getDateCreated(context, NoteVisionItem.getCreated(noteVision))
        );
        ClipData clip = ClipData.newPlainText(label, text);
        this.clipboard.setPrimaryClip(clip);
    }
}
