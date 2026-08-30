package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;
import java.util.List;
import java.util.Optional;

public interface UtilisateurService {
    Optional<Utilisateur> authentifier(String email, String motDePasseClair);
    List<Utilisateur> findAll();
    void addUtilisateur(Utilisateur u, String motDePasseClair);
    void setActif(Long id, boolean actif);
}