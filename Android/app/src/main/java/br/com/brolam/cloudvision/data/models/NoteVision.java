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
package br.com.brolam.cloudvision.data.models;

import java.util.Date;
import java.util.HashMap;

/**
 * Modelar e validar os campos de um NoteVision
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVision {

    public static final String PATH_NOTES_VISION = "notesVision";
    public static final String USER_NOTE_VISION = "/" + PATH_NOTES_VISION + "/%s/%s";
    public static final String PATH_NOTE_VISION_BACKGROUND = "images/notes_vision/%s/%s/background.JPEG";

    public static final String TITLE = "title";
    public static final int TITLE_LENGTH = 80;
    public static final String CREATED = "created";
    public static final String UPDATED = "updated";
    public static final String SUMMARY = "summary";
    public static final String BACKGROUND = "background";
    public static final String BACKGROUND_SIGNATURE = "background_signature";
    public static final String PRIORITY = ".priority";

    /**
     * Informar a origem da imagem de background de um Note Vision.
     */
    public enum BackgroundOrigin{
        UNDEFINED, //Não definida.
        LOCAL, // Imagem local, em arquivo temporário.
        REMOTE; //Imagem remota, arquivo na Web.
    }

    public static HashMap<String, Object> getNewNoteVision(String title, String summary, long created) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(TITLE, parseTitle(title));
        result.put(CREATED, created);
        result.put(UPDATED, created);
        result.put(SUMMARY, summary);
        result.put(PRIORITY, Double.valueOf(created));
        return result;
    }

    public static HashMap<String, Object> getUpdateNoteVision(String userId, String noteVisionKey, String title, String summary, long updated) {
        HashMap<String, Object> result = new HashMap<>();
        String noteVisionRootPath = String.format(NoteVision.USER_NOTE_VISION, userId, noteVisionKey);
        String titlePath = noteVisionRootPath + "/" + NoteVision.TITLE;
        String updatedPath = noteVisionRootPath + "/" + NoteVision.UPDATED;
        String summaryPath = noteVisionRootPath + "/" + NoteVision.SUMMARY;
        String priorityPath = noteVisionRootPath + "/" + NoteVision.PRIORITY;
        result.put(titlePath, parseTitle(title));
        result.put(updatedPath, updated);
        result.put(summaryPath, summary);
        result.put(priorityPath, Double.valueOf(updated));

        return result;
    }


    public static HashMap<String, Object> getUpdateNoteVisionSummary(String userId, String noteVisionKey, String summary, long updated) {
        HashMap<String, Object> result = new HashMap<>();
        String noteVisionRootPath = String.format(NoteVision.USER_NOTE_VISION, userId, noteVisionKey);
        String updatedPath = noteVisionRootPath + "/" + NoteVision.UPDATED;
        String summaryPath = noteVisionRootPath + "/" + NoteVision.SUMMARY;
        String priorityPath = noteVisionRootPath + "/" + NoteVision.PRIORITY;
        result.put(updatedPath, updated);
        result.put(summaryPath, summary);
        result.put(priorityPath, Double.valueOf(updated));
        return result;
    }

    public static HashMap<String, Object> getUpdateNoteVisionBackground(String userId, String noteVisionKey, BackgroundOrigin backgroundOrigin) {
        HashMap<String, Object> result = new HashMap<>();
        String noteVisionRootPath = String.format(NoteVision.USER_NOTE_VISION, userId, noteVisionKey);
        String backgroundPath = noteVisionRootPath + "/" + NoteVision.BACKGROUND;
        String backgroundSignature = noteVisionRootPath + "/" + NoteVision.BACKGROUND_SIGNATURE;
        String priorityPath = noteVisionRootPath + "/" + NoteVision.PRIORITY;
        result.put(backgroundPath, backgroundOrigin);
        //Sempre atualizar a assinatura do background quando a origem for
        //alterada para BackgroundOrigin.REMOTE, isso vai atualizar o cache da imagem.
        if (backgroundOrigin == BackgroundOrigin.REMOTE) {
            result.put(backgroundSignature, String.valueOf(new Date().getTime()));
        }
        result.put(priorityPath, Double.valueOf(new Date().getTime()));
        return result;
    }

    public static HashMap<String, Object> getDelete(String userId, String noteVisionKey){
        HashMap<String, Object> result = new HashMap<>();
        String noteVisionRootPath = String.format(USER_NOTE_VISION, userId, noteVisionKey);
        result.put(noteVisionRootPath, null);
        return result;
    }

    public static String parseTitle(String title) {
        if (title != null) {
            //Remover os caracteres que sinaliza o final da linha.
            title = title.replace("\n", "").replace("\r", "");
            int titleLength = title.length();
            return title.substring(0, titleLength <= TITLE_LENGTH ? titleLength : TITLE_LENGTH);
        }
        return "";
    }

    public static boolean checkTitle(String title) {
        return parseTitle(title).isEmpty() == false;
    }

    public static String getTitle(HashMap noteVision){
         return noteVision.containsKey(TITLE)? noteVision.get(TITLE).toString():"";
    }

    public static Long getCreated(HashMap noteVision){
        return noteVision.containsKey(CREATED)? Long.parseLong(noteVision.get(CREATED).toString()): 0;
    }

    public static Long getUpdated(HashMap noteVision){
        return noteVision.containsKey(UPDATED)? Long.parseLong(noteVision.get(UPDATED).toString()): 0;
    }

    public static String getSummary(HashMap noteVision){
        return noteVision.containsKey(SUMMARY)? noteVision.get(SUMMARY).toString():"";
    }

    public static BackgroundOrigin getBackground(HashMap noteVision) {
        if ( noteVision.containsKey(BACKGROUND)){
           return BackgroundOrigin.valueOf(noteVision.get(BACKGROUND).toString());
        }
        return BackgroundOrigin.UNDEFINED;
    }

    public static String getBackgroundSignature(HashMap noteVision){
        return noteVision.containsKey(BACKGROUND_SIGNATURE)? noteVision.get(BACKGROUND_SIGNATURE).toString(): "";

    }

    public static String getBackgroundPath(String userId, String noteVisionKey){
        return String.format(PATH_NOTE_VISION_BACKGROUND, userId, noteVisionKey);
    }
}
