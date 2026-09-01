package com.gestionstock.service;

import com.gestionstock.model.Mouvement;

import java.util.List;

public interface MouvementService {
    List<Mouvement> findAll();
    void addMouvement(Mouvement mouvement);
    long countEntreesDuJour();
    long countSortiesDuJour();
}