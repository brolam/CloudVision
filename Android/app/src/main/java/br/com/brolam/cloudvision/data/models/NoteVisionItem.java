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
 * Modelar e validar os campos de um NoteVisionItem.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class NoteVisionItem {

    public static final String PATH_NOTES_VISION_ITEMS =  "notesVisionItems";
    public static final String USER_NOTE_VISION_ITEMS =  "/" + PATH_NOTES_VISION_ITEMS + "/%s/%s/%s";

    public static final String CONTENT = "content";
    public static final String CREATED = "created";
    public static final String PRIORITY = ".priority";

    public static HashMap<String, Object> getNewNoteVisionItem(String content, long created){
        HashMap<String, Object> result = new HashMap<>();
        result.put(CONTENT, content);
        result.put(CREATED, created);
        result.put(PRIORITY, Double.valueOf(created));
        return result;
    }

    public static HashMap<String, Object> getUpdateNoteVisionItem(String userId, String noteVisionKey, String noteVisionItemKey, String content){
        HashMap<String, Object> result = new HashMap<>();
        String noteVisionItemRootPath = String.format(NoteVisionItem.USER_NOTE_VISION_ITEMS,userId,noteVisionKey,noteVisionItemKey);
        String contentPath =  noteVisionItemRootPath + "/" + NoteVisionItem.CONTENT;
        result.put(contentPath, content);
        return result;
    }

    public static String parseContent(String content) {
        if (content != null) {
            return content;
        }
        return "";
    }

    public static boolean checkContent(String content){
        content = parseContent(content).replace("\n", "").replace("\r", "");
        return parseContent(content).isEmpty() == false ;
    }


}
