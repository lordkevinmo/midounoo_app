package com.midounoo.midounoo.Model;

public class Lieux {

    private int lieuxIcone;
    private String lieuxTitre;
    private String lieuxAdresse;

    public Lieux(){

    }
    //Surcharge du Constructeur avec initialisation
    // des entités
    public Lieux(int lI, String lT, String lA) {
        this.lieuxIcone = lI;
        this.lieuxTitre = lT;
        this.lieuxAdresse = lA;
    }

    //Getters et Setters

    public int getLieuxIcone() {
        return lieuxIcone;
    }

    public String getLieuxTitre() {
        return lieuxTitre;
    }

    public String getLieuxAdresse() {
        return lieuxAdresse;
    }

    public void setLieuxIcone(int lieuxIcone) {
        this.lieuxIcone = lieuxIcone;
    }

    public void setLieuxTitre(String lieuxTitre) {
        this.lieuxTitre = lieuxTitre;
    }

    public void setLieuxAdresse(String lieuxAdresse) {
        this.lieuxAdresse = lieuxAdresse;
    }
}
