package com.example.demo;

import com.example.demo.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class GameController extends TextWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    // Liste des joueurs connectés avec leurs identifiants et leurs informations
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    @Autowired
    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        // Créer un joueur avec des positions par défaut
        Player newPlayer = new Player(100, 100); // Position initiale (100, 100)

        // Ajouter le joueur dans la liste des joueurs connectés
        players.put(newPlayer.getId(), newPlayer);

        // Envoyer la liste complète des joueurs aux clients
        messagingTemplate.convertAndSend("/topic/movements", players);

        System.out.println("Un nouveau joueur s'est connecté : " + newPlayer.getId());
    }

    // Méthode pour gérer la déconnexion d'un joueur
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        String playerId = session.getId();

        // Supprimer le joueur de la liste
        players.remove(playerId);
        System.out.println("Le joueur " + playerId + " a quitté.");
    }

    // Méthode pour déplacer un joueur
    public void movePlayer(Player player) {
        // Mettre à jour la position du joueur
        players.put(player.getId(), player);

        // Envoyer la mise à jour à tous les joueurs
        messagingTemplate.convertAndSend("/topic/movements", players);
    }
}

