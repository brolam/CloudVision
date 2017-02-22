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

    public static final String TITLE = "title";
    public static final int TITLE_LENGTH = 80;
    public static final String CREATED = "created";
    public static final String UPDATED = "updated";

    public static HashMap<String, Object> getNewNoteVision(String title, long created) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(TITLE, parseTitle(title));
        result.put(CREATED, created);
        result.put(UPDATED, created);
        return result;
    }

    public static HashMap<String, Object> getUpdateNoteVision(String userId, String noteVisionKey, String title, long updated) {
        HashMap<String, Object> result = new HashMap<>();
        String titlePath = String.format(NoteVision.USER_NOTE_VISION, userId, noteVisionKey) + "/" + NoteVision.TITLE;
        String updatedPath = String.format(NoteVision.USER_NOTE_VISION, userId, noteVisionKey) + "/" + NoteVision.UPDATED;
        result.put(titlePath, parseTitle(title));
        result.put(updatedPath, updated);
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
}
