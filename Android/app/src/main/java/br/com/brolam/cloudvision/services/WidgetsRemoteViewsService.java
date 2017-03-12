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
package br.com.brolam.cloudvision.services;


import android.content.Intent;
import android.widget.RemoteViewsService;

import br.com.brolam.cloudvision.ui.widgets.NoteVisionSummaryWidgetRVF;

/**
 * Gerenciar os RemoteViews dos widgets
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class WidgetsRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
         return new NoteVisionSummaryWidgetRVF(this);
    }
}