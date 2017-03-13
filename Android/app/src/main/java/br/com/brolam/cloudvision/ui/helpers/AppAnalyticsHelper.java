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
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Registrar os eventos para gerar estatísticas de utilização do aplicativo.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class AppAnalyticsHelper {
    public static final String ACTIVITY_NAME = "activity_name";

    private FirebaseAnalytics firebaseAnalytics;

    public AppAnalyticsHelper(Context context){
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    /**
     * Registrar a quantidade de Note Vision adicionados e confirmados.
     * Objetivo: Medir a quantidade de notes vision adicionados em um determinado período.
     * @param activityName informar o nome da atividade.
     */
    public void logNoteVisionAdded(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, activityName);
        firebaseAnalytics.logEvent("note_vision_added", bundle);
    }

    /**
     * Registrar a quantidade de vezes que a funcionalidade adicionar note vision foi acionada.
     * Objetivo: Medir a eficiência da solicitação e confirmação na inclusão dos notes vision.
     * @param activityName informar o nome da atividade.
     */
    public void logNoteVisionAdd(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, activityName);
        firebaseAnalytics.logEvent("note_vision_add", bundle);
    }

    /**
     * Registrar a quantidade de imagens background para os note Vision adicionados
     * Objetivo: Medir a quantidade de images de background vs. a quantidade de notes visions.
     * @param activityName informar o nome da atividade.
     */
    public void logNoteVisionAddBackground(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, activityName);
        firebaseAnalytics.logEvent("note_vision_add_background", bundle);
    }

    /**
     * Registrar a quantidade de vezes que o teclado foi acionada para editar as informacõea
     * recuperadas via a câmera fotográfica.
     * Objetivo: Medir a eficiência na digitalização dos textos via a câmera fotográfica.
     * @param activityName informar o nome da atividade.
     */
    public void logNoteVisionKeyboardOn(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, activityName);
        firebaseAnalytics.logEvent("note_vision_keyboard_on", bundle);
    }

    /**
     * Registrar a quantidade de notes Vision excluidos.
     * Objetivo: Medir a quantidade de notes vision excluidos vs. a quantidade de notes visions.
     * @param activityName informar o nome da atividade.
     */
    public void logNoteVisionDeleted(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, activityName);
        firebaseAnalytics.logEvent("note_vision_deleted", bundle);
    }

    /**
     * Registrar a quantidade de vezes que a funcinalidade CopyToClipboard foi utilizada.
     * Objetivo: Medir a importancia dessa funcionalidade para o usuário.
     * @param activityName informar o nome da atividade.
     */
    public void logNoteVisionCopyToClipboard(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, activityName);
        firebaseAnalytics.logEvent("note_vision_copy_to_clipboard", bundle);
    }

    /**
     * Registrar a quantidade de vezes que a funcinalidade Shared foi utilizada.
     * Objetivo: Medir a importancia dessa funcionalidade para o usuário.
     * @param activityName informar o nome da atividade.
     */
    public void logNoteVisionShared(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, activityName);
        firebaseAnalytics.logEvent("note_vision_shared", bundle);
    }

    /**
     * Registrar a utilização dos widgets
     * Objetivo: Medir a quantidade de usuários utilizando os Widgets.
     * @param widgetName informar o nome do widget.
     */
    public void logWidgets(String widgetName){
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_NAME, widgetName);
        firebaseAnalytics.logEvent("widgets", bundle);
    }
}
