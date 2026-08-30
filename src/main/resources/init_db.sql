--- ==============================================================================================
--SCRIPT D'INITIALISATION DE LA BASE DE DONNEES GESTIONSTOCKIAGE
--SGBD: MYSQL
--- ==============================================================================================

CREATE DATABASE IF NOT EXISTS gestion_stock_iage
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_stock_iage;

-- Table categories
CREATE TABLE IF NOT EXISTS categories(
    id  INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT
);

-- Table fournisseurs
CREATE TABLE IF NOT EXISTS fournisseurs(
    id  INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    tel VARCHAR(20)
);

-- Table produits
CREATE TABLE IF NOT EXISTS produits(
    id  INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    prix DECIMAL(12, 2) NOT NULL,
    quantite_stock INT NOT NULL DEFAULT 0,
    quantite_min INT NOT NULL DEFAULT 5,
    categorie_id INT,
    fournisseur_id INT,
    FOREIGN KEY (categorie_id) REFERENCES categories(id),
    FOREIGN KEY (fournisseur_id) REFERENCES fournisseurs(id)
);

-- Table mouvements de stock
CREATE TABLE IF NOT EXISTS mouvements(
    id  INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('ENTRE', 'SORTIE') NOT NULL,
    quantite INT NOT NULL,
    date_mouvement DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motif VARCHAR(255),
    produit_id INT,
    FOREIGN KEY (produit_id) REFERENCES produits(id)
);

--- ==============================================================================================
--DONNEES DE TEST
--- ==============================================================================================

INSERT INTO categories(nom, description) VALUES
 ('Informatique', 'Materiel et accessoires informatiques'),
 ('Mobilier', 'Bureau, chaises et rangements'),
 ('Fournitures', 'Papeterie et consommables');


INSERT INTO fournisseurs(nom, email, tel) VALUES
  ('TechPro SARL', 'contact@techpro.sn', '+221 77 100 00 01'),
  ('MeubleAfrik', 'contact@meubleafrik.sn', '+221 77 200 00 01');

INSERT INTO produits(nom, prix, quantite_stock, quantite_min, categorie_id, fournisseur_id) VALUES
    ("Ordinateur Portable", 550000.0, 15, 3, 1, 1),
    ("Bureau en bois", 87000.0, 8, 2, 2, 2);
