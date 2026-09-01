package com.gestionstock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StatistiqueService {
    Optional<StatistiqueLigne> produitLePlusMouvemente(LocalDate debut, LocalDate fin);
    Optional<StatistiqueLigne> categoriePlusForteValeur();
    Optional<StatistiqueLigne> fournisseurPlusDeProduits();
    long countRupturesEviteesDeJustesse(LocalDate debut, LocalDate fin);
    List<StatistiqueMois> mouvementsParMois(LocalDate debut, LocalDate fin);
    List<StatistiqueLigne> valeurStockParCategorie();
}