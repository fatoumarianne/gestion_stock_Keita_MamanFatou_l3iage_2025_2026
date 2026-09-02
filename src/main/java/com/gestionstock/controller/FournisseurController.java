package com.gestionstock.controller;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;

public class FournisseurController {
    @FXML private TableView<Fournisseur> tableFournisseurs;
    @FXML private TableColumn<Fournisseur, String> colonneNom;
    @FXML private TableColumn<Fournisseur, String> colonneEmail;
    @FXML private TableColumn<Fournisseur, String> colonneTel;
    @FXML private TableColumn<Fournisseur, String> colonneNbProduits;
    @FXML private TableColumn<Fournisseur, Void> colonneActions;
    @FXML private TextField champNom;
    @FXML private TextField champEmail;
    @FXML private TextField champTel;
    @FXML private Label labelErreur;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();
    private Fournisseur fournisseurEnCoursDeModification;

    @FXML
    public void initialize() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colonneTel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colonneNbProduits.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(fournisseurService.countProduits(data.getValue().getId()))));

        configurerColonneActions();
        chargerDonnees();
    }

    private void configurerColonneActions() {
        boolean estAdmin = SessionUtilisateur.estAdmin();

        colonneActions.setCellFactory(colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final Button boutonSupprimer = new Button("Supprimer");
            private final HBox conteneur = new HBox(5, boutonModifier, boutonSupprimer);

            {
                boutonModifier.setOnAction(e -> preparerModification(getTableView().getItems().get(getIndex())));
                boutonSupprimer.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex())));
                boutonSupprimer.setDisable(!estAdmin);
            }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                setGraphic(vide ? null : conteneur);
            }
        });
    }

    private void chargerDonnees() {
        List<Fournisseur> fournisseurs = fournisseurService.findAll();
        ObservableList<Fournisseur> liste = FXCollections.observableArrayList(fournisseurs);
        tableFournisseurs.setItems(liste);
    }

    private void preparerModification(Fournisseur fournisseur) {
        fournisseurEnCoursDeModification = fournisseur;
        champNom.setText(fournisseur.getNom());
        champEmail.setText(fournisseur.getEmail());
        champTel.setText(fournisseur.getTel());
        labelErreur.setText("");
    }

    @FXML
    private void enregistrer() {
        String nom = champNom.getText();

        if (nom == null || nom.isBlank()) {
            labelErreur.setText("Le nom est obligatoire.");
            return;
        }

        if (fournisseurEnCoursDeModification == null) {
            Fournisseur nouveauFournisseur = new Fournisseur(nom.trim(), champEmail.getText(), champTel.getText());
            fournisseurService.add(nouveauFournisseur);
        } else {
            fournisseurEnCoursDeModification.setNom(nom.trim());
            fournisseurEnCoursDeModification.setEmail(champEmail.getText());
            fournisseurEnCoursDeModification.setTel(champTel.getText());
            fournisseurService.update(fournisseurEnCoursDeModification);
        }

        annuler();
        chargerDonnees();
    }

    @FXML
    private void annuler() {
        fournisseurEnCoursDeModification = null;
        champNom.clear();
        champEmail.clear();
        champTel.clear();
        labelErreur.setText("");
    }

    private void supprimer(Fournisseur fournisseur) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer le fournisseur \"" + fournisseur.getNom() + "\" ?");
        var reponse = confirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            try {
                fournisseurService.delete(fournisseur.getId());
                chargerDonnees();
            } catch (IllegalStateException e) {
                Alert erreur = new Alert(Alert.AlertType.ERROR);
                erreur.setHeaderText(null);
                erreur.setContentText(e.getMessage());
                erreur.showAndWait();
            }
        }
    }
}