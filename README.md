# <img src="/Android/app/src/main/res/mipmap-xhdpi/ic_launcher.png" width="24"/> Cloud Vision
Capturar blocos de texto utilizando a câmera fotográfica do smartphone que serão facilmente salvos, compartilhados ou transferidos para a área de transferência do dispositivo.  

## Funcionalidades
- Capturar e salvar bloco de texto "Note Vision" através da câmera fotográfica; 
- Compartilhar o Note Vision através de outros aplicativos;
- Enviar o Note Vision para a área de transferência do dispositivo;
- Personalizar o Note Vision com imagem de plano de fundo.

## Demonstração
[![Demonstração](http://img.youtube.com/vi/QrG-vEyAgKQ/0.jpg)](http://www.youtube.com/watch?v=QrG-vEyAgKQ)

## Instalação do aplicativo 
- Android - [baixar](https://raw.githubusercontent.com/brolam/Capstone-Project/master/CloudVision.apk) e executar o apk em um smartphone ou tablet. 
## Configurando o ambiente de desenvolvimento
- Realizar o download do projeto https://github.com/brolam/Capstone-Project.git ;
- Criar um aplicativo no [Firebase Console](https://console.firebase.google.com/);
- Criar e copiar o arquivo google-services.json para o endereço Capstone-Project/Android/app/. [Acesse a documentação do Firebase para mais detalhes sobre esse procedimento](https://firebase.google.com/docs/android/setup#manually_add_firebase); 
- Configurar as regras de segurança do Realtime Database:
```
{
  "rules": {
    "notesVision": {
      "$user_id": {
        // grants write access to the owner of this user account
        // whose uid must exactly match the key ($user_id)
        ".read": "$user_id === auth.uid",
        ".write": "$user_id === auth.uid"
      }
    },
    
    "notesVisionItems": {
      "$user_id": {
        // grants write access to the owner of this user account
        // whose uid must exactly match the key ($user_id)
        ".read": "$user_id === auth.uid",
        ".write": "$user_id === auth.uid"
      }
    },
      
    "deletedFiles": {
      "$user_id": {
        // grants write access to the owner of this user account
        // whose uid must exactly match the key ($user_id)
        ".read": "$user_id === auth.uid",
        ".write": "$user_id === auth.uid"
      }
    }    
  
  }
}
```
- Configurar as regras de acesso ao Firebase Storage
```
service firebase.storage {
  match /b/{bucket}/o {
    // grants write access to the owner of this user account
    // whose uid must exactly match the key (userId)
    match /images/notes_vision/{userId}/{allPaths=**}{
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId;
    }
  }
}
```
## Contribuicões 
Cloud Vision é um projeto acadêmico desenvolvido no curso [Nanodegree Desenvolvedor Android](https://br.udacity.com/course/android-developer-nanodegree--nd801/) da [Udacity](https://br.udacity.com/) com o objetivo de desenvolver o conhecimento técnico nos assuntos abaixo:
- Desenvolvimento de aplicativo para a plataforma Android;
- Google Material Desing;
- Mobile Vision;
- Firebase Authentication, Realtime Database, Storage e Analytics.

Sendo assim, fique a vontade para contribuir com o seu conhecimento nos assuntos supracitados e estou disponível para dúvidas e sugestões. 

## O futuro
Esse aplicativo será melhorado na minha próxima especialização na área de Inteligência Artificial, onde pretendo adquirir conhecimento para melhorar a captura e análise de informações desse aplicativo. 

## Links importantes
 - [Nanodegree Desenvolvedor Android](https://br.udacity.com/course/android-developer-nanodegree--nd801/);
 - [Material Design](https://material.io/guidelines/);
 - [Mobile Vision](https://developers.google.com/vision/);
 - [Firebase](https://firebase.google.com/).

## Licença

[Licensed to the Apache Software Foundation (ASF)](http://www.apache.org/licenses/LICENSE-2.0)
