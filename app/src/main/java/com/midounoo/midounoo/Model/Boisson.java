package com.midounoo.midounoo.Model;

public class Boisson {
    private String designation;
    private String etiquette;
    private int prix;

    public Boisson() {
    }

    public Boisson(String designation, String etiquette, int prix) {
        this.designation = designation;
        this.etiquette = etiquette;
        this.prix = prix;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEtiquette() {
        return etiquette;
    }

    public void setEtiquette(String etiquette) {
        this.etiquette = etiquette;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }
}
