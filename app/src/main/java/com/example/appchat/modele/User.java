package com.example.appchat.modele;

public class User {
    private String id;
    private String imageUrl;
    private String Username;
    private String etat;

    public User(){}

    public User(String id, String imageUrl, String username, String etat) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.Username = username;
        this.etat=etat;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return Username;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
