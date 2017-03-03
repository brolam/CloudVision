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
import android.text.format.DateUtils;
import java.util.Date;

/**
 * Facilitar a formatação textos, números, datas e etc.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class FormatHelper {

    /**
     * Formatar uma data de criação
     * @param context informar um contexto válido.
     * @param datetime informar uma data e hora válida.
     * @return texto com a data formatada, ex: Fri, Feb 24, 2017 10:37 AM.
     */
    public static String getDateCreated(Context context, Long datetime){
        String formatCreated = DateUtils.formatDateTime(
                context,
                datetime,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR);
        return formatCreated;

    }

    /**
     * Formatar uma data de atualização
     * @param context informar um contexto válido.
     * @param dateTime informar uma data e hora válida.
     * @return texto com a data formatada, ex: 4 hours ago.
     */
    public static String getDateUpdated(Context context, long dateTime){
        return DateUtils.getRelativeTimeSpanString(
                dateTime,
                new Date().getTime(),
                DateUtils.SECOND_IN_MILLIS).toString();

    }

    /**
     * Remover os caractéres de quebra de linha de um texto.
     * @param text informar um texto válido.
     * @return texto com somente uma linha.
     */
    public static String getTextInOneLine(String text){
        text = text.replace("\n\r", " ");
        return text;
    }

}
