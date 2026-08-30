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