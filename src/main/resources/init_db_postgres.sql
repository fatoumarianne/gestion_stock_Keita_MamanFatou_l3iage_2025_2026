--- ==============================================================================================
--SCRIPT D'INITIALISATION DE LA BASE DE DONNEES GESTIONSTOCKIAGE
--SGBD: POSTGRESQL
-- Differences avec MYSQL:
    --AUTO_INCREMENT -> SERIAL
    -- ENUM -> VARCHAR + CHECK
    -- DECIMAL -> NUMERIC
    -- DATETIME -> TIMESTAMP
    -- INT -> INTEGER
--- ==============================================================================================

CREATE DATABASE gestion_stock_iage
       WITH
       OWNER = postgres
       ENCODING = 'UTF8'
       TEMPLATE = template0;

-- Table categories
CREATE TABLE IF NOT EXISTS categories(
                                         id  SERIAL PRIMARY KEY,
                                         nom VARCHAR(100) NOT NULL,
    description TEXT
    );

-- Table fournisseurs
CREATE TABLE IF NOT EXISTS fournisseurs(
                                           id  SERIAL PRIMARY KEY,
                                           nom VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    tel VARCHAR(20)
    );

-- Table produits
CREATE TABLE IF NOT EXISTS produits(
                                       id  SERIAL PRIMARY KEY,
                                       nom VARCHAR(150) NOT NULL,
    prix DECIMAL(12, 2) NOT NULL,
    quantite_stock INTEGER NOT NULL DEFAULT 0,
    quantite_min INTEGER NOT NULL DEFAULT 5,
    categorie_id INTEGER,
    fournisseur_id INTEGER,
    FOREIGN KEY (categorie_id) REFERENCES categories(id),
    FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id)
    );

-- Table mouvements de stock
CREATE TABLE IF NOT EXISTS mouvements(
                                         id  SERIAL PRIMARY KEY,
                                         type VARCHAR(8) NOT NULL CHECK (type IN ('ENTRE', 'SORTIE')),
    quantite INTEGER NOT NULL,
    date_mouvement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motif VARCHAR(255),
    produit_id INTEGER,
    FOREIGN KEY (produit_id) REFERENCES produits(id)
    );


INSERT INTO categories(nom, description) VALUES
                                             ('Informatique', 'Materiel et accessoires informatiques'),
                                             ('Mobilier', 'Bureau, chaises et rangements'),
                                             ('Fournitures', 'Papeterie et consommables');


INSERT INTO fournisseurs(nom, email, tel) VALUES
                                              ('TechPro SARL', 'contact@techpro.sn', '+221 77 100 00 01'),
                                              ('MeubleAfrik', 'contact@meubleafrik.sn', '+221 77 200 00 01');





INSERT INTO produits(nom, prix, quantite_stock, quantite_min, categorie_id, fournisseur_id) VALUES
                                                                                                ('Ordinateur Portable', 550000.0, 15, 3, 1, 1),
                                                                                                ('Bureau en bois', 87000.0, 8, 2, 2, 2);
CREATE TABLE utilisateurs (
                              id BIGSERIAL PRIMARY KEY,
                              email VARCHAR(150) NOT NULL UNIQUE,
                              nom VARCHAR(100) NOT NULL,
                              mot_de_passe_hash VARCHAR(255) NOT NULL,
                              role VARCHAR(20) NOT NULL,
                              date_creation DATE,
                              actif BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO utilisateurs (email, nom, mot_de_passe_hash, role, date_creation, actif) VALUES
                                                                                         ('admin@gestionstock.com', 'Admin Principal', '$2b$10$C16U9fwHGMwWtKVKblgagupCzI.Z.rGLL3aDUmmyGLnpEIXwpNycK', 'ADMIN', CURRENT_DATE, TRUE),
                                                                                         ('gestionnaire@gestionstock.com', 'Gestionnaire Test', '$2b$10$wsFzUEP7b9Q2/g3DeHJObe0Ll.luVt8zsi/yzwuADwNWyO54jcNFa', 'GESTIONNAIRE', CURRENT_DATE, TRUE);

-- Quelques mouvements de test pour peupler le dashboard et les statistiques
-- (produit_id 1 et 2 = les 2 premiers produits insérés plus haut dans ce script ;
--  utilisateur_id 1 = admin, 2 = gestionnaire, vu l'ordre d'insertion ci-dessus)
INSERT INTO mouvements (produit_id, type, quantite, motif, date_mouvement, utilisateur_id) VALUES
                                                                                               (1, 'ENTREE', 20, 'Réapprovisionnement initial', CURRENT_TIMESTAMP, 1),
                                                                                               (1, 'SORTIE', 5, 'Vente', CURRENT_TIMESTAMP, 2),
                                                                                               (2, 'ENTREE', 15, 'Réapprovisionnement initial', CURRENT_TIMESTAMP, 1);