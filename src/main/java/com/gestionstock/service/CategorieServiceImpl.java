package com.gestionstock.service;

import com.gestionstock.model.Categorie;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class CategorieServiceImpl implements CategorieService {

    @Override
    public List<Categorie> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Categorie c ORDER BY c.nom", Categorie.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<Categorie> findById(int id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(Categorie.class, id));
        }
    }

    @Override
    public void add(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'ajout de la catégorie");
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification de la catégorie");
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        // Règle métier : impossible de supprimer une catégorie encore rattachée à des produits
        if (countProduits(id) > 0) {
            throw new IllegalStateException(
                    "Impossible de supprimer cette catégorie : des produits y sont encore rattachés.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categorie categorie = em.find(Categorie.class, id);
            if (categorie != null) em.remove(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la suppression de la catégorie");
        } finally {
            em.close();
        }
    }

    @Override
    public long countProduits(int categorieId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Produit p WHERE p.categorie.id = :id", Long.class)
                    .setParameter("id", categorieId)
                    .getSingleResult();
        }
    }
}