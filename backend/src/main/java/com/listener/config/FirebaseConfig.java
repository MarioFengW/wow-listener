package com.listener.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct  // Se ejecuta automáticamente al iniciar Spring Boot
    public void initializeFirebase() throws IOException {

        // Solo inicializa si no hay una instancia previa
        if (FirebaseApp.getApps().isEmpty()) {

            InputStream serviceAccount = getClass()
                .getClassLoader()
                .getResourceAsStream("pokemon-a1fcf-firebase-adminsdk-fbsvc-2f3af916ba.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado correctamente");
        }
    }
}