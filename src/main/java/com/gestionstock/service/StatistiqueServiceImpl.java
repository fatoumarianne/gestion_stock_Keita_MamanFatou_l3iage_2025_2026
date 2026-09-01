package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StatistiqueServiceImpl implements StatistiqueService {

    @Override
    public Optional<StatistiqueLigne> produitLePlusMouvemente(LocalDate debut, LocalDate fin) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Object[]> resultats = em.createQuery(
                            "SELECT m.produit.nom, SUM(m.quantite) " +
                                    "FROM Mouvement m " +
                                    "WHERE m.dateMouvement BETWEEN :debut AND :fin " +
                                    "GROUP BY m.produit.nom " +
                                    "ORDER BY SUM(m.quantite) DESC", Object[].class)
                    .setParameter("debut", debut.atStartOfDay())
                    .setParameter("fin", fin.atTime(23, 59, 59))
                    .setMaxResults(1)
                    .getResultList();

            if (resultats.isEmpty()) return Optional.empty();
            Object[] ligne = resultats.get(0);
            return Optional.of(new StatistiqueLigne((String) ligne[0], ((Number) ligne[1]).doubleValue()));
        }
    }

    @Override
    public Optional<StatistiqueLigne> categoriePlusForteValeur() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Object[]> resultats = em.createQuery(
                            "SELECT p.categorie.nom, SUM(p.quantiteStock * p.prix) " +
                                    "FROM Produit p " +
                                    "WHERE p.categorie IS NOT NULL " +
                                    "GROUP BY p.categorie.nom " +
                                    "ORDER BY SUM(p.quantiteStock * p.prix) DESC", Object[].class)
                    .setMaxResults(1)
                    .getResultList();

            if (resultats.isEmpty()) return Optional.empty();
            Object[] ligne = resultats.get(0);
            return Optional.of(new StatistiqueLigne((String) ligne[0], ((Number) ligne[1]).doubleValue()));
        }
    }

    @Override
    public Optional<StatistiqueLigne> fournisseurPlusDeProduits() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Object[]> resultats = em.createQuery(
                            "SELECT p.fournisseur.nom, COUNT(p) " +
                                    "FROM Produit p " +
                                    "WHERE p.fournisseur IS NOT NULL " +
                                    "GROUP BY p.fournisseur.nom " +
                                    "ORDER BY COUNT(p) DESC", Object[].class)
                    .setMaxResults(1)
                    .getResultList();

            if (resultats.isEmpty()) return Optional.empty();
            Object[] ligne = resultats.get(0);
            return Optional.of(new StatistiqueLigne((String) ligne[0], ((Number) ligne[1]).doubleValue()));
        }
    }

    @Override
    public long countRupturesEviteesDeJustesse(LocalDate debut, LocalDate fin) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(m) FROM Mouvement m " +
                                    "WHERE m.type = :type " +
                                    "AND m.dateMouvement BETWEEN :debut AND :fin " +
                                    "AND m.stockApresMouvement <= m.produit.quantiteMin", Long.class)
                    .setParameter("type", TypeMouvement.SORTIE)
                    .setParameter("debut", debut.atStartOfDay())
                    .setParameter("fin", fin.atTime(23, 59, 59))
                    .getSingleResult();
        }
    }

    @Override
    public List<StatistiqueMois> mouvementsParMois(LocalDate debut, LocalDate fin) {
        // Regroupement fait côté Java (pas en JPQL) pour rester compatible MySQL ET PostgreSQL,
        // qui n'ont pas la même syntaxe pour extraire "année-mois" d'une date.
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Mouvement> mouvements = em.createQuery(
                            "SELECT m FROM Mouvement m WHERE m.dateMouvement BETWEEN :debut AND :fin",
                            Mouvement.class)
                    .setParameter("debut", debut.atStartOfDay())
                    .setParameter("fin", fin.atTime(23, 59, 59))
                    .getResultList();

            Map<YearMonth, long[]> parMois = new TreeMap<>(); // TreeMap = trié par ordre chronologique

            for (Mouvement m : mouvements) {
                YearMonth mois = YearMonth.from(m.getDateMouvement());
                long[] compteurs = parMois.computeIfAbsent(mois, k -> new long[2]); // [entrees, sorties]
                if (m.getType() == TypeMouvement.ENTREE) {
                    compteurs[0] += m.getQuantite();
                } else {
                    compteurs[1] += m.getQuantite();
                }
            }

            DateTimeFormatter formatMois = DateTimeFormatter.ofPattern("MM/yyyy");
            List<StatistiqueMois> resultat = new ArrayList<>();
            for (Map.Entry<YearMonth, long[]> entree : parMois.entrySet()) {
                resultat.add(new StatistiqueMois(
                        entree.getKey().format(formatMois),
                        entree.getValue()[0],
                        entree.getValue()[1]
                ));
            }
            return resultat;
        }
    }

    @Override
    public List<StatistiqueLigne> valeurStockParCategorie() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Object[]> resultats = em.createQuery(
                            "SELECT p.categorie.nom, SUM(p.quantiteStock * p.prix) " +
                                    "FROM Produit p " +
                                    "WHERE p.categorie IS NOT NULL " +
                                    "GROUP BY p.categorie.nom", Object[].class)
                    .getResultList();

            List<StatistiqueLigne> resultat = new ArrayList<>();
            for (Object[] ligne : resultats) {
                resultat.add(new StatistiqueLigne((String) ligne[0], ((Number) ligne[1]).doubleValue()));
            }
            return resultat;
        }
    }
}