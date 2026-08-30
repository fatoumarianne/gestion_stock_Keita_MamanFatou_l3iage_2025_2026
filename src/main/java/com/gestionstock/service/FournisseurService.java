package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;
import java.util.List;
import java.util.Optional;

public interface FournisseurService {
    List<Fournisseur> findAll();
    Optional<Fournisseur> findById(int id);
    void add(Fournisseur fournisseur);
    void update(Fournisseur fournisseur);
    void delete(int id);
    long countProduits(int fournisseurId);
}