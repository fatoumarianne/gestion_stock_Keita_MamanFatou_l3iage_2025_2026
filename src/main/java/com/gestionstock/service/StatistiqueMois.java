package com.gestionstock.service;

// Porte-résultat pour le graphique en barres : un mois + son total d'entrées et de sorties
public class StatistiqueMois {
    private final String mois;
    private final long totalEntrees;
    private final long totalSorties;

    public StatistiqueMois(String mois, long totalEntrees, long totalSorties) {
        this.mois = mois;
        this.totalEntrees = totalEntrees;
        this.totalSorties = totalSorties;
    }

    public String getMois() { return mois; }
    public long getTotalEntrees() { return totalEntrees; }
    public long getTotalSorties() { return totalSorties; }
}