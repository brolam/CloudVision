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
package br.com.brolam.cloudvision.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import java.util.HashMap;
import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.models.NoteVision;


/**
 * Estender a atividade {@link MainActivity} para incluir a funcinalidade de pesquisa.
 */
public class NoteVisionSearchable extends MainActivity {
    String search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.search = null;
        handleIntent(getIntent());
    }

    private void setSearchTitle(String search){
        this.setTitle(this.getString(R.string.note_vision_searchable_title_format, search));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.search = intent.getExtras().getString(SearchManager.QUERY, null);
            this.setSearchTitle(this.search);
        }
    }

    /**
     * Realizar a pesquisa no título do Note Vision {@link br.com.brolam.cloudvision.ui.adapters.NoteVisionAdapter}
     * @param noteVision informar um Note Vision válido.
     * @return Verdadeiro se o título do Note Vision conter o texto pesquisado.
     */
    @Override
    public Boolean searchNoteVision(HashMap noteVision){
        String title = NoteVision.getTitle(noteVision).toUpperCase();
        if ( this.search != null){
            return title.contains(search.toUpperCase());
        }
        return true;
    }

}
