package com.listener.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PokemonEventService {

    private static final long SSE_TIMEOUT_MS = 0L;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });
        emitter.onError((ex) -> {
            emitter.completeWithError(ex);
            emitters.remove(emitter);
        });

        return emitter;
    }

    public void sendSnapshot(SseEmitter emitter, Object payload) throws IOException {
        emitter.send(SseEmitter.event().name("snapshot").data(payload));
    }

    public void broadcastPokemonUpdated(Object payload) {
        broadcast("pokemon-updated", payload);
    }

    public void broadcastError(String message) {
        broadcast("error", message);
    }

    private void broadcast(String eventName, Object payload) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(payload));
            } catch (IOException ex) {
                emitter.completeWithError(ex);
                emitters.remove(emitter);
            }
        }
    }
}