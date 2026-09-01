package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Utilisateur;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MouvementController {
    @FXML private TableView<Mouvement> tableMouvements;
    @FXML private TableColumn<Mouvement, String> colonneDate;
    @FXML private TableColumn<Mouvement, String> colonneProduit;
    @FXML private TableColumn<Mouvement, TypeMouvement> colonneType;
    @FXML private TableColumn<Mouvement, Integer> colonneQuantite;
    @FXML private TableColumn<Mouvement, String> colonneMotif;
    @FXML private TableColumn<Mouvement, String> colonneUtilisateur;
    @FXML private ComboBox<TypeMouvement> filtreType;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;

    private final MouvementService mouvementService = new MouvementServiceImpl();
    private ObservableList<Mouvement> listeMouvements;
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurerColones();
        filtreType.getItems().add(null); // "Toutes"
        filtreType.getItems().addAll(TypeMouvement.values());
        chargerDonnees();
    }

    private void configurerColones() {
        colonneDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateMouvement().format(FORMAT_DATE)));
        colonneProduit.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProduit().getNom()));
        colonneType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colonneQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colonneMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colonneUtilisateur.setCellValueFactory(data -> {
            Utilisateur u = data.getValue().getUtilisateur();
            return new SimpleStringProperty(u != null ? u.getNom() : "");
        });
    }

    private void chargerDonnees() {
        List<Mouvement> mouvements = mouvementService.findAll();
        listeMouvements = FXCollections.observableArrayList(mouvements);
        tableMouvements.setItems(listeMouvements);
    }

    @FXML
    private void appliquerFiltres() {
        TypeMouvement typeChoisi = filtreType.getValue();
        var debut = dateDebut.getValue();
        var fin = dateFin.getValue();

        ObservableList<Mouvement> resultats = listeMouvements.filtered(mouvement ->
                (typeChoisi == null || typeChoisi == mouvement.getType())
                        && (debut == null || !mouvement.getDateMouvement().toLocalDate().isBefore(debut))
                        && (fin == null || !mouvement.getDateMouvement().toLocalDate().isAfter(fin))
        );

        tableMouvements.setItems(resultats);
    }

    @FXML
    private void ouvrirFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionstock/AddMouvementDialog.fxml"));
            Parent racine = loader.load();

            AddMouvementDialogController controleur = loader.getController();

            Stage fenetreDialogue = new Stage();
            fenetreDialogue.setTitle("Nouveau mouvement");
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
}