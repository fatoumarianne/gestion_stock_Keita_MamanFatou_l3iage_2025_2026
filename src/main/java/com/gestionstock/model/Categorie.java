package com.gestionstock.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 *@OneToMany: une Categorie pour plusieurs Produit
 *
 * mappedBy = "categorie": indique que la relation est déjà gérée du côté Produit(par
 * l'attribut categorie de la classe Produit).
 *
 * cascade = CascadeType.ALL: les opérations JPA(persit, update, delete) sur la Categorie se propagent
 * automatiquent à ses Produit
 */
@Entity
@Table(name = "categories")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nom;

    private String description;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private List<Produit> produits = new ArrayList<>();

    public Categorie() {
    }

    public Categorie(String description, String nom) {
        this.description = description;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
