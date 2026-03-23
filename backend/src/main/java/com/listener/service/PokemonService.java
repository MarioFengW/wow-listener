package com.listener.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.listener.model.Pokemon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PokemonService {

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
}