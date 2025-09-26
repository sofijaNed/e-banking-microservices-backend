/*!40101 SET NAMES utf8mb4 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `accountdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `accountdb`;

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `account_number` varchar(50) NOT NULL,
                           `type` varchar(20) DEFAULT NULL,
                           `iban` varchar(34) DEFAULT NULL,
                           `balance` decimal(19,4) NOT NULL DEFAULT 0.0000,
                           `available_balance` decimal(19,4) NOT NULL DEFAULT 0.0000,
                           `currency` varchar(10) NOT NULL,
                           `opened` date DEFAULT NULL,
                           `client_id` bigint DEFAULT NULL,
                           `version` bigint DEFAULT 0,
                           `created_at` timestamp NULL DEFAULT NULL,
                           `updated_at` timestamp NULL DEFAULT NULL,
                           `created_by` varchar(200) DEFAULT NULL,
                           `updated_by` varchar(200) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `account_number` (`account_number`),
                           KEY `idx_account_client` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- accounts; client_id povlačimo iz userdb.client
INSERT INTO `account`
(`account_number`,`type`,`iban`,`balance`,`available_balance`,`currency`,`opened`,`client_id`,`version`,`created_at`,`updated_at`,`created_by`,`updated_by`)
VALUES
    ('RS35123456789012345681','CHECKING','RS35123456789012345681',120000.00,120000.00,'RSD',CURDATE(),
     (SELECT id FROM userdb.client WHERE username='klijenta'),1,NOW(),NOW(),'seed','seed'),
    ('RS35123456789012345682','CHECKING','RS35123456789012345682', 20000.00, 20000.00,'RSD',CURDATE(),
     (SELECT id FROM userdb.client WHERE username='klijentb'),1,NOW(),NOW(),'seed','seed'),
    ('RS35123456789012345683','CHECKING','RS35123456789012345683', 50000.00, 50000.00,'RSD',CURDATE(),
     (SELECT id FROM userdb.client WHERE username='klijentc'),1,NOW(),NOW(),'seed','seed'),
    ('RS35123456789012345684','CHECKING','RS35123456789012345684',  1000.00,  1000.00,'RSD',CURDATE(),
     (SELECT id FROM userdb.client WHERE username='klijentd'),1,NOW(),NOW(),'seed','seed'),
    ('999-0000000001','BANK','999-0000000001',100000000.00,100000000.00,'RSD',CURDATE(),NULL,1,NOW(),NOW(),'seed','seed');
