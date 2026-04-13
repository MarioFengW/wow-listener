package com.listener.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.listener.model.Pokemon;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PokemonService {

    private final PokemonEventService pokemonEventService;

    public PokemonService(PokemonEventService pokemonEventService) {
        this.pokemonEventService = pokemonEventService;
    }

    @PostConstruct
    public void startPokemonListener() {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection("pokemon");

        collection.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                pokemonEventService.broadcastError("Error escuchando cambios: " + error.getMessage());
                return;
            }

            if (snapshot == null) {
                return;
            }

            List<Pokemon> pokemon = snapshot.getDocuments().stream().map(doc -> {
                Pokemon p = doc.toObject(Pokemon.class);
                p.setId(doc.getId());
                return p;
            }).toList();

            pokemonEventService.broadcastPokemonUpdated(pokemon);
        });
    }

    public List<Pokemon> getAllPokemon() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        QuerySnapshot querySnapshot = db
            .collection("pokemon")
            .get()
            .get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        List<Pokemon> lista = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            Pokemon p = doc.toObject(Pokemon.class);
            p.setId(doc.getId());
            lista.add(p);
        }

        return lista;
    }

    public Pokemon applyDamage(String pokemonId, int damage) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference document = db.collection("pokemon").document(pokemonId);

        Pokemon existing = document.get().get().toObject(Pokemon.class);
        if (existing == null) {
            throw new IllegalArgumentException("Pokemon no encontrado: " + pokemonId);
        }

        int newLife = Math.max(0, existing.getVida() - Math.max(damage, 0));

        ApiFuture<WriteResult> updateFuture = document.update("vida", newLife);
        updateFuture.get();

        existing.setId(pokemonId);
        existing.setVida(newLife);
        return existing;
    }
}