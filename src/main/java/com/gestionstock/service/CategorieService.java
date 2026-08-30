package com.gestionstock.service;

import com.gestionstock.model.Categorie;
import java.util.List;
import java.util.Optional;

public interface CategorieService {
    List<Categorie> findAll();
    Optional<Categorie> findById(int id);
    void add(Categorie categorie);
    void update(Categorie categorie);
    void delete(int id);
    long countProduits(int categorieId);
}