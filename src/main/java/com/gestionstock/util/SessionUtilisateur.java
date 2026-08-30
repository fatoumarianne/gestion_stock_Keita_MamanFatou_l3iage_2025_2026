package com.gestionstock.util;

import com.gestionstock.model.Utilisateur;

public class SessionUtilisateur {
    private static Utilisateur utilisateurConnecte;

    public static void connecter(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    public static void deconnecter() {
        utilisateurConnecte = null;
    }

    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static boolean estAdmin() {
        return utilisateurConnecte != null
                && utilisateurConnecte.getRole() == com.gestionstock.model.enums.RoleUtilisateur.ADMIN;
    }
}