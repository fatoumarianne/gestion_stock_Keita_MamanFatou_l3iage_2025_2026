package com.gestionstock.controller;

import com.gestionstock.model.Produit;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class DashboardController {
    @FXML private Label labelTotalProduits;
    @FXML private Label labelStockBas;
    @FXML private Label labelValeurStock;
    @FXML private Label labelMouvementsDuJour;
    @FXML private ListView<String> listeStockBas;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();

    @FXML
    public void initialize() {
        labelTotalProduits.setText(String.valueOf(produitService.countTotal()));
        labelStockBas.setText(String.valueOf(produitService.countStockBas()));
        labelValeurStock.setText(String.format("%.0f FCFA", produitService.valeurTotaleStock()));

        long entrees = mouvementService.countEntreesDuJour();
        long sorties = mouvementService.countSortiesDuJour();
        labelMouvementsDuJour.setText(entrees + " entrée(s) / " + sorties + " sortie(s)");

        for (Produit produit : produitService.findByStockBas()) {
            listeStockBas.getItems().add(
                    produit.getNom() + " — stock : " + produit.getQuantiteStock()
                            + " (seuil : " + produit.getQuantiteMin() + ")");
        }
        listeStockBas.setItems(FXCollections.observableArrayList(listeStockBas.getItems()));
    }
}