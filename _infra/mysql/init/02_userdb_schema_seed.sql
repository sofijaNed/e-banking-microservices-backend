/*!40101 SET NAMES utf8mb4 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `userdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `userdb`;

DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `firstname` varchar(100) NOT NULL,
                            `lastname` varchar(100) NOT NULL,
                            `email` varchar(150) NOT NULL,
                            `phone` varchar(50) NOT NULL,
                            `position` varchar(100) DEFAULT NULL,
                            `username` varchar(50) DEFAULT NULL,
                            `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
                            `created_by` varchar(200) DEFAULT NULL,
                            `updated_at` timestamp NULL DEFAULT NULL,
                            `updated_by` varchar(200) DEFAULT NULL,
                            `jmbg` char(13) NOT NULL,
                            `id_card_no` varchar(20) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `idx_employee_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `firstname` varchar(100) NOT NULL,
                          `lastname` varchar(100) NOT NULL,
                          `birthdate` date NOT NULL,
                          `email` varchar(150) NOT NULL,
                          `phone` varchar(50) NOT NULL,
                          `jmbg` char(13) NOT NULL,
                          `id_card_no` varchar(20) DEFAULT NULL,
                          `address` varchar(255) DEFAULT NULL,
                          `username` varchar(50) DEFAULT NULL,
                          `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
                          `created_by` varchar(200) DEFAULT NULL,
                          `updated_at` timestamp NULL DEFAULT NULL,
                          `updated_by` varchar(200) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `idx_client_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- employee
INSERT INTO `employee` (`firstname`,`lastname`,`email`,`phone`,`position`,`username`,`jmbg`,`id_card_no`)
VALUES ('Milan','Nikolić','milan.nikolic@example.com','060555555','Službenik','zaposleni','0101990333333','ID700000001')
    ON DUPLICATE KEY UPDATE email=VALUES(email), phone=VALUES(phone), position=VALUES(position);

-- clients
INSERT INTO `client` (`firstname`,`lastname`,`birthdate`,`email`,`phone`,`jmbg`,`id_card_no`,`address`,`username`)
VALUES
    ('Ana','Ćirić','1995-05-05','ana.ciric@example.com','060111111','0505995777777','ID600000001','Ulica 1, Beograd','klijenta'),
    ('Branko','Živković','1994-04-04','branko.zivkovic@example.com','060222222','0404994666666','ID600000002','Ulica 2, Beograd','klijentb'),
    ('Ceca','Milić','1993-03-03','ceca.milic@example.com','060333333','0303993555555','ID600000003','Ulica 3, Beograd','klijentc'),
    ('Đorđe','Čavić','1992-02-02','djordje.cavic@example.com','060444444','0202992444444','ID600000004','Ulica 4, Beograd','klijentd')
    ON DUPLICATE KEY UPDATE email=VALUES(email), phone=VALUES(phone), address=VALUES(address);
