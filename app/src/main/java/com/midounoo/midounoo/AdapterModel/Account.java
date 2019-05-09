package com.midounoo.midounoo.AdapterModel;

public class Account {

    private String account_titre;
    private int account_icon;

    //Constructeur laisser vide
    public Account() {

    }

    //Constructeur de la classe
    public Account(String account_titre, int account_icon) {
        this.account_titre = account_titre;
        this.account_icon = account_icon;
    }

    //Getters et setters pour la récupération des éléments de la bd
    public int getAccountIcon() {
        return account_icon;
    }

    public String getAccountTitre() {
        return account_titre;
    }

    public void setAccountIcon(int account_icon) {
        this.account_icon = account_icon;
    }

    public void setAccountTitre(String account_titre) {
        this.account_titre = account_titre;
    }
}
