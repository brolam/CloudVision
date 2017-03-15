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
import com.google.firebase.database.ValueEventListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import br.com.brolam.cloudvision.data.models.DeletedFiles;
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
    DatabaseReference referenceDeletedFiles;

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
        this.referenceDeletedFiles = database.getReference(DeletedFiles.PATH_DELETED_FILES).child(userId);
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
     * @return retornar com as chaves do NoteVision e NoteVisionItem.
     */
    public HashMap<String, String> setNoteVision(String noteVisionKey, String title, String noteVisionItemKey, String content, Date created) {
        Map<String, Object> batchUpdates = new HashMap<>();
        HashMap<String, String> keys = new HashMap<>();

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
            //Atualizar o summary do Note Vision com a alteração do conteúdo do Note Vision Item.
            batchUpdates.putAll(NoteVision.getUpdateNoteVisionSummary(userId, noteVisionKey, content, new Date().getTime()));
            batchUpdates.putAll(NoteVisionItem.getUpdateNoteVisionItem(this.userId, noteVisionKey, noteVisionItemKey, content));
        }
        database.getReference().updateChildren(batchUpdates);
        keys.put("noteVisionKey",noteVisionKey);
        keys.put("noteVisionItemKey",noteVisionItemKey);
        return keys;
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
     * Adicionar um ouvinte para um Note Vision.
     * @param noteVisionKey informar uma chave válida.
     * @param valueEventListener informar um ouvinte válido.
     */
    public void addListenerOneNoteVision(String noteVisionKey, ValueEventListener valueEventListener){
        referenceNotesVision.child(noteVisionKey).addValueEventListener(valueEventListener);
    }

    /**
     * Remover um ouvinte de um Note Vision.
     * @param noteVisionKey informar uma chave válida.
     * @param valueEventListener informar um ouvinte válido.
     */
    public void removeListenerOneNoteVision(String noteVisionKey, ValueEventListener valueEventListener){
        referenceNotesVision.child(noteVisionKey).removeEventListener(valueEventListener);
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

    /**
     * Excluir um Note Vision Item e atualizar o summary do Note Vision.
     * @param noteVisionKey informar um chave válida
     * @param noteVisionItemKey informar um chave válida
     */
    public void deleteNoteVisionItem(String noteVisionKey, String noteVisionItemKey){
        Map<String, Object> batchUpdates = new HashMap<>();
        //Atualizar o summary do Note Vision
        batchUpdates.putAll(NoteVision.getUpdateNoteVisionSummary(userId, noteVisionKey, "", new Date().getTime()));
        //Excluir o Note Vision Item.
        batchUpdates.putAll(NoteVisionItem.getDelete(userId, noteVisionKey, noteVisionItemKey));
        this.database.getReference().updateChildren(batchUpdates);
    }

    /**
     * Excluir um Note Vision e seus itens e também registrar a exclusão dos arquivos, imagens e etc.,
     * relacionados ao Note Vision. Sendo importante destacar, que esse processo será executado em uma única
     * tansação.
     * @param noteVisionKey
     */
    public void deleteNoteVision(String noteVisionKey){
        Map<String, Object> batchUpdates = new HashMap<>();
        //Criar uma chave para o arquivo deletado.
        String deletedFileKey = referenceDeletedFiles.push().getKey();
        String noteVisionBackgroundPath =  NoteVision.getBackgroundPath(getUserId(),noteVisionKey);
        HashMap deletedFile = DeletedFiles.getNewDeletedFiles(noteVisionBackgroundPath, new Date().getTime());
        //registrar o arquivo deletado e excluir o Note Vision e seus itens em uma única transação.
        batchUpdates.put(String.format(DeletedFiles.USER_DELETED_FILES, userId, deletedFileKey), deletedFile );
        batchUpdates.putAll(NoteVision.getDelete(getUserId(), noteVisionKey));
        batchUpdates.putAll(NoteVisionItem.getDeleteAll(getUserId(), noteVisionKey));
        this.database.getReference().updateChildren(batchUpdates);
    }

    /**
     * Excluir o registro de uma arquivo deletado.
     * @param deletedFileKey informar uma chave válida.
     */
    public void deleteDeletedFile(String deletedFileKey){
        Map<String, Object> batchUpdates = new HashMap<>();
        batchUpdates.putAll(DeletedFiles.getDelete(getUserId(), deletedFileKey));
        this.database.getReference().updateChildren(batchUpdates);
    }

    /**
     * Adicionar um ouvinte para um registro de arquivo deletado.
     * @param valueEventListener informar um ouvinte válido.
     */
    public void addListenerDeletedFiles(ValueEventListener valueEventListener){
        referenceDeletedFiles.addValueEventListener(valueEventListener);
    }

    /**
     * Remover um ouvinte para um registro de arquivo deletado
     * @param valueEventListener informar um ouvinte válido.
     */
    public void removeListenerDeletedFiles(ValueEventListener valueEventListener){
        referenceDeletedFiles.removeEventListener(valueEventListener);
    }
}
