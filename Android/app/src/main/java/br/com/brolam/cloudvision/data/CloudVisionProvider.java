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
package br.com.brolam.cloudvision.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import br.com.brolam.cloudvision.data.models.NoteVision;
import br.com.brolam.cloudvision.data.models.NoteVisionItem;

/**
 * Realizar manutenção e consultas no banco de dados do CloudVision.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class CloudVisionProvider {

    String userId;
    static FirebaseDatabase database;
    DatabaseReference referenceNotesVision;
    DatabaseReference referenceNotesVisionItems;

    /**
     * Construtor obrigratório com o ID do usuário.
     *
     * @param userId informar um ID válido.
     */
    public CloudVisionProvider(String userId) {
        this.userId = userId;
        if ( database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            database.setLogLevel(Logger.Level.DEBUG);
        }
        this.referenceNotesVision = database.getReference(NoteVision.PATH_NOTES_VISION).child(userId);
        this.referenceNotesVisionItems = database.getReference(NoteVisionItem.PATH_NOTES_VISION_ITEMS).child(userId);
    }

    /**
     * Incluir ou Atualizar um NoteVision e seu conteúdo( item ) através de um bloco de de atualizações
     * que será totalmente confirmado ou cancelado se ocorrer algum erro.
     * Para mais detalhes {@see https://firebase.google.com/docs/database/android/read-and-write#updating_or_deleting_data}
     *
     * @param noteVisionKey     informar null para inclusão ou uma chave válida para autalização.
     * @param title             informar um valor válido para o título {@see NoteVision.parseTitle}
     * @param noteVisionItemKey informar null para inclusão ou uma chave válida para atualização.
     * @param content           informar um conteúdo válido.
     * @param created           informar a data de inclusão do NoteVision e o conteúdo.
     */
    public void setNoteVision(String noteVisionKey, String title, String noteVisionItemKey, String content, Date created) {
        Map<String, Object> batchUpdates = new HashMap<>();

        if (noteVisionKey == null) {
            noteVisionKey = referenceNotesVision.push().getKey();
            batchUpdates.put(String.format(NoteVision.USER_NOTE_VISION, userId, noteVisionKey), NoteVision.getNewNoteVision(title, content, created.getTime()));
        } else {
            batchUpdates.putAll(NoteVision.getUpdateNoteVision(this.userId, noteVisionKey, title, content, created.getTime()));
        }

        if (noteVisionItemKey == null) {
            noteVisionItemKey = referenceNotesVisionItems.child(noteVisionKey).push().getKey();
            batchUpdates.put(String.format(NoteVisionItem.USER_NOTE_VISION_ITEMS, userId, noteVisionKey, noteVisionItemKey), NoteVisionItem.getNewNoteVisionItem(content, created.getTime()));
        } else {
            batchUpdates.putAll(NoteVisionItem.getUpdateNoteVisionItem(this.userId, noteVisionKey, noteVisionItemKey, content));
        }
        database.getReference().updateChildren(batchUpdates);
    }

    /**
     * Incluir ou Atualizar a origem da imagem de background para um Note Vision.
     * @param noteVisionKey informar uma chave válida.
     * @param backgroundOrigin informar a origem do background.
     */
    public void setNoteVisionBackground(String noteVisionKey, NoteVision.BackgroundOrigin backgroundOrigin){
        Map<String, Object> batchUpdates = new HashMap<>();
        batchUpdates.putAll(NoteVision.getUpdateNoteVisionBackground(this.userId, noteVisionKey , backgroundOrigin));
        database.getReference().updateChildren(batchUpdates);
    }


    public String getUserId() {
        return userId;
    }

    /**
     * Recuperar Notes Vision por ordem ascendente de prioridade "data da atualização" {@see NoteVision}
     * @return {@see Query}
     */
    public Query getQueryNotesVision(){
        return this.referenceNotesVision.orderByPriority();
    }

    /**
     * Recuperar Note Vision itens de um Note Vision por ordem de prioridade "data da criação" {@see NoteVisionItem}
     * @param noteVisionKey informar uma chave válida.
     * @return {@see Query}
     */
    public Query getQueryNoteVisionItems(String noteVisionKey){
        return this.referenceNotesVisionItems.child(noteVisionKey).orderByPriority();
    }
}
