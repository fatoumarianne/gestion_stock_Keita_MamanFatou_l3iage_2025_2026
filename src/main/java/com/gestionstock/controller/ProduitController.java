package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProduitController {
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colonneNom;
    @FXML private TableColumn<Produit, Double> colonnePrix;
    @FXML private TableColumn<Produit, String> colonnePrixPromo;
    @FXML private TableColumn<Produit, Integer> colonneStock;
    @FXML private TableColumn<Produit, Integer> colonneStockMin;
    @FXML private TableColumn<Produit, String> colonneCategorie;
    @FXML private TableColumn<Produit, String> colonneFournisseur;
    @FXML private TableColumn<Produit, Void> colonneActions;
    @FXML private TextField champRecherche;
    @FXML private ComboBox<Categorie> filtreCategorie;
    @FXML private ComboBox<Fournisseur> filtreFournisseur;
    @FXML private CheckBox caseStockBas;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    // Liste complète chargée depuis la base, utilisée comme référence pour la recherche/les filtres
    private ObservableList<Produit> listeProduits;

    @FXML
    public void initialize() {
        configurerColones();
        configurerColonneActions();
        configurerFiltres();
        chargerDonnees();
    }

    private void configurerColones() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonnePrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colonneStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colonneStockMin.setCellValueFactory(new PropertyValueFactory<>("quantiteMin"));
        colonneCategorie.setCellValueFactory(data -> {
            Categorie cat = data.getValue().getCategorie();
            return new SimpleStringProperty(cat != null ? cat.getNom() : "");
        });
        colonneFournisseur.setCellValueFactory(data -> {
            Fournisseur fournisseur = data.getValue().getFournisseur();
            return new SimpleStringProperty(fournisseur != null ? fournisseur.getNom() : "");
        });
        colonnePrixPromo.setCellValueFactory(data -> {
            Double promo = data.getValue().getPrixPromo();
            return new SimpleStringProperty(promo == null ? "" : String.valueOf(promo));
        });
    }

    private void configurerColonneActions() {
        colonneActions.setCellFactory(colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            { boutonModifier.setOnAction(e -> ouvrirDialogue(getTableView().getItems().get(getIndex()))); }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                setGraphic(vide ? null : boutonModifier);
            }
        });
    }

    private void configurerFiltres() {
        filtreCategorie.getItems().add(null); // "Toutes"
        filtreCategorie.getItems().addAll(categorieService.findAll());
        filtreCategorie.setConverter(convertisseur(c -> c == null ? "Toutes les catégories" : c.getNom()));

        filtreFournisseur.getItems().add(null); // "Tous"
        filtreFournisseur.getItems().addAll(fournisseurService.findAll());
        filtreFournisseur.setConverter(convertisseur(f -> f == null ? "Tous les fournisseurs" : f.getNom()));
    }

    private <T> javafx.util.StringConverter<T> convertisseur(java.util.function.Function<T, String> texte) {
        return new javafx.util.StringConverter<>() {
            @Override public String toString(T objet) { return texte.apply(objet); }
            @Override public T fromString(String chaine) { return null; }
        };
    }

    private void chargerDonnees() {
        List<Produit> produits = produitService.findAllProduits();
        listeProduits = FXCollections.observableArrayList(produits);
        tableProduits.setItems(listeProduits);
    }

    @FXML
    private void appliquerFiltres() {
        String recherche = champRecherche.getText();
        String rechercheMinuscule = (recherche == null) ? "" : recherche.trim().toLowerCase();
        Categorie categorieChoisie = filtreCategorie.getValue();
        Fournisseur fournisseurChoisi = filtreFournisseur.getValue();
        boolean stockBasUniquement = caseStockBas.isSelected();

        ObservableList<Produit> resultats = listeProduits.filtered(produit ->
                (rechercheMinuscule.isBlank() || (produit.getNom() != null && produit.getNom().toLowerCase().contains(rechercheMinuscule)))
                        && (categorieChoisie == null || categorieChoisie.equals(produit.getCategorie()))
                        && (fournisseurChoisi == null || fournisseurChoisi.equals(produit.getFournisseur()))
                        && (!stockBasUniquement || produit.getQuantiteStock() <= produit.getQuantiteMin())
        );

        tableProduits.setItems(resultats);
    }

    @FXML
    private void ouvrirFormulaireAjout() {
        ouvrirDialogue(null);
    }

    private void ouvrirDialogue(Produit produitAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionstock/AddProduitDialog.fxml"));
            Parent racine = loader.load();

            AddProduitDialogController controleur = loader.getController();
            controleur.initFormulaire(produitAModifier);

            Stage fenetreDialogue = new Stage();
            fenetreDialogue.setTitle(produitAModifier == null ? "Nouveau produit" : "Modifier le produit");
            fenetreDialogue.initModality(Modality.APPLICATION_MODAL);
            fenetreDialogue.setScene(new Scene(racine));
            fenetreDialogue.showAndWait();

            if (controleur.isSauvegardeEffectuee()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerProduit() {
        Produit produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();

        if (produitSelectionne == null) {
            Alert alerteInfo = new Alert(Alert.AlertType.INFORMATION);
            alerteInfo.setTitle("Aucune sélection");
            alerteInfo.setHeaderText(null);
            alerteInfo.setContentText("Veuillez sélectionner un produit à supprimer.");
            alerteInfo.showAndWait();
            return;
        }

        Alert alerteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        alerteConfirmation.setTitle("Confirmation de suppression");
        alerteConfirmation.setHeaderText(null);
        alerteConfirmation.setContentText("Voulez-vous vraiment supprimer le produit \"" + produitSelectionne.getNom() + "\" ?");

        Optional<ButtonType> reponse = alerteConfirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            produitService.deleteProduit(produitSelectionne.getId());
            chargerDonnees();
        }
    }
}