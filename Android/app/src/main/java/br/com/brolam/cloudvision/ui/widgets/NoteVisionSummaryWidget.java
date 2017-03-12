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

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.services.WidgetsRemoteViewsService;
import br.com.brolam.cloudvision.ui.NoteVisionActivity;
import br.com.brolam.cloudvision.ui.NoteVisionDetailsActivity;

/**
 * Exibir o resumo de um Note Vision em um Widget.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionSummaryWidget extends AppWidgetProvider {
    //Para registra e receber mensagens de atualizações
    public static final String NOTE_VISION_SUMMARY_UPDATE = "br.com.brolam.cloudvision.ui.widgets.NOTE_VISION_SUMMARY_UPDATE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_note_vision_summary);
        remoteViews.setRemoteAdapter(R.id.listView, new Intent(context, WidgetsRemoteViewsService.class));

        //Para acionar a tela de detalhes no onClick
        Intent intentTemplate = new Intent(context, NoteVisionDetailsActivity.class);
        PendingIntent pendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(intentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.listView, pendingIntentTemplate);
        //Configuar uma mensagem para quando a lista de Notes Vision estiver vazia.
        remoteViews.setEmptyView(R.id.listView, R.id.textViewWidgetMessage);

        //Para acionar a tela de inclusão no onClick
        Intent intent = new Intent(context, NoteVisionActivity.class);
        PendingIntent pendingIntentNewNoteVision = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.imageButtonAdd, pendingIntentNewNoteVision);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (NOTE_VISION_SUMMARY_UPDATE.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView
            );
        }
    }

    /**
     * Notificar que é necessário atualizar o Widget.
     * @param context
     */
    public static void notifyWidgetUpdate(Context context ){
        Intent dataUpdatedIntent = new Intent(NOTE_VISION_SUMMARY_UPDATE);
        context.sendBroadcast(dataUpdatedIntent);
    }


}
