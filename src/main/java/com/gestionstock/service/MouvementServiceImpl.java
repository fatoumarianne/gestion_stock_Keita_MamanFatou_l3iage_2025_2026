package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
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
            } else {
                produit.setQuantiteStock(produit.getQuantiteStock() + mouvement.getQuantite());
            }

            mouvement.setProduit(produit);
            mouvement.setDateMouvement(LocalDateTime.now());
            mouvement.setStockApresMouvement(produit.getQuantiteStock());
            em.persist(mouvement);

            em.getTransaction().commit();

        } catch (IllegalStateException | IllegalArgumentException e) {
            em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'enregistrement du mouvement.");
        } finally {
            em.close();
        }
    }

    @Override
    public long countEntreesDuJour() {
        return countMouvementsDuJour(TypeMouvement.ENTREE);
    }

    @Override
    public long countSortiesDuJour() {
        return countMouvementsDuJour(TypeMouvement.SORTIE);
    }

    private long countMouvementsDuJour(TypeMouvement type) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            LocalDate aujourdHui = LocalDate.now();
            return em.createQuery(
                            "SELECT COUNT(m) FROM Mouvement m WHERE m.type = :type " +
                                    "AND FUNCTION('DATE', m.dateMouvement) = :aujourdHui", Long.class)
                    .setParameter("type", type)
                    .setParameter("aujourdHui", aujourdHui)
                    .getSingleResult();
        }
    }
}