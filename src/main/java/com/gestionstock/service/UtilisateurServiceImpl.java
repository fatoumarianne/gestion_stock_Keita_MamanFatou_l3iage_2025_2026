package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class UtilisateurServiceImpl implements UtilisateurService {

    @Override
    public Optional<Utilisateur> authentifier(String email, String motDePasseClair) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            List<Utilisateur> resultats = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email",
                    Utilisateur.class
            ).setParameter("email", email).getResultList();

            if (resultats.isEmpty()) {
                return Optional.empty(); // email inconnu
            }

            Utilisateur utilisateur = resultats.get(0);

            if (!utilisateur.isActif()) {
                return Optional.empty(); // compte désactivé
            }

            boolean motDePasseValide = BCrypt.checkpw(motDePasseClair, utilisateur.getMotDePasseHash());
            return motDePasseValide ? Optional.of(utilisateur) : Optional.empty();
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT u FROM Utilisateur u ORDER BY u.nom", Utilisateur.class)
                    .getResultList();
        }
    }

    @Override
    public void addUtilisateur(Utilisateur u, String motDePasseClair) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            u.setMotDePasseHash(BCrypt.hashpw(motDePasseClair, BCrypt.gensalt()));
            em.persist(u);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la création de l'utilisateur");
        } finally {
            em.close();
        }
    }

    @Override
    public void setActif(Long id, boolean actif) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Utilisateur u = em.find(Utilisateur.class, id);
            if (u != null) u.setActif(actif);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la mise à jour du compte");
        } finally {
            em.close();
        }
    }
}