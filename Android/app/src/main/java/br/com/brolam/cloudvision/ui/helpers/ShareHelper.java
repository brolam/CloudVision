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

import android.content.Context;
import android.content.Intent;
import java.util.HashMap;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.data.models.NoteVisionItem;

/**
 * Facilitar o compartilhamento de informações com outros aplicativos.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class ShareHelper {

    /**
     * Compartilhar um Note Vision com outros aplicativos
     * @param noteVision informar um Note Vision válido.
     */
    public static void noteVision(Context context, HashMap noteVision){
        String title = NoteVision.getTitle(noteVision);
        String summary = NoteVision.getSummary(noteVision);
        String text = String.format("%s %s\n\r%s %s",
                FormatHelper.getTextInOneLine(title),
                FormatHelper.getDateCreated(context, NoteVision.getCreated(noteVision)),
                FormatHelper.getTextInOneLine(summary),
                FormatHelper.getDateCreated(context, NoteVision.getUpdated(noteVision))
        );
        share(context, text);
    }

    /**
     * Compartilhar um Note Vision Item com outros aplicativos.
     * @param noteVision informar um Note Vision válido.
     * @param noteVisionItem informar um Note Vision Item válido.
     */
    public static void noteVisionItem(Context context, HashMap noteVision, HashMap noteVisionItem){
        String title = NoteVision.getTitle(noteVision);
        String content = NoteVisionItem.getContent(noteVisionItem);
        String text = String.format("%s %s\n\r%s %s",
                FormatHelper.getTextInOneLine(title),
                FormatHelper.getDateCreated(context, NoteVision.getCreated(noteVision)),
                FormatHelper.getTextInOneLine(content),
                FormatHelper.getDateCreated(context, NoteVisionItem.getCreated(noteVision))
        );
        share(context, text);

    }

    private static void share(Context context, String text){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
