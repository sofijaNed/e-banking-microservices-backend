CREATE DATABASE IF NOT EXISTS userdb
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE userdb;


CREATE TABLE IF NOT EXISTS client (
  id           BIGINT NOT NULL AUTO_INCREMENT,
  firstname    VARCHAR(100) NOT NULL,
  lastname     VARCHAR(100) NOT NULL,
  birthdate    DATE NOT NULL,
  email        VARCHAR(150) NOT NULL,
  phone        VARCHAR(50) NOT NULL,
  address      VARCHAR(255) DEFAULT NULL,
  username     VARCHAR(50) DEFAULT NULL,
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by   VARCHAR(200) DEFAULT NULL,
  updated_at   TIMESTAMP NULL DEFAULT NULL,
  updated_by   VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_client_username (username)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS employee (
  id           BIGINT NOT NULL AUTO_INCREMENT,
  firstname    VARCHAR(100) NOT NULL,
  lastname     VARCHAR(100) NOT NULL,
  email        VARCHAR(150) NOT NULL,
  phone        VARCHAR(50) NOT NULL,
  POSITION     VARCHAR(100) DEFAULT NULL,
  username     VARCHAR(50) DEFAULT NULL,
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by   VARCHAR(200) DEFAULT NULL,
  updated_at   TIMESTAMP NULL DEFAULT NULL,
  updated_by   VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_employee_username (username)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO employee (id, firstname, lastname, email, phone, POSITION, username, created_at, created_by, updated_at, updated_by) VALUES
(1,'Petar','Petrović','petar.petrovic@example.com','064111222','Loan Manager','employee1','2025-08-10 18:42:39','system','2025-08-10 19:03:26','system'),
(2,'Jelena','Marković','jelena.markovic@example.com','062333444','Account Officer','employee2','2025-08-10 18:42:39','system','2025-08-10 19:03:26','system')
ON DUPLICATE KEY UPDATE
  email=VALUES(email), phone=VALUES(phone), POSITION=VALUES(POSITION),
  updated_at=VALUES(updated_at), updated_by=VALUES(updated_by);

INSERT INTO client (id, firstname, lastname, birthdate, email, phone, address, username, created_at, created_by, updated_at, updated_by) VALUES
(1,'Tara','Paunović','2000-07-16','tara@gmail.com','0652306542','Ulica 1, Beograd','client1','2025-08-10 18:42:39','system','2025-08-10 19:03:26','system'),
(2,'Milan','Ivić','2000-01-01','milan.ivic@example.com','06523014598','Ulica 2, Novi Sad','client2','2025-08-10 18:42:39','system','2025-08-10 19:03:26','system'),
(3,'Aleksandar','Jovanović','1985-11-05','alek.j@example.com','063555111','Adresa 3, Niš','client3','2025-08-10 18:42:39','system','2025-08-10 19:03:26','system')
ON DUPLICATE KEY UPDATE
  email=VALUES(email), phone=VALUES(phone), address=VALUES(address),
  updated_at=VALUES(updated_at), updated_by=VALUES(updated_by);