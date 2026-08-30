package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;

public class CategorieController {
    @FXML private TableView<Categorie> tableCategories;
    @FXML private TableColumn<Categorie, String> colonneNom;
    @FXML private TableColumn<Categorie, String> colonneDescription;
    @FXML private TableColumn<Categorie, String> colonneNbProduits;
    @FXML private TableColumn<Categorie, Void> colonneActions;
    @FXML private TextField champNom;
    @FXML private TextArea champDescription;
    @FXML private Label labelErreur;

    private final CategorieService categorieService = new CategorieServiceImpl();

    // Catégorie actuellement en cours de modification (null = mode "ajout")
    private Categorie categorieEnCoursDeModification;

    @FXML
    public void initialize() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colonneNbProduits.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(categorieService.countProduits(data.getValue().getId()))));

        configurerColonneActions();
        chargerDonnees();
    }

    private void configurerColonneActions() {
        colonneActions.setCellFactory(colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final Button boutonSupprimer = new Button("Supprimer");
            private final HBox conteneur = new HBox(5, boutonModifier, boutonSupprimer);

            {
                boutonModifier.setOnAction(e -> preparerModification(getTableView().getItems().get(getIndex())));
                boutonSupprimer.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                setGraphic(vide ? null : conteneur);
            }
        });
    }

    private void chargerDonnees() {
        List<Categorie> categories = categorieService.findAll();
        ObservableList<Categorie> liste = FXCollections.observableArrayList(categories);
        tableCategories.setItems(liste);
    }

    private void preparerModification(Categorie categorie) {
        categorieEnCoursDeModification = categorie;
        champNom.setText(categorie.getNom());
        champDescription.setText(categorie.getDescription());
        labelErreur.setText("");
    }

    @FXML
    private void enregistrer() {
        String nom = champNom.getText();

        if (nom == null || nom.isBlank()) {
            labelErreur.setText("Le nom est obligatoire.");
            return;
        }

        if (categorieEnCoursDeModification == null) {
            Categorie nouvelleCategorie = new Categorie(champDescription.getText(), nom.trim());
            categorieService.add(nouvelleCategorie);
        } else {
            categorieEnCoursDeModification.setNom(nom.trim());
            categorieEnCoursDeModification.setDescription(champDescription.getText());
            categorieService.update(categorieEnCoursDeModification);
        }

        annuler();
        chargerDonnees();
    }

    @FXML
    private void annuler() {
        categorieEnCoursDeModification = null;
        champNom.clear();
        champDescription.clear();
        labelErreur.setText("");
    }

    private void supprimer(Categorie categorie) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer la catégorie \"" + categorie.getNom() + "\" ?");
        var reponse = confirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            try {
                categorieService.delete(categorie.getId());
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