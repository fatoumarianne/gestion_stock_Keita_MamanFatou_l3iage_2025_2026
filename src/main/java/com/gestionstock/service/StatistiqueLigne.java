package com.gestionstock.service;

// Simple porte-résultat pour afficher "un nom + une valeur" (ex: "Boissons" + 45000)
public class StatistiqueLigne {
    private final String libelle;
    private final double valeur;

    public StatistiqueLigne(String libelle, double valeur) {
        this.libelle = libelle;
        this.valeur = valeur;
    }

    public String getLibelle() { return libelle; }
    public double getValeur() { return valeur; }
}