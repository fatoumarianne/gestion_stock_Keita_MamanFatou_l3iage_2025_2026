package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class FournisseurServiceImpl implements FournisseurService {

    @Override
    public List<Fournisseur> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT f FROM Fournisseur f ORDER BY f.nom", Fournisseur.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<Fournisseur> findById(int id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(Fournisseur.class, id));
        }
    }

    @Override
    public void add(Fournisseur fournisseur) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'ajout du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Fournisseur fournisseur) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        if (countProduits(id) > 0) {
            throw new IllegalStateException(
                    "Impossible de supprimer ce fournisseur : des produits y sont encore rattachés.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Fournisseur fournisseur = em.find(Fournisseur.class, id);
            if (fournisseur != null) em.remove(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la suppression du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public long countProduits(int fournisseurId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Produit p WHERE p.fournisseur.id = :id", Long.class)
                    .setParameter("id", fournisseurId)
                    .getSingleResult();
        }
    }
}