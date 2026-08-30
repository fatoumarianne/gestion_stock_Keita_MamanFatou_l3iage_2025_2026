package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddProduitDialogController {
    @FXML private Label labelTitre;
    @FXML private TextField champNom;
    @FXML private ComboBox<Categorie> comboCategorie;
    @FXML private ComboBox<Fournisseur> comboFournisseur;
    @FXML private TextField champPrix;
    @FXML private TextField champPrixPromo;
    @FXML private TextField champQuantiteStock;
    @FXML private TextField champQuantiteMin;
    @FXML private Label labelErreur;
    @FXML private Button boutonEnregistrer;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    // null = mode "ajout" ; non-null = mode "modification"
    private Produit produitAModifier;

    // Rappelé par ProduitController après fermeture pour savoir s'il faut recharger le tableau
    private boolean sauvegardeEffectuee = false;

    @FXML
    public void initialize() {
        comboCategorie.getItems().setAll(categorieService.findAll());
        comboFournisseur.getItems().setAll(fournisseurService.findAll());

        // Affiche le nom au lieu de toString() dans les ComboBox
        comboCategorie.setConverter(convertisseur(Categorie::getNom));
        comboFournisseur.setConverter(convertisseur(Fournisseur::getNom));

        // Validation en temps réel : on revérifie à chaque frappe
        champNom.textProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());
        champPrix.textProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());
        champPrixPromo.textProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());
        champQuantiteStock.textProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());
        champQuantiteMin.textProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());

        validerFormulaire();
    }

    // Petite fabrique de StringConverter générique pour éviter de répéter le code pour Categorie et Fournisseur
    private <T> javafx.util.StringConverter<T> convertisseur(java.util.function.Function<T, String> texte) {
        return new javafx.util.StringConverter<>() {
            @Override public String toString(T objet) { return objet == null ? "" : texte.apply(objet); }
            @Override public T fromString(String chaine) { return null; }
        };
    }

    /** Appelée par ProduitController. Passer null pour un ajout, un Produit existant pour une modification. */
    public void initFormulaire(Produit produit) {
        this.produitAModifier = produit;

        if (produit == null) {
            labelTitre.setText("Nouveau produit");
            return;
        }

        labelTitre.setText("Modifier le produit");
        champNom.setText(produit.getNom());
        champPrix.setText(String.valueOf(produit.getPrix()));
        champPrixPromo.setText(produit.getPrixPromo() == null ? "" : String.valueOf(produit.getPrixPromo()));
        champQuantiteStock.setText(String.valueOf(produit.getQuantiteStock()));
        champQuantiteMin.setText(String.valueOf(produit.getQuantiteMin()));
        comboCategorie.setValue(produit.getCategorie());
        comboFournisseur.setValue(produit.getFournisseur());
    }

    public boolean isSauvegardeEffectuee() {
        return sauvegardeEffectuee;
    }

    private void validerFormulaire() {
        String erreur = calculerErreurValidation();
        labelErreur.setText(erreur == null ? "" : erreur);
        boutonEnregistrer.setDisable(erreur != null);
    }

    // Retourne le message d'erreur, ou null si le formulaire est valide
    private String calculerErreurValidation() {
        if (champNom.getText() == null || champNom.getText().trim().length() < 2) {
            return "Le nom doit contenir au moins 2 caractères.";
        }

        Double prix = parseDouble(champPrix.getText());
        if (prix == null || prix <= 0) {
            return "Le prix doit être un nombre strictement positif.";
        }

        String texteBrutPromo = champPrixPromo.getText();
        if (texteBrutPromo != null && !texteBrutPromo.isBlank()) {
            Double prixPromo = parseDouble(texteBrutPromo);
            if (prixPromo == null || prixPromo <= 0) {
                return "Le prix promo doit être un nombre positif.";
            }
            if (prixPromo >= prix) {
                return "Le prix promo doit être strictement inférieur au prix normal.";
            }
        }

        Integer quantiteStock = parseInt(champQuantiteStock.getText());
        if (quantiteStock == null || quantiteStock < 0) {
            return "La quantité en stock doit être un entier ≥ 0.";
        }

        Integer quantiteMin = parseInt(champQuantiteMin.getText());
        if (quantiteMin == null || quantiteMin < 0) {
            return "La quantité minimum doit être un entier ≥ 0.";
        }

        if (comboCategorie.getValue() == null) {
            return "Veuillez sélectionner une catégorie.";
        }
        if (comboFournisseur.getValue() == null) {
            return "Veuillez sélectionner un fournisseur.";
        }

        return null;
    }

    private Double parseDouble(String texte) {
        try { return texte == null || texte.isBlank() ? null : Double.parseDouble(texte.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Integer parseInt(String texte) {
        try { return texte == null || texte.isBlank() ? null : Integer.parseInt(texte.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    @FXML
    private void enregistrer() {
        if (calculerErreurValidation() != null) return; // sécurité, le bouton est normalement déjà désactivé

        Produit produit = (produitAModifier == null) ? new Produit() : produitAModifier;
        produit.setNom(champNom.getText().trim());
        produit.setPrix(parseDouble(champPrix.getText()));
        String texteBrutPromo = champPrixPromo.getText();
        produit.setPrixPromo(texteBrutPromo == null || texteBrutPromo.isBlank() ? null : parseDouble(texteBrutPromo));
        produit.setQuantiteStock(parseInt(champQuantiteStock.getText()));
        produit.setQuantiteMin(parseInt(champQuantiteMin.getText()));
        produit.setCategorie(comboCategorie.getValue());
        produit.setFournisseur(comboFournisseur.getValue());

        if (produitAModifier == null) {
            produitService.addProduit(produit);
        } else {
            produitService.updateProduit(produit);
        }

        sauvegardeEffectuee = true;
        fermer();
    }

    @FXML
    private void annuler() {
        fermer();
    }

    private void fermer() {
        ((Stage) champNom.getScene().getWindow()).close();
    }
}