package com.example.demo.models;

import java.util.UUID;

public class Player {
    private String id;
    private String pseudo;
    private Position position;

    public Player() {
    }

    public Player(String pseudo, int positionX,  int positionY) {
        this.id = UUID.randomUUID().toString();
        this.pseudo = pseudo;
        this.position = new Position(positionX, positionY);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
