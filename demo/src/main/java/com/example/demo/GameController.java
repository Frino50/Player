package com.example.demo;

import com.example.demo.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/player")
public class GameController extends TextWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToPseudoMap = new ConcurrentHashMap<>();

    @Autowired
    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/connect")
    public void connect(Map<String, String> message, SimpMessageHeaderAccessor headerAccessor) {
        String pseudo = message.get("pseudo");

        Player newPlayer = new Player(pseudo, 100, 100);
        players.put(pseudo, newPlayer);

        String sessionId = headerAccessor.getSessionId();
        sessionToPseudoMap.put(sessionId, pseudo);

        messagingTemplate.convertAndSend("/topic/connect", players);
        System.out.println("Un nouveau joueur s'est connecté : " + pseudo);
    }

    @MessageMapping("/move")
    public void movePlayer(Player player) {
        players.put(player.getPseudo(), player);
        messagingTemplate.convertAndSend("/topic/movements", players);
    }

    @GetMapping("/getAll")
    public Set<String> getAll() {
        return players.keySet();
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String pseudo = sessionToPseudoMap.get(sessionId);

        if (pseudo != null) {
            players.entrySet().removeIf(entry -> entry.getValue().getPseudo().equals(pseudo));
            sessionToPseudoMap.remove(sessionId);

            messagingTemplate.convertAndSend("/topic/disconnect", pseudo);
            System.out.println("Le joueur " + pseudo + " a quitté.");
        }
    }
}
