package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddMouvementDialogController {
    @FXML private ComboBox<Produit> comboProduit;
    @FXML private RadioButton radioEntree;
    @FXML private RadioButton radioSortie;
    @FXML private TextField champQuantite;
    @FXML private TextField champMotif;
    @FXML private Label labelApercu;
    @FXML private Label labelErreur;
    @FXML private Button boutonEnregistrer;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();
    private boolean sauvegardeEffectuee = false;

    @FXML
    public void initialize() {
        ToggleGroup groupeType = new ToggleGroup();
        radioEntree.setToggleGroup(groupeType);
        radioSortie.setToggleGroup(groupeType);
        radioEntree.setSelected(true);

        comboProduit.getItems().setAll(produitService.findAllProduits());
        comboProduit.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Produit p) { return p == null ? "" : p.getNom() + " (stock: " + p.getQuantiteStock() + ")"; }
            @Override public Produit fromString(String s) { return null; }
        });

        comboProduit.valueProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());
        radioEntree.selectedProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());
        champQuantite.textProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());
        champMotif.textProperty().addListener((obs, ancien, nouveau) -> validerFormulaire());

        validerFormulaire();
    }

    public boolean isSauvegardeEffectuee() {
        return sauvegardeEffectuee;
    }

    private Integer parseQuantite() {
        try {
            return Integer.parseInt(champQuantite.getText().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void validerFormulaire() {
        String erreur = calculerErreur();
        labelErreur.setText(erreur == null ? "" : erreur);
        boutonEnregistrer.setDisable(erreur != null);
        mettreAJourApercu();
    }

    private String calculerErreur() {
        if (comboProduit.getValue() == null) {
            return "Veuillez sélectionner un produit.";
        }
        Integer quantite = parseQuantite();
        if (quantite == null || quantite <= 0) {
            return "La quantité doit être un entier strictement positif.";
        }
        boolean estSortie = radioSortie.isSelected();
        if (estSortie && (champMotif.getText() == null || champMotif.getText().isBlank())) {
            return "Le motif est obligatoire pour une sortie.";
        }
        if (estSortie && quantite > comboProduit.getValue().getQuantiteStock()) {
            return "Stock insuffisant (disponible : " + comboProduit.getValue().getQuantiteStock() + ").";
        }
        return null;
    }

    private void mettreAJourApercu() {
        if (comboProduit.getValue() == null) {
            labelApercu.setText("");
            return;
        }
        Integer quantite = parseQuantite();
        int stockActuel = comboProduit.getValue().getQuantiteStock();
        int quantiteEffective = (quantite == null) ? 0 : quantite;
        int stockResultant = radioSortie.isSelected()
                ? stockActuel - quantiteEffective
                : stockActuel + quantiteEffective;
        labelApercu.setText("Stock actuel : " + stockActuel + " → stock après mouvement : " + stockResultant);
    }

    @FXML
    private void enregistrer() {
        if (calculerErreur() != null) return;

        Mouvement mouvement = new Mouvement();
        mouvement.setProduit(comboProduit.getValue());
        mouvement.setType(radioSortie.isSelected() ? TypeMouvement.SORTIE : TypeMouvement.ENTREE);
        mouvement.setQuantite(parseQuantite());
        mouvement.setMotif(champMotif.getText());
        mouvement.setUtilisateur(SessionUtilisateur.getUtilisateurConnecte());

        try {
            mouvementService.addMouvement(mouvement);
            sauvegardeEffectuee = true;
            fermer();
        } catch (IllegalStateException | IllegalArgumentException e) {
            labelErreur.setText(e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        fermer();
    }

    private void fermer() {
        ((Stage) champQuantite.getScene().getWindow()).close();
    }
}