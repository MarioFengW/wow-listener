package com.listener.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() { // ← sin "throws IOException"
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("pokemon-a1fcf-firebase-adminsdk-fbsvc-2f3af916ba.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("❌ No se encontró el archivo de credenciales de Firebase");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado correctamente");
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ Error al inicializar Firebase: " + e.getMessage(), e);
        }
    }
}