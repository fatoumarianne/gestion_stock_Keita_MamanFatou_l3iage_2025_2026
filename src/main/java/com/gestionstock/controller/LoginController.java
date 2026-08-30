package com.gestionstock.controller;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.service.UtilisateurService;
import com.gestionstock.service.UtilisateurServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField champEmail;
    @FXML
    private PasswordField champMotDePasse;
    @FXML
    private Label labelErreur;

    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    @FXML
    private void seConnecter() {
        String email = champEmail.getText();
        String motDePasse = champMotDePasse.getText();

        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            labelErreur.setText("Veuillez renseigner email et mot de passe.");
            return;
        }

        Optional<Utilisateur> utilisateur = utilisateurService.authentifier(email, motDePasse);

        if (utilisateur.isEmpty()) {
            labelErreur.setText("Identifiants invalides ou compte inactif.");
            return;
        }

        SessionUtilisateur.connecter(utilisateur.get());
        chargerMenuPrincipal();
    }

    private void chargerMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionstock/main.fxml"));
            Parent racine = loader.load();

            Stage stage = (Stage) champEmail.getScene().getWindow();
            Scene scene = new Scene(racine);
            scene.getStylesheets().add(getClass().getResource("/com/gestionstock/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            labelErreur.setText("Erreur lors du chargement du menu principal.");
        }
    }
}