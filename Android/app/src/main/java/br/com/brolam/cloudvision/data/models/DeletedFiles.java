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
 * Modelar e validar os campos de um DeletedFiles.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class DeletedFiles {
    //Origem
    public static final String PATH_DELETED_FILES =  "deletedFiles";
    public static final String USER_DELETED_FILES =  "/" + PATH_DELETED_FILES + "/%s/%s";
    //Campos
    public static final String PATH = "path";
    public static final String DELETED = "deleted";

    public static HashMap<String, Object> getNewDeletedFiles(String path, long deleted){
        HashMap<String, Object> result = new HashMap<>();
        result.put(PATH, path);
        result.put(DELETED, deleted);
        return result;
    }

    public static HashMap<String, Object> getDelete(String userId, String deletedFileKey){
        HashMap<String, Object> result = new HashMap<>();
        String deletedFileRootPath = String.format(USER_DELETED_FILES, userId, deletedFileKey);
        result.put(deletedFileRootPath, null);
        return result;
    }

    /**
     * Recupear o caminho do arquivo deletado.
     * @param deletedFiles informar um DeletedFiles válido.
     * @return retornar a null se o campo caminho não for encontrado.
     */
    public static String getPath(HashMap deletedFiles){
        return deletedFiles.containsKey(PATH)? deletedFiles.get(PATH).toString(): null;
    }

}
