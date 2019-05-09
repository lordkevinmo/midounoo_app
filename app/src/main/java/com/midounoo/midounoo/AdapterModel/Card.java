package com.midounoo.midounoo.AdapterModel;

/**
 * Cette classe est le modèle sur lequel l'ajout de carte de paiement
 * se base. Nous avons pour attribut une image de type int qui représente
 * le logo de l'organisme (Visa, Paypal, ..) et le nom de l'organisme.
 *
 */
public class Card {
    private int cardIcone;
    private String cardLabel;

    public Card() {
    }

    public Card(int cardIcone, String cardLabel) {
        this.cardIcone = cardIcone;
        this.cardLabel = cardLabel;
    }

    public String getCardLabel() {
        return cardLabel;
    }

    public void setCardLabel(String cardLabel) {
        this.cardLabel = cardLabel;
    }

    public int getCardIcone() {
        return cardIcone;
    }

    public void setCardIcone(int cardIcone) {
        this.cardIcone = cardIcone;
    }
}
