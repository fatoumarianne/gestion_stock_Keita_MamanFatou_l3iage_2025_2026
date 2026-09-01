package com.gestionstock.controller;

import com.gestionstock.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StatistiqueController {
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private Label labelValeurStock;
    @FXML private Label labelProduitTop;
    @FXML private Label labelCategorieTop;
    @FXML private Label labelFournisseurTop;
    @FXML private Label labelRuptures;
    @FXML private BarChart<String, Number> graphiqueBarres;
    @FXML private PieChart graphiqueCamembert;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final StatistiqueService statistiqueService = new StatistiqueServiceImpl();

    @FXML
    public void initialize() {
        // Par défaut : les 6 derniers mois
        dateFin.setValue(LocalDate.now());
        dateDebut.setValue(LocalDate.now().minusMonths(6));
        actualiser();
    }

    @FXML
    private void actualiser() {
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();
        if (debut == null || fin == null) return;

        labelValeurStock.setText(String.format("%.0f FCFA", produitService.valeurTotaleStock()));

        Optional<StatistiqueLigne> produitTop = statistiqueService.produitLePlusMouvemente(debut, fin);
        labelProduitTop.setText(produitTop.isPresent()
                ? produitTop.get().getLibelle() + " (" + (int) produitTop.get().getValeur() + ")"
                : "Aucun mouvement");

        Optional<StatistiqueLigne> categorieTop = statistiqueService.categoriePlusForteValeur();
        labelCategorieTop.setText(categorieTop.isPresent()
                ? categorieTop.get().getLibelle() + " (" + (int) categorieTop.get().getValeur() + " FCFA)"
                : "Aucune donnée");

        Optional<StatistiqueLigne> fournisseurTop = statistiqueService.fournisseurPlusDeProduits();
        labelFournisseurTop.setText(fournisseurTop.isPresent()
                ? fournisseurTop.get().getLibelle() + " (" + (int) fournisseurTop.get().getValeur() + " produits)"
                : "Aucune donnée");

        labelRuptures.setText(String.valueOf(statistiqueService.countRupturesEviteesDeJustesse(debut, fin)));

        remplirGraphiqueBarres(debut, fin);
        remplirCamembert();
    }

    private void remplirGraphiqueBarres(LocalDate debut, LocalDate fin) {
        List<StatistiqueMois> donnees = statistiqueService.mouvementsParMois(debut, fin);

        XYChart.Series<String, Number> serieEntrees = new XYChart.Series<>();
        serieEntrees.setName("Entrées");
        XYChart.Series<String, Number> serieSorties = new XYChart.Series<>();
        serieSorties.setName("Sorties");

        for (StatistiqueMois mois : donnees) {
            serieEntrees.getData().add(new XYChart.Data<>(mois.getMois(), mois.getTotalEntrees()));
            serieSorties.getData().add(new XYChart.Data<>(mois.getMois(), mois.getTotalSorties()));
        }

        graphiqueBarres.getData().clear();
        graphiqueBarres.getData().addAll(serieEntrees, serieSorties);
    }

    private void remplirCamembert() {
        List<StatistiqueLigne> donnees = statistiqueService.valeurStockParCategorie();

        graphiqueCamembert.getData().clear();
        for (StatistiqueLigne ligne : donnees) {
            graphiqueCamembert.getData().add(
                    new PieChart.Data(ligne.getLibelle() + " (" + (int) ligne.getValeur() + ")", ligne.getValeur()));
        }
    }
}