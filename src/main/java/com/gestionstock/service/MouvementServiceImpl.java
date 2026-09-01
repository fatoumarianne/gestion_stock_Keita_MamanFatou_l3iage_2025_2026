package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class MouvementServiceImpl implements MouvementService {

    @Override
    public List<Mouvement> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT m FROM Mouvement m ORDER BY m.dateMouvement DESC", Mouvement.class)
                    .getResultList();
        }
    }

    @Override
    public void addMouvement(Mouvement mouvement) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // On recharge le produit dans CET EntityManager pour pouvoir le modifier
            Produit produit = em.find(Produit.class, mouvement.getProduit().getId());
            if (produit == null) {
                throw new IllegalArgumentException("Produit introuvable.");
            }

            if (mouvement.getType() == TypeMouvement.SORTIE) {
                if (mouvement.getQuantite() > produit.getQuantiteStock()) {
                    throw new IllegalStateException(
                            "Stock insuffisant : quantité disponible = " + produit.getQuantiteStock());
                }
                produit.setQuantiteStock(produit.getQuantiteStock() - mouvement.getQuantite());
            } else { // ENTREE
                produit.setQuantiteStock(produit.getQuantiteStock() + mouvement.getQuantite());
            }

            mouvement.setProduit(produit);
            mouvement.setDateMouvement(LocalDateTime.now());
            em.persist(mouvement);

            // Le mouvement ET la mise à jour du stock sont validés ensemble ici
            em.getTransaction().commit();

        } catch (IllegalStateException | IllegalArgumentException e) {
            em.getTransaction().rollback();
            throw e; // message métier à afficher tel quel côté UI
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'enregistrement du mouvement.");
        } finally {
            em.close();
        }
    }
}