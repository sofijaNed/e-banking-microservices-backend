/*!40101 SET NAMES utf8mb4 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `transactiondb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `transactiondb`;

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `sender_account_id` bigint DEFAULT NULL,
                               `receiver_account_id` bigint DEFAULT NULL,
                               `amount` decimal(19,4) NOT NULL,
                               `currency` varchar(10) NOT NULL,
                               `description` varchar(255) DEFAULT NULL,
                               `status` varchar(50) DEFAULT NULL,
                               `reference` varchar(100) DEFAULT NULL,
                               `model` varchar(100) DEFAULT NULL,
                               `number` varchar(100) DEFAULT NULL,
                               `transaction_type` varchar(50) DEFAULT NULL,
                               `created_at` timestamp NULL DEFAULT NULL,
                               `updated_at` timestamp NULL DEFAULT NULL,
                               `created_by` varchar(200) DEFAULT NULL,
                               `updated_by` varchar(200) DEFAULT NULL,
                               `date` timestamp NOT NULL DEFAULT current_timestamp(),
                               PRIMARY KEY (`id`),
                               KEY `idx_tx_sender` (`sender_account_id`),
                               KEY `idx_tx_receiver` (`receiver_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- seed: nema transakcija (čist start)
