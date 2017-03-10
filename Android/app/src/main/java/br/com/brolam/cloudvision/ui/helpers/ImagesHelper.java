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

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.brolam.cloudvision.R;
import br.com.brolam.cloudvision.data.CloudVisionProvider;
import br.com.brolam.cloudvision.data.models.DeletedFiles;
import br.com.brolam.cloudvision.data.models.NoteVision;

/**
 * Armazenar e recupear imagens no Firebase Storage.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class ImagesHelper implements ValueEventListener {
    private static final String TAG = "ImagesHelper";
    public static final int REQUEST_IMAGE_CAPTURE = 2000;
    //Informar a chave do Note Vision que está solicitando o armazenamento da image
    // {@see takeNoteVisionBackground} e {@see saveNoteVisionBackground}
    private static String requestImageNoteVisionKey = null;
    private Activity activity;
    private CloudVisionProvider cloudVisionProvider;
    private FirebaseStorage firebaseStorage;
    //Armazenar as solicitações de armazenamentos para restourar os uploads
    //se a atividade for reconstruida.
    private static HashMap<String,String> notesVisionUploadsReference;

    private int noteVisionBackgroundWidth;
    private int noteVisionBackgroundHeight;

    public ImagesHelper(Activity activity, CloudVisionProvider cloudVisionProvider ){
        this.activity = activity;
        Resources resources = activity.getResources();
        this.noteVisionBackgroundWidth = resources.getInteger(R.integer.note_vision_background_width);
        this.noteVisionBackgroundHeight = resources.getInteger(R.integer.note_vision_background_height);
        this.cloudVisionProvider = cloudVisionProvider;
        this.firebaseStorage = FirebaseStorage.getInstance();
        if ( notesVisionUploadsReference  == null ) {
            notesVisionUploadsReference = new HashMap<>();
        } else {
            restoreStorageReference();
        }
    }

    /**
     * Restaurar os uploads se a atividade for reconstruida.
     */
    private void restoreStorageReference() {
        //Se o saveImageNoteVisonBackground for executado nesse método, significa que a tela foi reconstruida,
        //provavelmente na rotação, dessa forma, é necessário salvar nesse método.
        if (requestImageNoteVisionKey != null) {
            saveImageNoteVisonBackground(requestImageNoteVisionKey);
        }

        for (Map.Entry<String, String> map : this.notesVisionUploadsReference.entrySet()) {
            requestNoteVisionBackgroundPutFile(map.getKey());
        }
    }

    /**
     * Realizar a solicitação do download ou recuperar a imagem do arquivo temporário e exibir
     * em um ImageView para uma imagem de background Note Vision
     * @param noteVisionKey informar uma chave válida.
     * @param noteVision informar um Note Vision válido.
     * @param imageView informar um ImageView válido.
     */
    public void loadNoteVisionBackground(String noteVisionKey, HashMap noteVision, ImageView imageView) {
        //Endereço da imagem no Firebase Storage.
        String pathNoteVisionBackground = NoteVision.getBackgroundPath( this.cloudVisionProvider.getUserId(), noteVisionKey);
        imageView.setImageBitmap(null);
        NoteVision.BackgroundOrigin backgroundOrigin = NoteVision.getBackground(noteVision);

        if (backgroundOrigin == NoteVision.BackgroundOrigin.LOCAL) {
            /**
             * Se o upload da imagem de background ainda não foi realizado, será exibida
             * a imagem do arquivo temporário.
             */
            imageView.setImageURI(getImageUriFile(noteVisionKey));
            //Relizar uma requisição de upload se ainda não existir uma requisição ativia.
            requestNoteVisionBackgroundPutFile(noteVisionKey);

        } else if (backgroundOrigin == NoteVision.BackgroundOrigin.REMOTE) {
            Glide.with(this.activity)
                    .using(new FirebaseImageLoader())
                    .load(this.firebaseStorage.getReference(pathNoteVisionBackground))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    //Quando uma imagem é atualizado, uma nova assinatura deve ser criada
                    //para o Glide também atualizar a imagem em cache.
                    .signature(new StringSignature(NoteVision.getBackgroundSignature(noteVision)))
                    .into(imageView);
        }
    }

    /**
     * Realizar uma requisição de uma foto na camera fotográfica nativa do Android para uma imagem
     * de background Note Vision.
     * @param noteVisionKey informar uma chave válida.
     * @throws IOException
     */
    public void takeNoteVisionBackground(String noteVisionKey) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            //Informar o arquivo temporário onde a imagem será gravada.
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUriFileProvider(noteVisionKey));
            //Informar a chave do Note Vision que será recuperado no método {@see saveNoteVisionBackground}
            requestImageNoteVisionKey = noteVisionKey;
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /*
      Acionar esse método no onActivityResult da atividade para finalizar a confirmação da
      imagem.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == activity.RESULT_OK) {
            if (requestImageNoteVisionKey != null) {
                saveImageNoteVisonBackground(requestImageNoteVisionKey);
            }
        }
    }

    /**
     * Salvar a imagens do Note Vision background.
     * @param noteVisionKey informar uma chave válida.
     */
    private void saveImageNoteVisonBackground(String noteVisionKey) {
        if ( parseImageNoteVisionBackground(noteVisionKey)) {
            //Sinalizar que o Note Vision deve considerado uma imagem temporária.
            cloudVisionProvider.setNoteVisionBackground(noteVisionKey, NoteVision.BackgroundOrigin.LOCAL);
            //Realizar a requisição de upload da imagem.
            requestNoteVisionBackgroundPutFile(noteVisionKey);
        }
        requestImageNoteVisionKey = null;
    }

    /**
     * Realizar uma requisição de upload no Firebase Storage para uma imagem de background Note Vision.
     * @param noteVisionKey informar uma chave válida.
     */
    private void requestNoteVisionBackgroundPutFile(String noteVisionKey) {
        //Endereço da imagem no Firebase Storage.
        String pathNoteVisionBackground = NoteVision.getBackgroundPath( this.cloudVisionProvider.getUserId(), noteVisionKey);

        //Recuperar uma referência para o arquivo no Firebase Storage e verificar se já
        //existe uma requisição ativa ou gerar uma nova requisição.
        StorageReference reference = firebaseStorage.getReference(pathNoteVisionBackground);
        if (reference != null) {
            List<UploadTask> activeUploadTasks = reference.getActiveUploadTasks();
            UploadTask uploadTask = null;
            if (activeUploadTasks.size() > 0) {
                uploadTask = activeUploadTasks.get(0);
            } else {
                uploadTask = reference.putFile(getImageUriFileProvider(noteVisionKey));
            }
            //Adicionar ouvintes para os eventos de falha e sucesso.
            uploadTask.addOnFailureListener(new OnFailureNoteVisionBackground(noteVisionKey));
            uploadTask.addOnSuccessListener(new OnSuccessNoteVisionBackground(noteVisionKey));
            //Atualizar a lista de requisições de upload.
            notesVisionUploadsReference.put(noteVisionKey, pathNoteVisionBackground);
        }
    }

    /**
     * Realizar uma requisição de exclusão no Firebase Storage.
     * @param deletedFileKey informar uma chave válida.
     */
    private void requestDeleteFile(String deletedFileKey, String path) {

        //Recuperar uma referência para o arquivo no Firebase Storage e verificar se já
        //existe uma requisição ativa ou gerar uma nova requisição.
        StorageReference reference = firebaseStorage.getReference(path);
        if (reference != null) {
            List<UploadTask> activeUploadTasks = reference.getActiveUploadTasks();
            Task<Void> taskDelete = null;
            if (activeUploadTasks.size() == 0) {
                taskDelete = reference.delete();
                //Adicionar ouvintes para os eventos de falha e sucesso.
                taskDelete.addOnFailureListener(new OnFailureDeletedFile(deletedFileKey));
                taskDelete.addOnSuccessListener(new OnSuccessDeletedFile(deletedFileKey));
            }

        }
    }

    /**
     * Monitorar os registros de arquivos deletados e solicitar a exclusão no Firebase Storage.
     * @param dataSnapshot informar uma lista de {@link DeletedFiles}
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot deletedFile : dataSnapshot.getChildren()) {
            HashMap deletedFileValues = (HashMap) deletedFile.getValue();
            String path = DeletedFiles.getPath(deletedFileValues);
            if (path != null) {
                requestDeleteFile(deletedFile.getKey(), path);
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    /**
     * Ouvinte para o evento de falha no upload de uma imagem de background Note Vision
     */
    private class OnFailureNoteVisionBackground implements OnFailureListener{
        String noteVisionKey;
        public OnFailureNoteVisionBackground(String noteVisionKey){
            this.noteVisionKey = noteVisionKey;
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            notesVisionUploadsReference.remove(this.noteVisionKey);
        }
    }

    /**
     * Ouvinte para o evento de falha na exclusão de um arquivo
     */
    private class OnFailureDeletedFile implements OnFailureListener{
        String deletedFileKey;
        public OnFailureDeletedFile(String deletedFileKey){
            this.deletedFileKey = deletedFileKey;
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            if ( e.getMessage().indexOf("Object does not exist") > -1 ){
                cloudVisionProvider.deleteDeletedFile(deletedFileKey);
            }
        }
    }

    /**
     * Ouvinte para o evento de sucesso no upload de uma imagem de background Note Vision
     */
    private class OnSuccessNoteVisionBackground implements OnSuccessListener<UploadTask.TaskSnapshot>{
        String noteVisionKey;

        public OnSuccessNoteVisionBackground(String noteVisionKey){
            this.noteVisionKey = noteVisionKey;
        }

        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            if ( deleteImage(getImageUriFile(this.noteVisionKey).getPath())){
                notesVisionUploadsReference.remove(this.noteVisionKey);
                cloudVisionProvider.setNoteVisionBackground(this.noteVisionKey, NoteVision.BackgroundOrigin.REMOTE);
            };
        }
    }

    /**
     * Ouvinte para o evento de sucesso na exclusão no upload de uma imagem de background Note Vision
     */
    private class OnSuccessDeletedFile implements OnSuccessListener<Void>{
        String deletedFileKey;

        public OnSuccessDeletedFile(String deletedFileKey){
            this.deletedFileKey = deletedFileKey;
        }

        @Override
        public void onSuccess(Void aVoid) {
            cloudVisionProvider.deleteDeletedFile(deletedFileKey);
        }
    }

    /**
     * Reduzir o tamanho de uma imagem e atualizar o arquivo da imagem.
     * @param filepath informar o endereço completo da imagem
     * @param width informar a largura em pixel
     * @param height informar a altura em pixels
     * @throws IOException
     */
    private void resizeImage(String filepath, int width,  int height ) throws IOException {
        //Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(filepath), 720, 1280, true);
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(filepath), width, height, true);
        File imageFile = new File(filepath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        outputStream.write(data);
        outputStream.close();
    }

    /**
     * Excluir o arquivo de uma imagem.
     * @param filepath informar o endereço completo da imagem
     * @return Verdadeiro se a exclusão for realizada com sucesso.
     */
    private boolean deleteImage(String filepath){
        File file = new File(filepath);
        return file.delete();
    }

    /**
     * Recuperar o endereço no File Provider conforme a chave de um Note Vision.
     * @param noteVisionKey informar uma chave válida.
     * @return Uri com o endereço da imagem no File Provider.
     */
    private Uri getImageUriFileProvider(String noteVisionKey){
        File imageFile = new File(getImageUriFile(noteVisionKey).getPath());
        return FileProvider.getUriForFile(this.activity, "br.com.brolam.cloudvision.fileprovider", imageFile);
    }

    /**
     * Recuperar o endereço fisico do arquivo de imagem conforme uma chave.
     * @param key informar uma chave válida.
     * @return Uri com o endereço fisico do arquvio da imagem.
     */
    private Uri getImageUriFile(String key){
        File storageDir = this.activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return Uri.parse(String.format("%s/%s.jpg", storageDir, key));
    }

    /**
     * Analisar e validar uma imagem de background para um Note Vision.
     * @param noteVisionKey
     * @return verdadeiro se a imagem for válida.
     */
    private boolean parseImageNoteVisionBackground(String noteVisionKey){
        Uri imageUri = getImageUriFile(noteVisionKey);
        File imageFile = new File(imageUri.getPath());
        if (imageFile.exists()){
            //Reduzir o tamanho de imagens acima de 1MB.
            if (imageFile.getTotalSpace() > 1048576) {
                //Tentar reduzir o tamanho da imagem.
                try {
                    resizeImage(imageUri.getPath(),
                            noteVisionBackgroundWidth,
                            noteVisionBackgroundHeight);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if ( imageFile.getTotalSpace() == 0){
                //Excluir a imagem se a mesma não for válida.
                imageFile.delete();
                return false;
            }
            return true;
        };
        return false;
    }
}
