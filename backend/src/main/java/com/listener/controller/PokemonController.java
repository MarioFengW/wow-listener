package com.listener.controller;

import com.listener.model.Pokemon;
import com.listener.service.PokemonEventService;
import com.listener.service.PokemonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/pokemon")
@CrossOrigin(origins = "*")
public class PokemonController {

    private final PokemonService pokemonService;
    private final PokemonEventService pokemonEventService;

    public PokemonController(PokemonService pokemonService, PokemonEventService pokemonEventService) {
        this.pokemonService = pokemonService;
        this.pokemonEventService = pokemonEventService;
    }

    @GetMapping
    public ResponseEntity<List<Pokemon>> getAll() {
        try {
            List<Pokemon> pokemon = pokemonService.getAllPokemon();
            return ResponseEntity.ok(pokemon);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        SseEmitter emitter = pokemonEventService.subscribe();

        try {
            List<Pokemon> snapshot = pokemonService.getAllPokemon();
            pokemonEventService.sendSnapshot(emitter, snapshot);
        } catch (ExecutionException | InterruptedException | IOException ex) {
            emitter.completeWithError(ex);
        }

        return emitter;
    }

    @PostMapping("/{id}/damage")
    public ResponseEntity<Pokemon> applyDamage(@PathVariable("id") String id, @RequestBody DamageRequest request) {
        if (request == null || request.damage() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Pokemon updated = pokemonService.applyDamage(id, request.damage());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        } catch (ExecutionException | InterruptedException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public record DamageRequest(int damage) {}
}