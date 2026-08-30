package com.gestionstock.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fournisseurs")
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String tel;

    public Fournisseur() {
    }

    public Fournisseur(String nom, String email, String tel) {
        this.nom = nom;
        this.email = email;
        this.tel = tel;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
