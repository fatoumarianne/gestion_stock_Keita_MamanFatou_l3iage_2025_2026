package com.gestionstock.service;

import com.gestionstock.model.Produit;

import java.util.List;
import java.util.Optional;

public interface ProduitService {
    List<Produit> findAllProduits();
    Optional<Produit> findById(int id);
    List<Produit> findByCategorie(int categorieId);
    void addProduit(Produit p);
    void updateProduit(Produit p);
    void deleteProduit(int id);
    List<Produit> findByStockBas();
}
