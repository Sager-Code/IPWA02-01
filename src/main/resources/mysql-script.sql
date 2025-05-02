-- Erstellen der Datenbank
CREATE DATABASE IF NOT EXISTS ghost_net_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ghost_net_db;

-- Tabelle für Reporter (meldende Personen)
CREATE TABLE IF NOT EXISTS reporters (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    is_anonymous BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

-- Tabelle für Rescuer (bergende Personen)
CREATE TABLE IF NOT EXISTS rescuers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

-- Tabelle für Geisternetze
CREATE TABLE IF NOT EXISTS ghost_nets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    estimated_size VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    report_date TIMESTAMP NOT NULL,
    last_update_date TIMESTAMP,
    reporter_id BIGINT,
    rescuer_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (reporter_id) REFERENCES reporters(id),
    FOREIGN KEY (rescuer_id) REFERENCES rescuers(id)
);

-- Beispieldaten einfügen (optional)

-- Beispiel-Reporter
INSERT INTO reporters (name, phone_number, is_anonymous) VALUES 
('Max Mustermann', '+49123456789', FALSE),
('Anonym', NULL, TRUE);

-- Beispiel-Rescuer
INSERT INTO rescuers (name, phone_number) VALUES 
('Rettungsteam Nord', '+4987654321'),
('Meereswächter e.V.', '+4912345678');

-- Beispiel-Geisternetze
INSERT INTO ghost_nets (latitude, longitude, estimated_size, status, report_date, reporter_id) VALUES 
(54.12345, 10.98765, '20m²', 'REPORTED', NOW(), 1),
(53.54321, 11.12345, '15m²', 'REPORTED', NOW(), 2);