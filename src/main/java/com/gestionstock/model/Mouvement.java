package com.gestionstock.model;

import com.gestionstock.model.enums.TypeMouvement;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * @Enumerated(EnumType.STRING): Stocke la valeur textuelle ("ENTRE", "SORTIE") en base plutôt que
 * l'ordinal, ce qui reste lisible et robuste si l'ordre de l'enum change plus tard
 */
@Entity
@Table(name = "mouvements")
public class Mouvement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement type;

    @Column(nullable = false)
    private int quantite;

    private String motif;

    @Column(name = "date_mouvement")
    private LocalDateTime dateMouvement;

    public Mouvement() {
    }

    public Mouvement(Produit produit, TypeMouvement type, int quantite, String motif, LocalDateTime dateMouvement) {
        this.produit = produit;
        this.type = type;
        this.quantite = quantite;
        this.motif = motif;
        this.dateMouvement = dateMouvement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public TypeMouvement getType() {
        return type;
    }

    public void setType(TypeMouvement type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }
}
