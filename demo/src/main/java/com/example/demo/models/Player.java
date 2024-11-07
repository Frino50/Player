package com.example.demo.models;

import java.util.UUID;

public class Player {
    private String id;
    private Position position;

    // Constructeurs, getters et setters
    public Player() {
    }

    public Player(int positionX, int positionY) {
        this.id = UUID.randomUUID().toString();
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
}
